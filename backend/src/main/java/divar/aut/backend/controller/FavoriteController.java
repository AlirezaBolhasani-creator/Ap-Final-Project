package divar.aut.backend.controller;

import divar.aut.backend.dto.AdSummaryResponse;
import divar.aut.backend.security.UserPrincipal;
import divar.aut.backend.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user favorites (saved advertisements).
 * <p>
 * Provides endpoints to list, add, and remove favorites for the currently
 * authenticated user. All endpoints are prefixed with {@code /api/favorites}.
 * </p>
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    /**
     * The service layer for favorite-related business logic.
     */
    private final FavoriteService favoriteService;

    /**
     * Constructs a new FavoriteController with the required service.
     *
     * @param favoriteService the favorite service to be used.
     */
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * Retrieves the list of favorite advertisements for the authenticated user.
     *
     * @param principal the currently authenticated user principal.
     * @return a list of {@link AdSummaryResponse} objects representing the user's favorite ads.
     */
    @GetMapping
    public List<AdSummaryResponse> listFavorites(@AuthenticationPrincipal UserPrincipal principal) {
        return favoriteService.listFavorites(principal.getUser());
    }

    /**
     * Adds an advertisement to the authenticated user's favorites.
     * <p>
     * If the ad is already favorited, the operation may be idempotent (no duplicate entry).
     * On successful addition, returns a {@code 201 Created} status.
     * </p>
     *
     * @param adId      the ID of the advertisement to add.
     * @param principal the currently authenticated user principal.
     * @return an empty response entity with status {@code 201 Created}.
     */
    @PostMapping("/{adId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long adId, @AuthenticationPrincipal UserPrincipal principal) {
        favoriteService.addFavorite(principal.getUser(), adId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Removes an advertisement from the authenticated user's favorites.
     * <p>
     * If the ad is not in the favorites list, the operation is idempotent.
     * Returns {@code 204 No Content} on successful removal.
     * </p>
     *
     * @param adId      the ID of the advertisement to remove.
     * @param principal the currently authenticated user principal.
     * @return an empty response entity with status {@code 204 No Content}.
     */
    @DeleteMapping("/{adId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long adId, @AuthenticationPrincipal UserPrincipal principal) {
        favoriteService.removeFavorite(principal.getUser(), adId);
        return ResponseEntity.noContent().build();
    }
}