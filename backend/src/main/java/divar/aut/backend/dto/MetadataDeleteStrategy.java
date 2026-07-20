package divar.aut.backend.dto;

/**
 * Enumeration of strategies for deleting metadata (e.g., categories or cities)
 * that have associated advertisements.
 * <p>
 * Specifies how to handle dependent ads when a metadata entity is deleted.
 * </p>
 */
public enum MetadataDeleteStrategy {
    REASSIGN,
    DELETE_ADS
}