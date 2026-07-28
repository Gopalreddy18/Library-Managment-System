package service;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class BookService {
    public void addBook(String title, String author, int quantity) {
        if (title == null || title.trim().isEmpty()) {
            System.out.println("Title is required");
            return;
        }

        String sql = "INSERT INTO books(title, author, quantity) VALUES(?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("Database connection failed");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, title.trim());
                ps.setString(2, author.trim());
                ps.setInt(3, quantity);
                ps.executeUpdate();
                System.out.println("Book added successfully");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void viewBooks() {
        String sql = "SELECT id, title, author, quantity FROM books ORDER BY id";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("Database connection failed");
                return;
            }

            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                System.out.println("\nBOOK LIST");
                while (rs.next()) {
                    System.out.println(rs.getInt("id") + " "
                            + rs.getString("title") + " "
                            + rs.getString("author") + " "
                            + rs.getInt("quantity"));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id=?";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("Database connection failed");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
                System.out.println("Book deleted");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
