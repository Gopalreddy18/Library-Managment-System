package service;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class StudentService {
    public void addStudent(String name, String email, String phone) {
        String sql = "INSERT INTO students(name, email, phone) VALUES(?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("Database connection failed");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, name.trim());
                ps.setString(2, email.trim());
                ps.setString(3, phone.trim());
                ps.executeUpdate();
                System.out.println("Student added successfully");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void viewStudents() {
        String sql = "SELECT id, name, email, phone FROM students ORDER BY id";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("Database connection failed");
                return;
            }

            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                System.out.println("\nSTUDENT LIST");
                while (rs.next()) {
                    System.out.println(rs.getInt("id") + " "
                            + rs.getString("name") + " "
                            + rs.getString("email") + " "
                            + rs.getString("phone"));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id=?";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                System.out.println("Database connection failed");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
                System.out.println("Student deleted");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
