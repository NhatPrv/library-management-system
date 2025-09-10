import common.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClientFrame extends JFrame {
    private final BookTableModel model = new BookTableModel();
    private final JTable table = new JTable(model);

    private final JTextField txtId = new JTextField();  
    private final JTextField txtTitle = new JTextField();
    private final JTextField txtAuthor = new JTextField();
    private final JTextField txtYear = new JTextField();

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientFrame() {
        setTitle("Library Client (Realtime)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));

        // Table
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel form = new JPanel(new GridLayout(1, 8, 6, 6));
        txtId.setEditable(false);
        form.add(new JLabel("ID"));    form.add(txtId);
        form.add(new JLabel("Title")); form.add(txtTitle);
        form.add(new JLabel("Author"));form.add(txtAuthor);
        form.add(new JLabel("Year"));  form.add(txtYear);
        add(form, BorderLayout.NORTH);

        // Buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        JButton btnAdd=new JButton("Add");
        JButton btnUpdate=new JButton("Update");
        JButton btnDelete=new JButton("Delete");
        JButton btnClear=new JButton("Clear");
        actions.add(btnAdd); actions.add(btnUpdate); actions.add(btnDelete); actions.add(btnClear);
        add(actions, BorderLayout.SOUTH);

        // Row click → load to form
        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            Book b = model.getAt(r);
            if(b!=null){
                txtId.setText(String.valueOf(b.getId()));
                txtTitle.setText(b.getTitle());
                txtAuthor.setText(b.getAuthor());
                txtYear.setText(String.valueOf(b.getYear()));
            }
        });

        btnAdd.addActionListener(e -> doAdd());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());
        btnClear.addActionListener(e -> { txtId.setText(""); txtTitle.setText(""); txtAuthor.setText(""); txtYear.setText(""); });

        connect("127.0.0.1", 5555);
        new Receiver().start();
    }

    private void connect(String host, int port){
        try {
            Socket s = new Socket(host, port);
            out = new ObjectOutputStream(s.getOutputStream());
            in  = new ObjectInputStream(s.getInputStream());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Không kết nối được server", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void doAdd(){
        try {
            String title = txtTitle.getText().trim();
            String author = txtAuthor.getText().trim();
            int year = Integer.parseInt(txtYear.getText().trim());
            out.writeObject(Message.add(new Book(0, title, author, year)));
            out.flush();
        } catch (Exception ex){ showErr("Dữ liệu không hợp lệ"); }
    }

    private void doUpdate(){
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String title = txtTitle.getText().trim();
            String author = txtAuthor.getText().trim();
            int year = Integer.parseInt(txtYear.getText().trim());
            out.writeObject(Message.update(new Book(id, title, author, year)));
            out.flush();
        } catch (Exception ex){ showErr("Chọn dòng để sửa"); }
    }

    private void doDelete(){
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            out.writeObject(Message.delete(new Book(id, "", "", 0)));
            out.flush();
        } catch (Exception ex){ showErr("Chọn dòng để xóa"); }
    }

    private void showErr(String m){ JOptionPane.showMessageDialog(this, "❌ " + m, "Error", JOptionPane.ERROR_MESSAGE); }

    class Receiver extends Thread {
        @Override public void run() {
            try {
                while(true){
                    Object obj = in.readObject();
                    if(!(obj instanceof Message m)) break;
                    switch (m.getType()){
                        case INIT   -> SwingUtilities.invokeLater(() -> model.setAll(m.getBooks()));
                        case ADD    -> SwingUtilities.invokeLater(() -> model.upsert(m.getBook()));
                        case UPDATE -> SwingUtilities.invokeLater(() -> model.upsert(m.getBook()));
                        case DELETE -> SwingUtilities.invokeLater(() -> model.removeById(m.getBook().getId()));
                        case ERROR  -> showErr(m.getError());
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientFrame().setVisible(true));
    }
}
