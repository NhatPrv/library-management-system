import common.Book;
import common.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class LibraryServer {
    private final int port;
    private final BookDAO dao = new BookDAO();
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public LibraryServer(int port){ this.port=port; }

    public void start(){
        new Thread(() -> {
            try(ServerSocket ss = new ServerSocket(port)) {
                System.out.println("Server listening on " + port);
                while(true){
                    Socket s = ss.accept();
                    ClientHandler h = new ClientHandler(this, s);
                    clients.add(h);
                    h.start();
                }
            } catch (IOException e){ e.printStackTrace(); }
        },"accept-thread").start();
    }

    public List<Book> getAll(){ return dao.findAll(); }

    public synchronized Message handle(Message req){
        try{
            switch(req.getType()){
                case ADD -> {
                    Book nb = dao.insert(req.getBook());
                    if(nb==null) return Message.error("Insert failed");
                    broadcast(Message.add(nb));
                    return null; // đã broadcast
                }
                case UPDATE -> {
                    if(!dao.update(req.getBook())) return Message.error("Update failed");
                    broadcast(Message.update(req.getBook()));
                    return null;
                }
                case DELETE -> {
                    if(!dao.delete(req.getBook().getId())) return Message.error("Delete failed");
                    broadcast(Message.delete(req.getBook()));
                    return null;
                }
                default -> { return Message.error("Unsupported request"); }
            }
        } catch (Exception e){
            return Message.error(e.getMessage());
        }
    }

    public void broadcast(Message m){
        for(ClientHandler ch : clients) ch.send(m);
    }

    public void remove(ClientHandler h){ clients.remove(h); }
}
