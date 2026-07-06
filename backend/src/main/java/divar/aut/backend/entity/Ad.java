package divar.aut.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The core entity of the marketplace: something a user is trying to sell.
 * Every ad starts life as PENDING_REVIEW and only becomes visible to the
 * public once an admin approves it (see AdStatus for the full lifecycle).
 */
@Entity
@Table(name = "ads")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "must not be blank")
    @Size(max = 200, message = "must be at most 200 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "must not be blank")
    @Column(nullable = false, length = 2000)
    private String description;

    @NotNull(message = "must not be null")
    @Column(nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCondition itemCondition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id")
    private City city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdStatus status = AdStatus.PENDING_REVIEW;

    @Column(length = 500)
    private String rejectionReason;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<AdImage> images = new ArrayList<>();

    protected Ad() {
        // required by JPA
    }

    public Ad(String title, String description, double price, ItemCondition itemCondition,
              User owner, Category category, City city) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.itemCondition = itemCondition;
        this.owner = owner;
        this.category = category;
        this.city = city;
        this.status = AdStatus.PENDING_REVIEW;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // --- Lifecycle transitions live here so the rules are in one place ---

    public void approve() {
        this.status = AdStatus.ACTIVE;
        this.rejectionReason = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        this.status = AdStatus.REJECTED;
        this.rejectionReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsSold() {
        if (this.status != AdStatus.ACTIVE) {
            throw new IllegalStateException("Only an ACTIVE ad can be marked as sold");
        }
        this.status = AdStatus.SOLD;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDeleted() {
        this.status = AdStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void applyEdit(String title, String description, double price, ItemCondition itemCondition,
                          Category category, City city) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.itemCondition = itemCondition;
        this.category = category;
        this.city = city;
        this.status = AdStatus.PENDING_REVIEW;  // edits go back to review
        this.updatedAt = LocalDateTime.now();
    }

    public void addImage(AdImage image) {
        this.images.add(image);
    }

    // --- getters/setters ---

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ItemCondition getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public AdStatus getStatus() {
        return status;
    }

    public void setStatus(AdStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<AdImage> getImages() {
        return images;
    }

    public void setImages(List<AdImage> images) {
        this.images = images;
    }
}
