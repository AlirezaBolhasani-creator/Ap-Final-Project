package divar.aut.frontend.model;

import java.util.List;

public record AdDetailData(
        Long id,
        String title,
        String description,
        double price,
        String itemCondition,
        String status,
        String categoryName,
        String cityName,
        String rejectionReason,
        SellerProfile seller,
        List<String> imageFileNames,
        double averageRating,
        int ratingCount
) {
    public record SellerProfile(Long id, String username, String fullname) {
    }
}
