package service;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class IssueService {
    public void issueBook(int studentId, int bookId) {
        String checkBookSql = "SELECT quantity FROM books WHERE id=?";
        String updateBookSql = "UPDATE books SET quantity = quantity - 1 WHERE id=? AND quantity > 0";
        String insertIssueSql = "INSERT INTO issued_books(student_id, book_id) VALUES(?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("Database connection failed");
                return;
            }

            try (PreparedStatement checkPs = con.prepareStatement(checkBookSql)) {
                checkPs.setInt(1, bookId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Book not found");
                        return;
                    }
                    if (rs.getInt("quantity") <= 0) {
                        System.out.println("Book is out of stock");
                        return;
                    }
                }
            }

            try (PreparedStatement updateBookPs = con.prepareStatement(updateBookSql)) {
                updateBookPs.setInt(1, bookId);
                if (updateBookPs.executeUpdate() == 0) {
                    System.out.println("Could not issue book");
                    return;
                }
            }

            try (PreparedStatement issuePs = con.prepareStatement(insertIssueSql)) {
                issuePs.setInt(1, studentId);
                issuePs.setInt(2, bookId);
                issuePs.executeUpdate();
                System.out.println("Book issued successfully");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void returnBook(int issueId) {
        String selectBookSql = "SELECT book_id FROM issued_books WHERE id=? AND returned=0";
        String updateIssueSql = "UPDATE issued_books SET returned=1 WHERE id=?";
        String updateBookSql = "UPDATE books SET quantity = quantity + 1 WHERE id=?";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("Database connection failed");
                return;
            }

            int bookId = -1;
            try (PreparedStatement selectPs = con.prepareStatement(selectBookSql)) {
                selectPs.setInt(1, issueId);
                try (ResultSet rs = selectPs.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Issue record not found");
                        return;
                    }
                    bookId = rs.getInt("book_id");
                }
            }

            try (PreparedStatement updateIssuePs = con.prepareStatement(updateIssueSql)) {
                updateIssuePs.setInt(1, issueId);
                updateIssuePs.executeUpdate();
            }

            try (PreparedStatement updateBookPs = con.prepareStatement(updateBookSql)) {
                updateBookPs.setInt(1, bookId);
                updateBookPs.executeUpdate();
            }

            System.out.println("Book returned successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void viewIssuedBooks() {
        String sql = "SELECT ib.id, s.name, b.title, ib.issue_date, ib.returned FROM issued_books ib "
                + "JOIN students s ON ib.student_id = s.id "
                + "JOIN books b ON ib.book_id = b.id ORDER BY ib.id";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("Database connection failed");
                return;
            }

            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                System.out.println("\nISSUED BOOKS");
                while (rs.next()) {
                    System.out.println(rs.getInt("id") + " "
                            + rs.getString("name") + " "
                            + rs.getString("title") + " "
                            + rs.getTimestamp("issue_date") + " "
                            + rs.getInt("returned"));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
