package divar.aut.backend.service;

import divar.aut.backend.dto.RatingRequest;
import divar.aut.backend.dto.RatingResponse;
import divar.aut.backend.entity.Ad;
import divar.aut.backend.entity.SellerRating;
import divar.aut.backend.entity.User;
import divar.aut.backend.exception.ApiException;
import divar.aut.backend.repository.AdRepository;
import divar.aut.backend.repository.SellerRatingRepository;
import divar.aut.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerRatingServiceTest {

    @Mock
    private SellerRatingRepository sellerRatingRepository;

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SellerRatingService sellerRatingService;

    private User buyer;
    private User seller;
    private Ad ad;
    private RatingRequest ratingRequest;

    @BeforeEach
    void setUp() {
        buyer = new User("buyer", "pass", "Buyer", "b@test.com", "0911");
        ReflectionTestUtils.setField(buyer, "id", 1L);

        seller = new User("seller", "pass", "Seller", "s@test.com", "0912");
        ReflectionTestUtils.setField(seller, "id", 2L);

        ad = new Ad("item", "desc", 1000.0, null, seller, null, null);
        ReflectionTestUtils.setField(ad, "id", 10L);

        ratingRequest = mock(RatingRequest.class);
        lenient().when(ratingRequest.getScore()).thenReturn(4);
        lenient().when(ratingRequest.getComment()).thenReturn("Great");
    }

    @Test
    void rateSeller_ThrowsException_WhenBuyerIsSeller() {
        Ad ownAd = new Ad("item", "desc", 1000.0, null, buyer, null, null);
        ReflectionTestUtils.setField(ownAd, "id", 11L);

        when(adRepository.findById(11L)).thenReturn(Optional.of(ownAd));

        ApiException exception = assertThrows(ApiException.class, () ->
                sellerRatingService.rateSeller(buyer, 11L, ratingRequest)
        );
        assertTrue(exception.getMessage().contains("You cannot rate yourself"));
    }

    @Test
    void rateSeller_ThrowsException_WhenRatingExists() {
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        when(sellerRatingRepository.existsByBuyerAndSeller(buyer, seller)).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class, () ->
                sellerRatingService.rateSeller(buyer, 10L, ratingRequest)
        );
        assertTrue(exception.getMessage().contains("already rated this seller"));
    }

    @Test
    void rateSeller_SavesAndReturnsResponse_WhenValid() {
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        when(sellerRatingRepository.existsByBuyerAndSeller(buyer, seller)).thenReturn(false);

        RatingResponse response = sellerRatingService.rateSeller(buyer, 10L, ratingRequest);

        verify(sellerRatingRepository, times(1)).save(any(SellerRating.class));
        assertNotNull(response);
    }

    @Test
    void getAverageRating_ReturnsCorrectAverage() {
        SellerRating rating1 = new SellerRating(seller, buyer, ad, 4, "Good");
        SellerRating rating2 = new SellerRating(seller, buyer, ad, 5, "Excellent");

        when(sellerRatingRepository.findBySellerOrderByCreatedAtDesc(seller))
                .thenReturn(Arrays.asList(rating1, rating2));

        double average = sellerRatingService.getAverageRating(seller);

        assertEquals(4.5, average);
    }

    @Test
    void getAverageRating_ReturnsZero_WhenNoRatings() {
        when(sellerRatingRepository.findBySellerOrderByCreatedAtDesc(seller))
                .thenReturn(Collections.emptyList());

        double average = sellerRatingService.getAverageRating(seller);

        assertEquals(0.0, average);
    }

    @Test
    void listRatingsForSellerId_ReturnsList_WhenUserExists() {
        SellerRating rating = new SellerRating(seller, buyer, ad, 5, "Excellent");

        when(userRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(sellerRatingRepository.findBySellerOrderByCreatedAtDesc(seller))
                .thenReturn(Collections.singletonList(rating));

        var responses = sellerRatingService.listRatingsForSellerId(2L);

        assertEquals(1, responses.size());
    }

    @Test
    void listRatingsForSellerId_ThrowsException_WhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () ->
                sellerRatingService.listRatingsForSellerId(99L)
        );
        assertTrue(exception.getMessage().contains("User not found"));
    }
}