package divar.aut.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents a conversation thread between a buyer and a seller
 * about a specific advertisement. Each conversation is uniquely
 * identified by the combination of ad, buyer, and seller.
 * Messages are linked to this conversation via the {@link Message} entity.
 */
@Entity
@Table(name = "conversations", uniqueConstraints = @UniqueConstraint(columnNames = {"ad_id", "buyer_id", "seller_id"}))
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Conversation() {
    }

    public Conversation(Ad ad, User buyer, User seller) {
        this.ad = ad;
        this.buyer = buyer;
        this.seller = seller;
    }

    public Long getId() { return id; }
    public Ad getAd() { return ad; }
    public User getBuyer() { return buyer; }
    public User getSeller() { return seller; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}