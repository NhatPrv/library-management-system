import common.BookTableModel;
import javax.swing.*;
import java.awt.*;

public class ServerFrame extends JFrame {
    private final LibraryServer server;
    private final BookTableModel model = new BookTableModel();
    private final JTable table = new JTable(model);

    public ServerFrame() {
        setTitle("Library Server");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600,400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));
        add(new JScrollPane(table), BorderLayout.CENTER);

        server = new LibraryServer(5555);
        server.start();

        // Cập nhật bảng server mỗi 2 giây (đơn giản)
        new Timer(2000, e -> model.setData(server.getAll())).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServerFrame().setVisible(true));
    }
}
