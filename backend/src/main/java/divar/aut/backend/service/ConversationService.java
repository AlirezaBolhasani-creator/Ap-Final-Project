package divar.aut.backend.service;

import divar.aut.backend.dto.ConversationResponse;
import divar.aut.backend.dto.MessageRequest;
import divar.aut.backend.dto.MessageResponse;
import divar.aut.backend.entity.Ad;
import divar.aut.backend.entity.Conversation;
import divar.aut.backend.entity.Message;
import divar.aut.backend.entity.User;
import divar.aut.backend.exception.ApiException;
import divar.aut.backend.repository.AdRepository;
import divar.aut.backend.repository.ConversationRepository;
import divar.aut.backend.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AdRepository adRepository;

    public ConversationService(ConversationRepository conversationRepository, MessageRepository messageRepository,
                               AdRepository adRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.adRepository = adRepository;
    }

    public ConversationResponse startOrGetConversation(User buyer, Long adId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> ApiException.notFound("Advertisement not found"));
        User seller = ad.getOwner();

        if (seller.getId().equals(buyer.getId())) {
            throw ApiException.badRequest("You cannot message yourself about your own ad");
        }

        if (buyer.isBlocked()) {
            throw ApiException.forbidden("You cannot start a conversation because you are blocked.");
        } else if (seller.isBlocked()) {
            throw ApiException.forbidden("You cannot start a conversation with a blocked user.");
        }

        Conversation conversation = conversationRepository.findByAdAndBuyerAndSeller(ad, buyer, seller)
                .orElseGet(() -> conversationRepository.save(new Conversation(ad, buyer, seller)));
        return toConversationResponse(conversation);
    }

    public List<ConversationResponse> listConversationsForUser(User user) {
        return conversationRepository.findAllForUser(user).stream()
                .map(this::toConversationResponse)
                .toList();
    }

    public List<MessageResponse> listMessages(User requester, Long conversationId) {
        Conversation conversation = findConversationOrThrow(conversationId);
        requireParticipant(requester, conversation);
        return messageRepository.findByConversationOrderBySentAtAsc(conversation).stream()
                .map(MessageResponse::new)
                .toList();
    }

    public MessageResponse sendMessage(User sender, Long conversationId, MessageRequest request) {
        Conversation conversation = findConversationOrThrow(conversationId);
        requireParticipant(sender, conversation);

        User receiver = conversation.getBuyer().getId().equals(sender.getId())
                ? conversation.getSeller()
                : conversation.getBuyer();

        if (sender.isBlocked()) {
            throw ApiException.forbidden("Blocked users cannot send messages");
        }
        else if(receiver.isBlocked()) {
            throw ApiException.forbidden("Receiver is blocked you cannot send messages");
        }
        Message message = new Message(conversation, sender, request.getContent());
        messageRepository.save(message);
        return new MessageResponse(message);
    }

    private ConversationResponse toConversationResponse(Conversation conversation) {
        Message lastMessage = messageRepository.findFirstByConversationOrderBySentAtDesc(conversation);
        return new ConversationResponse(conversation, lastMessage);
    }

    private Conversation findConversationOrThrow(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> ApiException.notFound("Conversation not found"));
    }

    private void requireParticipant(User user, Conversation conversation) {
        boolean isBuyer = conversation.getBuyer().getId().equals(user.getId());
        boolean isSeller = conversation.getSeller().getId().equals(user.getId());
        if (!isBuyer && !isSeller) {
            throw ApiException.forbidden("You are not part of this conversation");
        }
    }
}
