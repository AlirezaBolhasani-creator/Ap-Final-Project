package divar.aut.frontend.model;

public record AdminStatsData(long totalUsers, long blockedUsers, long totalAds, long pendingAds,
                             long activeAds, long soldAds, long categories, long cities) {
}
