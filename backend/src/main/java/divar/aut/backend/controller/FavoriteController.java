package divar.aut.backend.controller;

import divar.aut.backend.dto.AdSummaryResponse;
import divar.aut.backend.security.UserPrincipal;
import divar.aut.backend.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<AdSummaryResponse> listFavorites(@AuthenticationPrincipal UserPrincipal principal) {
        return favoriteService.listFavorites(principal.getUser());
    }

    @PostMapping("/{adId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long adId, @AuthenticationPrincipal UserPrincipal principal) {
        favoriteService.addFavorite(principal.getUser(), adId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{adId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long adId, @AuthenticationPrincipal UserPrincipal principal) {
        favoriteService.removeFavorite(principal.getUser(), adId);
        return ResponseEntity.noContent().build();
    }
}
