import common.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientFrame extends JFrame {
    private final JTextField txtId = new JTextField();       // chỉ hiển thị
    private final JTextField txtTitle = new JTextField();
    private final JTextField txtAuthor = new JTextField();
    private final JTextField txtYear = new JTextField();

    private final BookTableModel model = new BookTableModel();
    private final JTable table = new JTable(model);

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientFrame() {
        setTitle("Library Client");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800,500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));

        // Table
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel form = new JPanel(new GridLayout(1,8,6,6));
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

        // Row click → load form
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

        // Actions
        btnAdd.addActionListener(e -> sendAdd());
        btnUpdate.addActionListener(e -> sendUpdate());
        btnDelete.addActionListener(e -> sendDelete());
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

    private void sendAdd(){
        try {
            Book b = new Book(0, txtTitle.getText().trim(), txtAuthor.getText().trim(), Integer.parseInt(txtYear.getText().trim()));
            out.writeObject(Message.add(b)); out.flush();
        } catch (Exception ex){ showErr(ex.getMessage()); }
    }

    private void sendUpdate(){
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            Book b = new Book(id, txtTitle.getText().trim(), txtAuthor.getText().trim(), Integer.parseInt(txtYear.getText().trim()));
            out.writeObject(Message.update(b)); out.flush();
        } catch (Exception ex){ showErr("Chọn dòng để sửa"); }
    }

    private void sendDelete(){
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            Book b = new Book(id, "", "", 0);
            out.writeObject(Message.delete(b)); out.flush();
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
                        case INIT, LIST -> SwingUtilities.invokeLater(() -> model.setData(m.getBooks()));
                        case ADD -> {
                            // lấy danh sách hiện tại + thêm item (cho mượt); hoặc yêu cầu server gửi LIST
                            List<Book> cur = new ArrayList<>();
                            for(int i=0;i<model.getRowCount();i++) cur.add(model.getAt(i));
                            cur.add(m.getBook());
                            SwingUtilities.invokeLater(() -> model.setData(cur));
                        }
                        case UPDATE -> {
                            List<Book> cur = new ArrayList<>();
                            for(int i=0;i<model.getRowCount();i++){
                                Book x = model.getAt(i);
                                if(x.getId()==m.getBook().getId()) x = m.getBook();
                                cur.add(x);
                            }
                            SwingUtilities.invokeLater(() -> model.setData(cur));
                        }
                        case DELETE -> {
                            List<Book> cur = new ArrayList<>();
                            for(int i=0;i<model.getRowCount();i++){
                                Book x = model.getAt(i);
                                if(x.getId()!=m.getBook().getId()) cur.add(x);
                            }
                            SwingUtilities.invokeLater(() -> model.setData(cur));
                        }
                        case ERROR -> showErr(m.getError());
                    }
                }
            } catch (Exception ignored) { }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientFrame().setVisible(true));
    }
}
