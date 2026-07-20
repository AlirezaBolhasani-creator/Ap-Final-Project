package divar.aut.backend.repository;

import divar.aut.backend.entity.Conversation;
import divar.aut.backend.entity.Message;
import divar.aut.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationOrderBySentAtAsc(Conversation conversation);
    Message findFirstByConversationOrderBySentAtDesc(Conversation conversation);
    void deleteByConversationIn(List<Conversation> conversations);
    long countByConversationAndSenderNotAndReadFalse(Conversation conversation, User sender);
    List<Message> findByConversationAndSenderNotAndReadFalse(Conversation conversation, User sender);
}
