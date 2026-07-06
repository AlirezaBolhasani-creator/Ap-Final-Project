package divar.aut.backend.repository;

import divar.aut.backend.entity.AdImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdImageRepository extends JpaRepository<AdImage, Long> {
}
