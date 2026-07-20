package divar.aut.backend.service;

import divar.aut.backend.dto.AdDetailResponse;
import divar.aut.backend.dto.AdRequest;
import divar.aut.backend.dto.AdSummaryResponse;
import divar.aut.backend.entity.Ad;
import divar.aut.backend.entity.AdImage;
import divar.aut.backend.entity.AdStatus;
import divar.aut.backend.entity.Category;
import divar.aut.backend.entity.City;
import divar.aut.backend.entity.ItemCondition;
import divar.aut.backend.entity.User;
import divar.aut.backend.exception.ApiException;
import divar.aut.backend.repository.AdRepository;
import divar.aut.backend.repository.CategoryRepository;
import divar.aut.backend.repository.CityRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Business rules for creating, browsing, editing, and moderating
 * advertisements. Anything about *who is allowed to do what* to an ad lives
 * here, not in the controller - the controller just extracts the
 * authenticated user from the request and delegates.
 */
@Service
public class AdService {

    private final AdRepository adRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final ImageStorageService imageStorageService;
    private final SellerRatingService sellerRatingService;
    private final ConversationService conversationService;

    public AdService(AdRepository adRepository, CategoryRepository categoryRepository,
                     CityRepository cityRepository, ImageStorageService imageStorageService,
                     SellerRatingService sellerRatingService, ConversationService conversationService) {
        this.adRepository = adRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.imageStorageService = imageStorageService;
        this.sellerRatingService = sellerRatingService;
        this.conversationService = conversationService;
    }

    public AdDetailResponse createAd(User owner, AdRequest request) {
        Category category = findCategoryOrThrow(request.getCategoryId());

        if (categoryRepository.existsByParent(category)) {
            throw ApiException.badRequest("You must select a subcategory");
        }

        City city = findCityOrThrow(request.getCityId());
        ItemCondition condition = parseCondition(request.getItemCondition());

        Ad ad = new Ad(request.getTitle(), request.getDescription(), request.getPrice(),
                condition, owner, category, city);
        adRepository.save(ad);

        return toDetailResponse(ad);
    }

    public AdDetailResponse updateAd(User actingUser, Long adId, AdRequest request) {
        Ad ad = findAdOrThrow(adId);
        requireOwner(actingUser, ad);

        Category category = findCategoryOrThrow(request.getCategoryId());

        if (categoryRepository.existsByParent(category)) {
            throw ApiException.badRequest("You must select a subcategory");
        }

        City city = findCityOrThrow(request.getCityId());
        ItemCondition condition = parseCondition(request.getItemCondition());

        ad.applyEdit(request.getTitle(), request.getDescription(), request.getPrice(),
                condition, category, city);
        adRepository.save(ad);

        return toDetailResponse(ad);
    }

    /**
     * Fetches one ad for viewing. Public visitors and other users may only
     * see it if it is ACTIVE; the owner and admins may see it in any status
     * (e.g. the owner checking on a still-pending ad).
     */
    public AdDetailResponse getAdForViewing(Long adId, User viewerOrNull) {
        Ad ad = findAdOrThrow(adId);

        boolean isOwner = viewerOrNull != null && viewerOrNull.getId().equals(ad.getOwner().getId());
        boolean isAdmin = viewerOrNull != null && viewerOrNull.isAdmin();

        if (ad.getStatus() != AdStatus.ACTIVE && !isOwner && !isAdmin) {
            throw ApiException.notFound("Advertisement not found");
        }

        return toDetailResponse(ad);
    }

    /**
     * Advanced search restricted to ACTIVE ads only so nothing pending/rejected/deleted/sold
     * is ever leaked from this method.
     */
    public List<AdSummaryResponse> searchActiveAds(String keyword, Long categoryId, Long cityId,
                                                   Double minPrice, Double maxPrice,
                                                   String conditionText, String sortBy) {
        String normalizedKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        ItemCondition condition = (conditionText == null || conditionText.isBlank()) ?
                null : parseCondition(conditionText);
        Sort sort = resolveSort(sortBy);

        List<Ad> ads = adRepository.searchActiveAds(normalizedKeyword, categoryId, cityId,
                minPrice, maxPrice, condition, sort);

        if ("highest_rating".equals(sortBy)) {
            ads.sort((ad1, ad2) -> {
                double rating1 = sellerRatingService.getAverageRating(ad1.getOwner());
                double rating2 = sellerRatingService.getAverageRating(ad2.getOwner());
                return Double.compare(rating2, rating1);
            });
        }

        return ads.stream().map(AdSummaryResponse::new).toList();
    }

    /**
     * List all ads owned by the authenticated user, regardless of status.
     * The user can see their pending/rejected/sold ads to track them.
     */
    public List<AdSummaryResponse> listMyAds(User owner) {
        return adRepository.findByOwnerOrderByCreatedAtDesc(owner).stream()
                .map(AdSummaryResponse::new)
                .toList();
    }

    public List<AdSummaryResponse> listPendingAds() {
        return adRepository.findByStatusOrderByCreatedAtDesc(AdStatus.PENDING_REVIEW).stream()
                .map(AdSummaryResponse::new)
                .toList();
    }

    /**
     * Owner deletes their own ad (marks as DELETED, not actually removed).
     */
    public void deleteOwnAd(User actingUser, Long adId) {
        Ad ad = findAdOrThrow(adId);
        requireOwner(actingUser, ad);
        ad.markAsDeleted();
        adRepository.save(ad);
    }

    /**
     * Owner marks their ad as SOLD (only works if currently ACTIVE).
     */
    public void markAsSold(User actingUser, Long adId) {
        Ad ad = findAdOrThrow(adId);
        requireOwner(actingUser, ad);

        if (ad.getStatus() != AdStatus.ACTIVE) {
            throw ApiException.badRequest("Only an active ad can be marked as sold");
        }
        ad.markAsSold();
        adRepository.save(ad);
    }

    /**
     * Upload multiple images to an ad. Owner only.
     */
    public void addImages(User actingUser, Long adId, List<MultipartFile> files) {
        Ad ad = findAdOrThrow(adId);
        requireOwner(actingUser, ad);

        if (files == null || files.isEmpty()) {
            throw ApiException.badRequest("No image files provided");
        }

        for (MultipartFile file : files) {
            String fileName = imageStorageService.save(file);
            ad.addImage(new AdImage(ad, fileName));
        }
        // Ad.images has cascade = ALL, so saving the ad also inserts the new AdImage rows
        adRepository.save(ad);
    }

    /**
     * Admin-only: approve a pending ad (moves to ACTIVE).
     */
    public AdDetailResponse approvePendingAd(Long adId) {
        Ad ad = findAdOrThrow(adId);
        if (ad.getStatus() != AdStatus.PENDING_REVIEW) {
            throw ApiException.badRequest("Only a pending ad can be approved");
        }
        ad.approve();
        adRepository.save(ad);
        return toDetailResponse(ad);
    }

    /**
     * Admin-only: reject a pending ad with a reason (moves to REJECTED).
     */
    public AdDetailResponse rejectPendingAd(Long adId, String reason, User admin) {
        Ad ad = findAdOrThrow(adId);
        if (ad.getStatus() != AdStatus.PENDING_REVIEW) {
            throw ApiException.badRequest("Only a pending ad can be rejected");
        }
        ad.reject(reason);
        adRepository.save(ad);
        String content = "آگهی شما با عنوان «" + ad.getTitle() + "» به دلیل «" + reason + "» رد شد.";
        conversationService.sendAdminMessageForRejectedAd(admin, ad, content);
        return toDetailResponse(ad);
    }

    // --- helpers ---

    private void requireOwner(User actingUser, Ad ad) {
        if (!ad.getOwner().getId().equals(actingUser.getId())) {
            throw ApiException.forbidden("You do not own this advertisement");
        }
    }

    private Ad findAdOrThrow(Long adId) {
        return adRepository.findById(adId)
                .orElseThrow(() -> ApiException.notFound("Advertisement not found"));
    }

    private Category findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> ApiException.badRequest("Category not found"));
    }

    private City findCityOrThrow(Long cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> ApiException.badRequest("City not found"));
    }

    private ItemCondition parseCondition(String value) {
        try {
            return ItemCondition.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("itemCondition must be NEW or USED");
        }
    }

    private Sort resolveSort(String sortBy) {
        if (sortBy == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sortBy) {
            case "cheapest" -> Sort.by(Sort.Direction.ASC, "price");
            case "expensive" -> Sort.by(Sort.Direction.DESC, "price");
            case "highest_rating" -> Sort.unsorted();
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    private AdDetailResponse toDetailResponse(Ad ad) {
        double avgRating = sellerRatingService.getAverageRating(ad.getOwner());
        int ratingCount = sellerRatingService.getRatingCount(ad.getOwner());
        return new AdDetailResponse(ad, avgRating, ratingCount,
                sellerRatingService.listRatingsForSeller(ad.getOwner()));
    }
}