import common.Message;
import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final LibraryServer server;
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(LibraryServer s, Socket socket){
        this.server=s; this.socket=socket;
        setName("client-"+socket.getRemoteSocketAddress());
    }

    @Override public void run(){
        try(socket){
            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());

            // Gửi INIT danh sách hiện tại
            send(Message.init(server.getAll()));

            // Vòng lặp nhận yêu cầu
            while(true){
                Object obj = in.readObject();
                if(!(obj instanceof Message req)) break;
                Message resp = server.handle(req);
                if(resp!=null) send(resp); // chỉ gửi riêng nếu là ERROR
            }
        } catch (Exception ignored) {
        } finally {
            server.remove(this);
        }
    }

    public synchronized void send(Message m){
        try {
            if(out!=null){ out.writeObject(m); out.flush(); }
        } catch (IOException ignored) { }
    }
}
