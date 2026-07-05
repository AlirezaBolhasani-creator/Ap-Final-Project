package divar.aut.backend.repository;

import divar.aut.backend.entity.Ad;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {
    // automatically all methods are done
    Page<Ad> findByStatus(String status, Pageable pageable);
}
