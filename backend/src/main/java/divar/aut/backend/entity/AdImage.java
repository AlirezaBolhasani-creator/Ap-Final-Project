package divar.aut.backend.entity;

import jakarta.persistence.*;

/**
 * One of potentially many images attached to an ad. Allows an ad to display
 * multiple photos in a gallery instead of a single overwritten image.
 */
@Entity
@Table(name = "ad_images")
public class AdImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @Column(nullable = false)
    private String fileName;

    protected AdImage() {
        // required by JPA
    }

    public AdImage(Ad ad, String fileName) {
        this.ad = ad;
        this.fileName = fileName;
    }

    public Long getId() {
        return id;
    }

    public Ad getAd() {
        return ad;
    }

    public void setAd(Ad ad) {
        this.ad = ad;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
