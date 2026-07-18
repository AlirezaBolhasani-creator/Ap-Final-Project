package divar.aut.frontend.model;

public record MessageData(
        Long id,
        Long conversationId,
        Long senderId,
        String senderUsername,
        String content,
        String sentAt,
        boolean senderAdmin
) {
}
