package divar.aut.backend.repository;

import divar.aut.backend.model.User;

public interface UserRepository {
    void save(User user);
    User findByUsername(String username);
}