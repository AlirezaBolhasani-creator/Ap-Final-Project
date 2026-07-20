package divar.aut.backend.dto;

/**
 * Data transfer object for administrative system statistics.
 * <p>
 * Contains aggregated metrics about the application's usage and state,
 * including user counts, ad statuses, and metadata totals.
 * </p>
 */
public class AdminStatsResponse {
    private final long totalUsers;
    private final long blockedUsers;
    private final long totalAds;
    private final long pendingAds;
    private final long activeAds;
    private final long soldAds;
    private final long categories;
    private final long cities;

    /**
     * Constructs a new AdminStatsResponse with all metrics.
     *
     * @param totalUsers   total number of registered users.
     * @param blockedUsers number of blocked users.
     * @param totalAds     total number of advertisements.
     * @param pendingAds   number of ads in pending state.
     * @param activeAds    number of active ads.
     * @param soldAds      number of sold ads.
     * @param categories   total number of categories.
     * @param cities       total number of cities.
     */
    public AdminStatsResponse(long totalUsers, long blockedUsers, long totalAds, long pendingAds,
                              long activeAds, long soldAds, long categories, long cities) {
        this.totalUsers = totalUsers;
        this.blockedUsers = blockedUsers;
        this.totalAds = totalAds;
        this.pendingAds = pendingAds;
        this.activeAds = activeAds;
        this.soldAds = soldAds;
        this.categories = categories;
        this.cities = cities;
    }

    /**
     * Returns the total number of registered users.
     *
     * @return total users count.
     */
    public long getTotalUsers() { return totalUsers; }

    /**
     * Returns the number of blocked users.
     *
     * @return blocked users count.
     */
    public long getBlockedUsers() { return blockedUsers; }

    /**
     * Returns the total number of advertisements.
     *
     * @return total ads count.
     */
    public long getTotalAds() { return totalAds; }

    /**
     * Returns the number of pending advertisements.
     *
     * @return pending ads count.
     */
    public long getPendingAds() { return pendingAds; }

    /**
     * Returns the number of active advertisements.
     *
     * @return active ads count.
     */
    public long getActiveAds() { return activeAds; }

    /**
     * Returns the number of sold advertisements.
     *
     * @return sold ads count.
     */
    public long getSoldAds() { return soldAds; }

    /**
     * Returns the total number of categories.
     *
     * @return categories count.
     */
    public long getCategories() { return categories; }

    /**
     * Returns the total number of cities.
     *
     * @return cities count.
     */
    public long getCities() { return cities; }
}