package divar.aut.backend.dto;

public class AdminStatsResponse {
    private final long totalUsers;
    private final long blockedUsers;
    private final long totalAds;
    private final long pendingAds;
    private final long activeAds;
    private final long soldAds;
    private final long categories;
    private final long cities;

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

    public long getTotalUsers() { return totalUsers; }
    public long getBlockedUsers() { return blockedUsers; }
    public long getTotalAds() { return totalAds; }
    public long getPendingAds() { return pendingAds; }
    public long getActiveAds() { return activeAds; }
    public long getSoldAds() { return soldAds; }
    public long getCategories() { return categories; }
    public long getCities() { return cities; }
}
