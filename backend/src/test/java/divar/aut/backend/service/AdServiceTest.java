package divar.aut.backend.service;

import divar.aut.backend.dto.AdRequest;
import divar.aut.backend.entity.*;
import divar.aut.backend.exception.ApiException;
import divar.aut.backend.repository.AdRepository;
import divar.aut.backend.repository.CategoryRepository;
import divar.aut.backend.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdServiceTest {

    @Mock private AdRepository adRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private CityRepository cityRepository;
    @Mock private ImageStorageService imageStorageService;
    @Mock private SellerRatingService sellerRatingService;
    @Mock private ConversationService conversationService;

    @InjectMocks
    private AdService adService;

    private User owner;
    private User otherUser;
    private Ad ad;
    private Category category;
    private City city;

    @BeforeEach
    void setUp() {
        owner = new User("owner", "pass", "Owner", "owner@test.com", "0911");
        owner.setId(1L);

        otherUser = new User("other", "pass", "Other", "other@test.com", "0912");
        otherUser.setId(2L);

        category = new Category("وسایل شخصی");
        ReflectionTestUtils.setField(category, "id", 100L);

        city = new City("تهران");
        ReflectionTestUtils.setField(city, "id", 200L);

        ad = new Ad("دوچرخه", "توضیحات", 5000.0, ItemCondition.USED, owner, category, city);
        ReflectionTestUtils.setField(ad, "id", 10L);
        ReflectionTestUtils.setField(ad, "status", AdStatus.PENDING_REVIEW);
    }

    @Test
    void createAd_ShouldSaveAdWithPendingReviewStatus() {
        AdRequest request = mock(AdRequest.class);
        when(request.getCategoryId()).thenReturn(100L);
        when(request.getCityId()).thenReturn(200L);
        when(request.getItemCondition()).thenReturn("NEW");
        when(request.getTitle()).thenReturn("موبایل");
        when(request.getDescription()).thenReturn("توضیحات");
        when(request.getPrice()).thenReturn(1000.0);

        when(categoryRepository.findById(100L)).thenReturn(Optional.of(category));
        when(cityRepository.findById(200L)).thenReturn(Optional.of(city));

        when(sellerRatingService.getAverageRating(owner)).thenReturn(0.0);
        when(sellerRatingService.getRatingCount(owner)).thenReturn(0);

        adService.createAd(owner, request);

        ArgumentCaptor<Ad> adCaptor = ArgumentCaptor.forClass(Ad.class);
        verify(adRepository, times(1)).save(adCaptor.capture());

        Ad savedAd = adCaptor.getValue();

        assertEquals(AdStatus.PENDING_REVIEW, savedAd.getStatus(), "آگهی جدید باید با وضعیت در انتظار بررسی ذخیره شود");
        assertEquals("موبایل", savedAd.getTitle());
    }

    @Test
    void updateAd_WhenUserIsNotOwner_ShouldThrowException() {
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        AdRequest request = mock(AdRequest.class);

        ApiException exception = assertThrows(ApiException.class, () -> {
            adService.updateAd(otherUser, 10L, request);
        });

        assertTrue(exception.getMessage().contains("You do not own this advertisement"),
                "کاربر نباید بتواند آگهی دیگران را ویرایش کند");
    }

    @Test
    void approvePendingAd_WhenAdIsPending_ShouldChangeStatusToActive() {
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));

        when(sellerRatingService.getAverageRating(owner)).thenReturn(4.5);
        when(sellerRatingService.getRatingCount(owner)).thenReturn(10);

        adService.approvePendingAd(10L);

        ArgumentCaptor<Ad> adCaptor = ArgumentCaptor.forClass(Ad.class);
        verify(adRepository, times(1)).save(adCaptor.capture());

        Ad savedAd = adCaptor.getValue();

        assertEquals(AdStatus.ACTIVE, savedAd.getStatus(), "پس از تایید مدیر، وضعیت آگهی باید به فعال تغییر کند");
    }
}