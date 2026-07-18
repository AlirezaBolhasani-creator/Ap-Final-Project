package divar.aut.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seller_ratings", uniqueConstraints = @UniqueConstraint(columnNames = {"buyer_id", "seller_id"}))
public class SellerRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @Column(nullable = false)
    private int score;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected SellerRating() {
    }

    public SellerRating(User seller, User buyer, Ad ad, int score, String comment) {
        this.seller = seller;
        this.buyer = buyer;
        this.ad = ad;
        this.score = score;
        this.comment = comment;
    }

    public Long getId() { return id; }
    public User getSeller() { return seller; }
    public User getBuyer() { return buyer; }
    public Ad getAd() { return ad; }
    public int getScore() { return score; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
