import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LibraryGUI extends JFrame {

    Library lib = new Library();

    JTextField idField, nameField, authorField;
    JTable table;
    DefaultTableModel model;

    public LibraryGUI() {

        setTitle("Library Management System");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));


        JLabel title = new JLabel("LIBRARY MANAGEMENT SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(20,20));
        centerPanel.setBackground(new Color(245,247,250));



        JPanel formPanel = new JPanel(new GridLayout(4,2,15,15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Book"));

        JLabel idLabel = new JLabel("Book ID");
        JLabel nameLabel = new JLabel("Book Name");
        JLabel authorLabel = new JLabel("Author");

        idField = new JTextField();
        nameField = new JTextField();
        authorField = new JTextField();

        JButton addBtn = new JButton("Add Book");

        addBtn.setBackground(new Color(52,152,219));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);

        formPanel.add(idLabel);
        formPanel.add(idField);

        formPanel.add(nameLabel);
        formPanel.add(nameField);

        formPanel.add(authorLabel);
        formPanel.add(authorField);

        formPanel.add(new JLabel(""));
        formPanel.add(addBtn);

        centerPanel.add(formPanel, BorderLayout.WEST);


        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Author");
        model.addColumn("Issued");

        table = new JTable(model);

        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane pane = new JScrollPane(table);

        centerPanel.add(pane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
 
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245,247,250));

        JButton issueBtn = new JButton("Issue Book");
        JButton returnBtn = new JButton("Return Book");
        JButton refreshBtn = new JButton("Refresh");

        issueBtn.setBackground(new Color(46,204,113));
        returnBtn.setBackground(new Color(231,76,60));
        refreshBtn.setBackground(new Color(241,196,15));

        JButton[] buttons = {issueBtn, returnBtn, refreshBtn};

        for (JButton b : buttons) {
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setPreferredSize(new Dimension(140,40));
            buttonPanel.add(b);
        }

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        addBtn.addActionListener(e -> {

            try {

                int id = Integer.parseInt(idField.getText());

                String name = nameField.getText();
                String author = authorField.getText();

                if(name.isEmpty() || author.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please fill all fields");
                    return;
                }

                lib.addBook(new Book(id, name, author));

                refreshTable();

                idField.setText("");
                nameField.setText("");
                authorField.setText("");

            } catch(Exception ex) {

                JOptionPane.showMessageDialog(this,
                        "Invalid Input");
            }

        });

        issueBtn.addActionListener(e -> {

            int row = table.getSelectedRow();

            if(row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Select a book");
                return;
            }

            int id = (int) model.getValueAt(row,0);

            lib.issueBook(id);

            refreshTable();
        });

        returnBtn.addActionListener(e -> {

            int row = table.getSelectedRow();

            if(row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Select a book");
                return;
            }

            int id = (int) model.getValueAt(row,0);

            lib.returnBook(id);

            refreshTable();
        });

        refreshBtn.addActionListener(e -> refreshTable());

        setVisible(true);
    }
 
    public void refreshTable() {

        model.setRowCount(0);

        for(Book b : lib.books) {

            model.addRow(new Object[] {
                    b.id,
                    b.name,
                    b.author,
                    b.isIssued
            });
        }
    }

    public static void main(String[] args) {

        new LibraryGUI();
    }
}