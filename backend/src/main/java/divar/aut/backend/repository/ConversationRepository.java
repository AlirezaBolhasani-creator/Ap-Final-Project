package divar.aut.backend.repository;

import divar.aut.backend.entity.Ad;
import divar.aut.backend.entity.Conversation;
import divar.aut.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByAdAndBuyerAndSeller(Ad ad, User buyer, User seller);

    @Query("SELECT c FROM Conversation c WHERE c.buyer = :user OR c.seller = :user ORDER BY c.createdAt DESC")
    List<Conversation> findAllForUser(@Param("user") User user);
}
