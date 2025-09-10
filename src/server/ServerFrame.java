package server;

import common.BookTableModel;
import javax.swing.*;
import java.awt.*;

public class ServerFrame extends JFrame {
    private final LibraryServer server = new LibraryServer();
    private final BookTableModel model = new BookTableModel();

    public ServerFrame() {
        setTitle("Library Server (Realtime)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

        // Lần đầu hiển thị
        model.setAll(server.getAll());

        // Lắng nghe sự kiện từ server (push)
        server.addListener(m -> SwingUtilities.invokeLater(() -> {
            switch (m.getType()){
                case ADD    -> model.upsert(m.getBook());
                case UPDATE -> model.upsert(m.getBook());
                case DELETE -> model.removeById(m.getBook().getId());
                default -> {}
            }
        }));

        try { server.startAcceptLoop(5555); } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServerFrame().setVisible(true));
    }
}
