package divar.aut.backend.controller;

import divar.aut.backend.dto.RatingRequest;
import divar.aut.backend.dto.RatingResponse;
import divar.aut.backend.security.UserPrincipal;
import divar.aut.backend.service.SellerRatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing seller ratings.
 * <p>
 * Provides endpoints to create, retrieve, and delete ratings for sellers.
 * Endpoints are not all under a common base path; they are explicitly defined
 * with their full paths.
 * </p>
 */
@RestController
public class RatingController {

    /**
     * The service layer for seller rating business logic.
     */
    private final SellerRatingService sellerRatingService;

    /**
     * Constructs a new RatingController with the required service.
     *
     * @param sellerRatingService the seller rating service to be used.
     */
    public RatingController(SellerRatingService sellerRatingService) {
        this.sellerRatingService = sellerRatingService;
    }

    /**
     * Rates a seller based on an advertisement.
     * <p>
     * Accepts a {@link RatingRequest} with rating value and optional comment.
     * The rating is associated with the authenticated user and the ad owner
     * (seller). Returns the created rating details with {@code 201 Created}
     * status.
     * </p>
     *
     * @param adId      the ID of the advertisement that serves as context for the rating.
     * @param request   the rating data (score and comment).
     * @param principal the currently authenticated user (the rater).
     * @return a {@link ResponseEntity} containing the created {@link RatingResponse}
     *         and status {@code 201 Created}.
     */
    @PostMapping("/ads/{adId}/ratings")
    public ResponseEntity<RatingResponse> rateSeller(@PathVariable Long adId,
                                                     @Valid @RequestBody RatingRequest request,
                                                     @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sellerRatingService.rateSeller(principal.getUser(), adId, request));
    }

    /**
     * Retrieves all ratings for a specific seller.
     * <p>
     * Returns a list of {@link RatingResponse} objects for the seller
     * identified by the given ID. This endpoint is publicly accessible
     * (no authentication required).
     * </p>
     *
     * @param sellerId the ID of the seller.
     * @return a list of {@link RatingResponse} objects.
     */
    @GetMapping("/api/sellers/{sellerId}/ratings")
    public List<RatingResponse> listRatings(@PathVariable Long sellerId) {
        return sellerRatingService.listRatingsForSellerId(sellerId);
    }

    /**
     * Deletes a rating by its unique identifier.
     * <p>
     * Removes the specified rating. This operation is typically restricted
     * to administrators or the rating owner, depending on security configuration.
     * Returns {@code 204 No Content} on successful deletion.
     * </p>
     *
     * @param ratingId the ID of the rating to delete.
     * @return an empty response entity with status {@code 204 No Content}.
     */
    @DeleteMapping("/ratings/{ratingId}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long ratingId) {
        sellerRatingService.deleteRating(ratingId);
        return ResponseEntity.noContent().build();
    }
}