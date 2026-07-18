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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerRatingService {

    private final SellerRatingRepository sellerRatingRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;

    public SellerRatingService(SellerRatingRepository sellerRatingRepository, AdRepository adRepository,
                               UserRepository userRepository) {
        this.sellerRatingRepository = sellerRatingRepository;
        this.adRepository = adRepository;
        this.userRepository = userRepository;
    }

    public RatingResponse rateSeller(User buyer, Long adId, RatingRequest request) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> ApiException.notFound("Advertisement not found"));
        User seller = ad.getOwner();

        if (seller.getId().equals(buyer.getId())) {
            throw ApiException.badRequest("You cannot rate yourself");
        }
        if (sellerRatingRepository.existsByBuyerAndSeller(buyer, seller)) {
            throw ApiException.badRequest("You have already rated this seller");
        }

        SellerRating rating = new SellerRating(seller, buyer, ad, request.getScore(), request.getComment());
        sellerRatingRepository.save(rating);
        return new RatingResponse(rating);
    }

    public double getAverageRating(User seller) {
        return ratingsFor(seller).stream().mapToInt(SellerRating::getScore).average().orElse(0.0);
    }

    public int getRatingCount(User seller) {
        return ratingsFor(seller).size();
    }

    public List<RatingResponse> listRatingsForSeller(User seller) {
        return ratingsFor(seller).stream().map(RatingResponse::new).toList();
    }

    public List<RatingResponse> listRatingsForSellerId(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> ApiException.notFound("User not found"));
        return ratingsFor(seller).stream().map(RatingResponse::new).toList();
    }

    private List<SellerRating> ratingsFor(User seller) {
        return sellerRatingRepository.findBySellerOrderByCreatedAtDesc(seller);
    }
}
