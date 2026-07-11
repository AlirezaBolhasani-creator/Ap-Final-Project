package divar.aut.frontend.model;

public record UserData(Long id, String username, String fullname, String email, String phone,
                       String role, boolean blocked) {
}
