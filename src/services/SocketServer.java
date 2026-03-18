package services;

import java.io.*;
import java.net.*;
import java.util.*;

public class SocketServer {
    private static SocketServer instance;
    private int port = 5000;
    private ServerSocket serverSocket;
    private boolean running = false;
    private List<ClientHandler> clients = new ArrayList<>();

    private SocketServer() {}

    public static SocketServer getInstance() {
        if (instance == null) {
            instance = new SocketServer();
        }
        return instance;
    }

    public void start() {
        if (running) return;
        running = true;
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Socket Server iniciado en puerto " + port);
                while (running) {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(clientSocket);
                    clients.add(handler);
                    new Thread(handler).start();
                }
            } catch (java.net.BindException e) {
                // Silencioso: el puerto ya está en uso por otra ventana, está bien.
                running = false;
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error en Socket Server: " + e.getMessage());
                }
            }
        }).start();
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
            for (ClientHandler client : clients) {
                client.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando Socket Server: " + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println("Error inicializando cliente: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                String input;
                writer.println("Conectado al servidor de RedSocial");
                while ((input = reader.readLine()) != null) {
                    System.out.println("Recibido de cliente: " + input);
                    writer.println("Eco: " + input);
                }
            } catch (IOException e) {
                System.err.println("Cliente desconectado");
            } finally {
                close();
            }
        }

        public void close() {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (socket != null) socket.close();
                clients.remove(this);
            } catch (IOException e) {
                System.err.println("Error cerrando cliente: " + e.getMessage());
            }
        }
    }
}
