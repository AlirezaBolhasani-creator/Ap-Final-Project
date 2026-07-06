package divar.aut.backend.controller;

import divar.aut.backend.dto.AdDetailResponse;
import divar.aut.backend.dto.AdRequest;
import divar.aut.backend.dto.AdSummaryResponse;
import divar.aut.backend.security.UserPrincipal;
import divar.aut.backend.service.AdService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST endpoints for ad CRUD operations, search, and admin moderation.
 * Public endpoints are accessible to all; authenticated endpoints enforce
 * user/owner/admin roles via SecurityConfig. See AdService for the actual
 * business rules and permission checks.
 */
@RestController
@RequestMapping("/ads")
public class AdController {

    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    /**
     * Browse/search active ads. Public endpoint, no auth required.
     * Supports filtering by keyword, category, city, price range, condition, and sorting.
     */
    @GetMapping
    public List<AdSummaryResponse> searchAds(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String condition,
            @RequestParam(defaultValue = "newest") String sortBy) {
        return adService.searchActiveAds(keyword, categoryId, cityId, minPrice, maxPrice, condition, sortBy);
    }

    /**
     * Get a single ad for viewing. Authenticated user can see non-active ads if owner;
     * public (null user) can only see ACTIVE ads.
     */
    @GetMapping("/{id}")
    public AdDetailResponse getAd(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return adService.getAdForViewing(id, principal != null ? principal.getUser() : null);
    }

    /**
     * Create a new ad (post-ad form). Authenticated user only.
     */
    @PostMapping
    public AdDetailResponse createAd(@AuthenticationPrincipal UserPrincipal principal,
                                      @Valid @RequestBody AdRequest request) {
        return adService.createAd(principal.getUser(), request);
    }

    /**
     * Edit an existing ad. Owner only.
     */
    @PutMapping("/{id}")
    public AdDetailResponse updateAd(@PathVariable Long id,
                                      @AuthenticationPrincipal UserPrincipal principal,
                                      @Valid @RequestBody AdRequest request) {
        return adService.updateAd(principal.getUser(), id, request);
    }

    /**
     * Delete an ad. Owner only.
     */
    @DeleteMapping("/{id}")
    public void deleteAd(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        adService.deleteOwnAd(principal.getUser(), id);
    }

    /**
     * Mark an ad as sold. Owner only, only works if ad is ACTIVE.
     */
    @PutMapping("/{id}/mark-as-sold")
    public void markAsSold(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        adService.markAsSold(principal.getUser(), id);
    }

    /**
     * Upload images for an ad. Owner only.
     * Accepts multiple files via multipart/form-data with key "files".
     */
    @PostMapping("/{id}/images")
    public void uploadImages(@PathVariable Long id,
                             @AuthenticationPrincipal UserPrincipal principal,
                             @RequestParam("files") List<MultipartFile> files) {
        adService.addImages(principal.getUser(), id, files);
    }

    /**
     * List all ads owned by the authenticated user (any status).
     */
    @GetMapping("/my-ads")
    public List<AdSummaryResponse> listMyAds(@AuthenticationPrincipal UserPrincipal principal) {
        return adService.listMyAds(principal.getUser());
    }

    // --- Admin-only endpoints (access controlled by SecurityConfig) ---

    /**
     * Admin-only: list pending ads awaiting review.
     */
    @GetMapping("/pending")
    public List<AdSummaryResponse> listPendingAds() {
        // Note: SecurityConfig restricts this to admins only
        // We would need a paginated version; for now return all pending
        return adService.searchActiveAds(null, null, null, null, null, "PENDING_REVIEW", null).stream()
                .filter(ad -> ad.getStatus().equals("PENDING_REVIEW"))
                .toList();
    }

    /**
     * Admin-only: approve a pending ad (move to ACTIVE).
     */
    @PutMapping("/{id}/approve")
    public AdDetailResponse approvePendingAd(@PathVariable Long id) {
        return adService.approvePendingAd(id);
    }

    /**
     * Admin-only: reject a pending ad with a reason.
     */
    @PutMapping("/{id}/reject")
    public AdDetailResponse rejectPendingAd(@PathVariable Long id,
                                            @RequestParam(required = false) String reason) {
        return adService.rejectPendingAd(id, reason != null ? reason : "");
    }
}
