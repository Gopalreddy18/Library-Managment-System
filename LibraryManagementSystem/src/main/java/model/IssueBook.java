package model;

import java.sql.Timestamp;

public class IssueBook {
    private int id;
    private int studentId;
    private int bookId;
    private Timestamp issueDate;
    private boolean returned;

    public IssueBook() {
    }

    public IssueBook(int id, int studentId, int bookId, Timestamp issueDate, boolean returned) {
        this.id = id;
        this.studentId = studentId;
        this.bookId = bookId;
        this.issueDate = issueDate;
        this.returned = returned;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Timestamp getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Timestamp issueDate) {
        this.issueDate = issueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
