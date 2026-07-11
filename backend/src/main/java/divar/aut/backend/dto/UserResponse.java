package divar.aut.backend.dto;

import divar.aut.backend.entity.User;

public class UserResponse {
    private final Long id;
    private final String username;
    private final String fullname;
    private final String email;
    private final String phone;
    private final String role;
    private final boolean blocked;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullname = user.getFullname();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole().name();
        this.blocked = user.isBlocked();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getFullname() { return fullname; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public boolean isBlocked() { return blocked; }
}
