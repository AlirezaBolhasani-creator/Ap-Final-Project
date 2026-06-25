//package divar.aut.backend;
//
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class UserRepository {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    public UserRepository(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//        this.jdbcTemplate.execute(
//                "CREATE TABLE IF NOT EXISTS users (" +
//                        "username TEXT PRIMARY KEY, " +
//                        "password TEXT)"
//        );
//    }
//
//    public boolean saveUser(String username, String password) {
//        try {
//            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
//            jdbcTemplate.update(sql, username, password);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public boolean verifyUser(String username, String password) {
//        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
//        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username, password);
//        return count != null && count > 0;
//    }
//}