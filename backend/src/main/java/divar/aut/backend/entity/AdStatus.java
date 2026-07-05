package divar.aut.backend.entity;

/**
 * The lifecycle states an advertisement can be in.
 *
 * PENDING_REVIEW -> ACTIVE       (admin approves)
 * PENDING_REVIEW -> REJECTED     (admin rejects, optionally with a reason)
 * ACTIVE         -> SOLD         (owner marks it sold)
 * ACTIVE/PENDING_REVIEW/REJECTED -> DELETED (owner or admin deletes it)
 *
 * Only ACTIVE ads are visible in public browsing/search results.
 */
public enum AdStatus {
    PENDING_REVIEW,
    ACTIVE,
    REJECTED,
    DELETED,
    SOLD
}
