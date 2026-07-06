package divar.aut.backend.dto;

import divar.aut.backend.entity.Ad;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Full details of a single ad, used in the ad detail page. Includes
 * complete info plus seller rating if available.
 */
public class AdDetailResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final double price;
    private final String itemCondition;
    private final String status;
    private final String categoryName;
    private final String cityName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String rejectionReason;
    private final SellerProfile seller;
    private final List<String> imageFileNames;
    private final double averageRating;
    private final int ratingCount;

    public AdDetailResponse(Ad ad, double averageRating, int ratingCount) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.description = ad.getDescription();
        this.price = ad.getPrice();
        this.itemCondition = ad.getItemCondition().name();
        this.status = ad.getStatus().name();
        this.categoryName = ad.getCategory().getName();
        this.cityName = ad.getCity().getName();
        this.createdAt = ad.getCreatedAt();
        this.updatedAt = ad.getUpdatedAt();
        this.rejectionReason = ad.getRejectionReason();
        this.seller = new SellerProfile(ad.getOwner());
        this.imageFileNames = ad.getImages().stream()
                .map(img -> img.getFileName())
                .collect(Collectors.toList());
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
    }

    // Nested DTO for seller info
    public static class SellerProfile {
        private final Long id;
        private final String username;
        private final String fullname;

        public SellerProfile(divar.aut.backend.entity.User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }

        public Long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getFullname() {
            return fullname;
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getItemCondition() {
        return itemCondition;
    }

    public String getStatus() {
        return status;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCityName() {
        return cityName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public SellerProfile getSeller() {
        return seller;
    }

    public List<String> getImageFileNames() {
        return imageFileNames;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }
}
