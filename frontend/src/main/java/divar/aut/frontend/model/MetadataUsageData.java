package divar.aut.frontend.model;

/**
 * Data Transfer Object representing the number of advertisements that would be
 * affected by a deletion operation on a metadata entity (e.g., category or city).
 *
 * @param affectedAds the number of advertisements associated with the metadata entity.
 */
public record MetadataUsageData(long affectedAds) {
}