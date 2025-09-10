package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class AcceptThread extends Thread {
    private final LibraryServer server;
    private final int port;

    AcceptThread(LibraryServer s, int port){ this.server=s; this.port=port; setName("accept-thread"); }

    @Override public void run(){
        try(ServerSocket ss = new ServerSocket(port)) {
            System.out.println("Server listening on " + port);
            while(true){
                Socket s = ss.accept();
                ClientHandler h = new ClientHandler(server, s);
                server.register(h);
                h.start();
            }
        } catch (IOException e){ e.printStackTrace(); }
    }
}
