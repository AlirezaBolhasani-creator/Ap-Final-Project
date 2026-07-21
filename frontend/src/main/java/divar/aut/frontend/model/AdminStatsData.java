package divar.aut.frontend.model;

/**
 * Data Transfer Object representing administrative system statistics.
 * <p>
 * Contains aggregated metrics about the application's users, advertisements,
 * categories, and cities. Used for displaying the admin dashboard.
 * </p>
 *
 * @param totalUsers   the total number of registered users.
 * @param blockedUsers the number of users who are currently blocked.
 * @param totalAds     the total number of advertisements.
 * @param pendingAds   the number of ads pending review.
 * @param activeAds    the number of active (approved) ads.
 * @param soldAds      the number of ads marked as sold.
 * @param categories   the total number of categories.
 * @param cities       the total number of cities.
 */
public record AdminStatsData(long totalUsers, long blockedUsers, long totalAds, long pendingAds,
                             long activeAds, long soldAds, long categories, long cities) {
}