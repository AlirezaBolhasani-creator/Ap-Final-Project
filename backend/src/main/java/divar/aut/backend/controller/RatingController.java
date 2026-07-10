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

@RestController
public class RatingController {

    private final SellerRatingService sellerRatingService;

    public RatingController(SellerRatingService sellerRatingService) {
        this.sellerRatingService = sellerRatingService;
    }

    @PostMapping("/ads/{adId}/ratings")
    public ResponseEntity<RatingResponse> rateSeller(@PathVariable Long adId,
                                                     @Valid @RequestBody RatingRequest request,
                                                     @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sellerRatingService.rateSeller(principal.getUser(), adId, request));
    }

    @GetMapping("/api/sellers/{sellerId}/ratings")
    public List<RatingResponse> listRatings(@PathVariable Long sellerId) {
        return sellerRatingService.listRatingsForSellerId(sellerId);
    }
}
