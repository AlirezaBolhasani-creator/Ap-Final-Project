package divar.aut.backend.repository;

import divar.aut.backend.entity.Ad;
import divar.aut.backend.entity.Favorite;
import divar.aut.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);
    Optional<Favorite> findByUserAndAd(User user, Ad ad);
    boolean existsByUserAndAd(User user, Ad ad);
    void deleteByAdIn(List<Ad> ads);
}
