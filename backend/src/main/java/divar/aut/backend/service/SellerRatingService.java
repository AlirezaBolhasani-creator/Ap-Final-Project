package divar.aut.backend.service;

import divar.aut.backend.entity.User;
import org.springframework.stereotype.Service;

/**
 * Handles seller rating calculations and retrieval.
 * Stub for now; full implementation comes in Step 13.
 */
@Service
public class SellerRatingService {

    /**
     * Get average rating for a seller. Returns 0 if no ratings exist.
     */
    public double getAverageRating(User seller) {
        // TODO: implement when Step 13 adds SellerRating entity
        return 0.0;
    }

    /**
     * Get count of ratings for a seller.
     */
    public int getRatingCount(User seller) {
        // TODO: implement when Step 13 adds SellerRating entity
        return 0;
    }
}
