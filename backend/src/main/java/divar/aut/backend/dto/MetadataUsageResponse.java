package divar.aut.backend.dto;

/**
 * Data Transfer Object for metadata usage responses.
 * <p>
 * Contains the count of advertisements that would be affected
 * by a deletion operation on a metadata entity (e.g., category or city).
 * </p>
 */
public class MetadataUsageResponse {

    private final long affectedAds;

    /**
     * Constructs a new MetadataUsageResponse with the given affected ads count.
     *
     * @param affectedAds the number of affected advertisements.
     */
    public MetadataUsageResponse(long affectedAds) {
        this.affectedAds = affectedAds;
    }
    public long getAffectedAds() {
        return affectedAds;
    }
}