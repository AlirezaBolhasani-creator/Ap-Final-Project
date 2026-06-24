package divar.aut.backend.repository;

import divar.aut.backend.model.User;

import java.sql.*;

public class SqlUserRepository implements UserRepository
{
    private static SqlUserRepository instance;
    private SqlUserRepository()
    {
        DatabaseManager.initDatabase();
    }
    public static synchronized SqlUserRepository getInstance()
    {
        if (instance == null)
            instance = new SqlUserRepository();
        return instance;
    }
    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user in SQL: " + e.getMessage());
        }
        return null;
    }
    @Override
    public void save(User user)
    {
        String sql = "INSERT OR REPLACE INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();
            System.out.println("User saved to SQL: " + user.getUsername());

        } catch (SQLException e) {
            System.err.println("Error saving user to SQL: " + e.getMessage());
        }

    }
}
