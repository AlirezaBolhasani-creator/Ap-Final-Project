package divar.aut.backend.repository;

import divar.aut.backend.entity.Ad;
import divar.aut.backend.entity.AdStatus;
import divar.aut.backend.entity.ItemCondition;
import divar.aut.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {

    Page<Ad> findByStatus(AdStatus status, Pageable pageable);

    List<Ad> findByStatusOrderByCreatedAtDesc(AdStatus status);

    /**
     * Find all ads owned by a specific user, ordered newest first. Includes ads
     * in any status (so owner can see their pending/rejected ads too).
     */
    List<Ad> findByOwnerOrderByCreatedAtDesc(User owner);

    /**
     * Advanced search for ACTIVE ads only. Deliberately restricted at the query
     * level so non-active ads are never accidentally leaked. Supports filtering
     * by keyword (title/description), category, city, price range, condition,
     * and custom sort order.
     */
    @Query("SELECT a FROM Ad a WHERE a.status = 'ACTIVE' " +
           "AND (:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "                      OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR a.category.id = :categoryId) " +
           "AND (:cityId IS NULL OR a.city.id = :cityId) " +
           "AND (:minPrice IS NULL OR a.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR a.price <= :maxPrice) " +
           "AND (:condition IS NULL OR a.itemCondition = :condition)")
    List<Ad> searchActiveAds(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("cityId") Long cityId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("condition") ItemCondition condition,
            Sort sort
    );
}

