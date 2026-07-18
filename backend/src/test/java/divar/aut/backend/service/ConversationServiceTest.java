package divar.aut.backend.service;

import divar.aut.backend.dto.MessageRequest;
import divar.aut.backend.dto.MessageResponse;
import divar.aut.backend.entity.Ad;
import divar.aut.backend.entity.Conversation;
import divar.aut.backend.entity.User;
import divar.aut.backend.exception.ApiException;
import divar.aut.backend.repository.AdRepository;
import divar.aut.backend.repository.ConversationRepository;
import divar.aut.backend.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock private ConversationRepository conversationRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private AdRepository adRepository;

    @InjectMocks
    private ConversationService conversationService;

    private User buyer;
    private User seller;
    private Ad ad;
    private Conversation conversation;

    @BeforeEach
    void setUp() {
        buyer = new User("buyer", "pass", "Buyer", "b@test.com", "0911");
        buyer.setId(1L);

        seller = new User("seller", "pass", "Seller", "s@test.com", "0912");
        seller.setId(2L);

        ad = new Ad("لپ‌تاپ دست دوم", "توضیحات", 15000.0, null, seller, null, null);
        ReflectionTestUtils.setField(ad, "id", 10L);

        conversation = new Conversation(ad, buyer, seller);
        ReflectionTestUtils.setField(conversation, "id", 100L);
    }

    @Test
    void startOrGetConversation_WhenBuyerIsSeller_ShouldThrowException() {
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));

        ApiException exception = assertThrows(ApiException.class, () -> {
            conversationService.startOrGetConversation(seller, 10L);
        });

        assertTrue(exception.getMessage().contains("You cannot message yourself"));
    }

    @Test
    void sendMessage_WhenSenderIsBlocked_ShouldThrowException() {


        buyer.setBlocked(true);
        when(conversationRepository.findById(100L)).thenReturn(Optional.of(conversation));

        MessageRequest request = new MessageRequest();
        ReflectionTestUtils.setField(request, "content", "سلام، موجوده؟");

        ApiException exception = assertThrows(ApiException.class, () -> {
            conversationService.sendMessage(buyer, 100L, request);
        });

        assertTrue(exception.getMessage().contains("Blocked users cannot send messages"));
    }

    @Test
    void sendMessage_WhenReceiverIsBlocked_ShouldThrowException() {
        seller.setBlocked(true);
        when(conversationRepository.findById(100L)).thenReturn(Optional.of(conversation));

        MessageRequest request = new MessageRequest();
        ReflectionTestUtils.setField(request, "content", "سلام، موجوده؟");

        ApiException exception = assertThrows(ApiException.class, () -> {
            conversationService.sendMessage(buyer, 100L, request);
        });

        assertTrue(exception.getMessage().contains("Receiver is blocked"));
    }

    @Test
    void sendMessage_WhenBothUsersAreActive_ShouldSaveAndReturnMessage() {
        when(conversationRepository.findById(100L)).thenReturn(Optional.of(conversation));

        MessageRequest request = new MessageRequest();
        ReflectionTestUtils.setField(request, "content", "سلام، قیمت نهایی چنده؟");

        MessageResponse response = conversationService.sendMessage(buyer, 100L, request);

        assertNotNull(response);
        assertEquals("سلام، قیمت نهایی چنده؟", response.getContent());

        verify(messageRepository, times(1)).save(any());
    }
}