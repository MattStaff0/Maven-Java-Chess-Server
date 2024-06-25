package chess;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class LobbyServer {
    List<ServerThread> totalPlayers = new CopyOnWriteArrayList<>();
    int totalCount = 0;
    final static int SERVERPORT = 12345;

    public LobbyServer() {}

    public int getClients() {
        return this.totalCount;
    }

    public synchronized void incrementClients() {
        this.totalCount++;
    }

    public synchronized void decrementClients() {
        this.totalCount--;
    }

    public void checkAlive() {
        for (ServerThread thread : totalPlayers) {
            if (!thread.isConnected() || !thread.sendHeartBeat()) {
                totalPlayers.remove(thread);
                decrementClients();
            }
        }
        System.out.println("num of clinets" + this.totalCount);
        System.out.println("made it");
    }

    public static void main(String[] args) {
        LobbyServer lobbyServer = new LobbyServer();
        try {
            ServerSocket serverSocket = new ServerSocket(SERVERPORT);

            while (true) {
                Socket socket = serverSocket.accept();
                ServerThread thread = new ServerThread(lobbyServer, socket);
                lobbyServer.totalPlayers.add(thread);
                lobbyServer.incrementClients();
                thread.start();
                System.out.println("worked");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ServerThread extends Thread {
    private LobbyServer parent;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private boolean running = true;

    public ServerThread(LobbyServer parentServer, Socket socket) {
        this.parent = parentServer;
        this.socket = socket;

        try {
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        try {
            while (running) {
                String message = reader.readLine();
                if (message == null) {
                    break; // Client disconnected
                }
                if (message.equals("still_alive")) {
                    Thread.sleep(2500);
                    System.out.println("working");
                    this.parent.checkAlive();
                }  else{
                    break; }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.closeAll();
        }
    }

    public void closeAll() {
        running = false;
        try {
            this.writer.close();
            this.reader.close();
            this.socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return !socket.isClosed();
    }

    public boolean sendHeartBeat(){
        try{
            this.writer.println("" + this.parent.getClients());
            this.writer.flush();
            return true;
        } catch (Exception e){
            this.closeAll();
            return false;
        }

    }
}
