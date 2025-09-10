package server;

import common.Book;
import common.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LibraryServer {
    private final BookDAO dao = new BookDAO();
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    /** Observer cho ServerFrame (để cập nhật GUI server theo push, không polling). */
    public interface ServerListener { void onEvent(Message m); }
    private final List<ServerListener> listeners = new ArrayList<>();
    public void addListener(ServerListener l){ listeners.add(l); }
    public void removeListener(ServerListener l){ listeners.remove(l); }

    /** Gửi cho tất cả client + báo cho ServerFrame. */
    public void broadcast(Message m){
        for (ClientHandler ch : clients) ch.send(m);
        for (ServerListener l : listeners) l.onEvent(m);
    }

    public List<Book> getAll(){ return dao.findAll(); }

    /** Xử lý yêu cầu từ client, sau đó broadcast ngay. */
    public synchronized Message handle(Message req){
        try {
            switch (req.getType()){
                case ADD -> {
                    Book nb = dao.insert(req.getBook());
                    if (nb==null) return Message.error("Insert failed");
                    broadcast(Message.add(nb));
                }
                case UPDATE -> {
                    if (!dao.update(req.getBook())) return Message.error("Update failed");
                    broadcast(Message.update(req.getBook()));
                }
                case DELETE -> {
                    if (!dao.delete(req.getBook().getId())) return Message.error("Delete failed");
                    broadcast(Message.delete(req.getBook()));
                }
                default -> { return Message.error("Unsupported"); }
            }
            return null; // đã broadcast
        } catch (Exception e){
            return Message.error(e.getMessage());
        }
    }

    public void register(ClientHandler h){ clients.add(h); }
    public void unregister(ClientHandler h){ clients.remove(h); }

    public void startAcceptLoop(int port) throws IOException {
        new AcceptThread(this, port).start();
    }
}
