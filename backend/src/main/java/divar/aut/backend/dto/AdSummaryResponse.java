package divar.aut.backend.dto;

import divar.aut.backend.entity.Ad;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A lightweight view of an ad, used in list screens (browse, search, my ads,
 * favorites). Contains only essential info for display in a list/card.
 */
public class AdSummaryResponse {

    private final Long id;
    private final String title;
    private final double price;
    private final String itemCondition;
    private final String status;
    private final String cityName;
    private final String categoryName;
    private final LocalDateTime createdAt;
    private final String thumbnailFileName;

    public AdSummaryResponse(Ad ad) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.price = ad.getPrice();
        this.itemCondition = ad.getItemCondition().name();
        this.status = ad.getStatus().name();
        this.cityName = ad.getCity().getName();
        this.categoryName = ad.getCategory().getName();
        this.createdAt = ad.getCreatedAt();

        // Use first image as thumbnail, or null if no images
        List<divar.aut.backend.entity.AdImage> images = ad.getImages();
        this.thumbnailFileName = images.isEmpty() ? null : images.get(0).getFileName();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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

    public String getCityName() {
        return cityName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getThumbnailFileName() {
        return thumbnailFileName;
    }
}
