package divar.aut.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents a user's favorite advertisement.
 * A user can mark multiple ads as favorites, and an ad can be favorited
 * by multiple users. The combination of user and ad is unique, preventing
 * duplicate favorites. This entity is used to power the user's wishlist
 * or saved ads feature.
 */
@Entity
@Table(name = "favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ad_id"}))
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Favorite() {
    }

    public Favorite(User user, Ad ad) {
        this.user = user;
        this.ad = ad;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Ad getAd() {
        return ad;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
