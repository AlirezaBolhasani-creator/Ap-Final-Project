package divar.aut.backend.repository;

import divar.aut.backend.entity.Ad;
import divar.aut.backend.entity.SellerRating;
import divar.aut.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerRatingRepository extends JpaRepository<SellerRating, Long> {
    List<SellerRating> findBySellerOrderByCreatedAtDesc(User seller);
    boolean existsByBuyerAndSellerAndAd(User buyer, User seller, Ad ad);
    void deleteByAdIn(List<Ad> ads);
}
