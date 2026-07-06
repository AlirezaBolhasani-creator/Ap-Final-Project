package divar.aut.backend.controller;

import divar.aut.backend.dto.ConversationResponse;
import divar.aut.backend.dto.MessageRequest;
import divar.aut.backend.dto.MessageResponse;
import divar.aut.backend.security.UserPrincipal;
import divar.aut.backend.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> startConversation(@RequestParam Long adId,
                                                                  @AuthenticationPrincipal UserPrincipal principal) {
        ConversationResponse response = conversationService.startOrGetConversation(principal.getUser(), adId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<ConversationResponse> listMyConversations(@AuthenticationPrincipal UserPrincipal principal) {
        return conversationService.listConversationsForUser(principal.getUser());
    }

    @GetMapping("/{id}/messages")
    public List<MessageResponse> listMessages(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return conversationService.listMessages(principal.getUser(), id);
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageResponse> sendMessage(@PathVariable Long id,
                                                       @Valid @RequestBody MessageRequest request,
                                                       @AuthenticationPrincipal UserPrincipal principal) {
        MessageResponse response = conversationService.sendMessage(principal.getUser(), id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
