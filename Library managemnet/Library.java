import java.util.ArrayList;

public class Library {

    ArrayList<Book> books = new ArrayList<>();

    // Add Book
    public void addBook(Book b) {
        books.add(b);
    }

    // Issue Book
    public void issueBook(int id) {

        for (Book b : books) {

            if (b.id == id) {

                if (!b.isIssued) {
                    b.isIssued = true;
                    return;
                }
            }
        }
    }

    // Return Book
    public void returnBook(int id) {

        for (Book b : books) {

            if (b.id == id) {

                if (b.isIssued) {
                    b.isIssued = false;
                    return;
                }
            }
        }
    }
}