package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String URL =
            "jdbc:mysql://localhost:3306/lib_man?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "gopal";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            initializeDatabase(connection);
            return connection;
        } catch (Exception e) {
            System.out.println("Database connection error: " + e.getMessage());
            return null;
        }
    }

    private static void initializeDatabase(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS books ("
                            + "id INT AUTO_INCREMENT PRIMARY KEY, "
                            + "title VARCHAR(100) NOT NULL, "
                            + "author VARCHAR(100) NOT NULL, "
                            + "quantity INT NOT NULL DEFAULT 1)"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS students ("
                            + "id INT AUTO_INCREMENT PRIMARY KEY, "
                            + "name VARCHAR(100) NOT NULL, "
                            + "email VARCHAR(100) UNIQUE, "
                            + "phone VARCHAR(20))"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS issued_books ("
                            + "id INT AUTO_INCREMENT PRIMARY KEY, "
                            + "student_id INT NOT NULL, "
                            + "book_id INT NOT NULL, "
                            + "issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                            + "returned TINYINT(1) DEFAULT 0)"
            );
        }
    }
}
