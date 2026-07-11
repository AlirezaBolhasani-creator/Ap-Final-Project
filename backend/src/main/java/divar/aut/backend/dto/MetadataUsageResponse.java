package divar.aut.backend.dto;

public class MetadataUsageResponse {
    private final long affectedAds;

    public MetadataUsageResponse(long affectedAds) {
        this.affectedAds = affectedAds;
    }

    public long getAffectedAds() {
        return affectedAds;
    }
}
