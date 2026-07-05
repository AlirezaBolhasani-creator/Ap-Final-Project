package divar.aut.backend.dto;

import divar.aut.backend.entity.Ad;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdResponse {

    private Long id;
    private String title;
    private BigDecimal price;
    private String location;
    private String condition;
    private String category;
    private String imageUrl;
    private int photoCount;
    private String status;
    private String user_id;
    private LocalDateTime time;

    public AdResponse(Ad ad) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.price = ad.getPrice();
        this.location = ad.getLocation();
        this.condition = ad.getCondition();
        this.category = ad.getCategory();
        this.imageUrl = ad.getImageUrl();
        this.photoCount = ad.getPhotoCount();
        this.status = ad.getStatus();
        this.user_id = ad.getUser_id();
        this.time = ad.getTime();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public String getLocation() { return location; }
    public String getCondition() { return condition; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public int getPhotoCount() { return photoCount; }
    public String getStatus() { return status; }
    public String getUser_id() { return user_id; }
    public LocalDateTime getTime() { return time; }
}
