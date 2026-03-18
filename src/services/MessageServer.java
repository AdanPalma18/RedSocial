package services;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MessageServer {
    private static MessageServer instance;
    private ServerSocket serverSocket;
    private Map<String, PrintWriter> clientWriters;
    private ExecutorService threadPool;
    private boolean running;
    private static final int PORT = 5555;

    private MessageServer() {
        clientWriters = new ConcurrentHashMap<>();
        threadPool = Executors.newCachedThreadPool();
        running = false;
    }

    public static MessageServer getInstance() {
        if (instance == null) {
            instance = new MessageServer();
        }
        return instance;
    }

    public void iniciar() {
        if (running) {
            return;
        }

        threadPool.execute(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                running = true;
                System.out.println("Servidor de mensajes iniciado en puerto " + PORT);

                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        threadPool.execute(new ClientHandler(clientSocket));
                    } catch (IOException e) {
                        if (running) {
                            System.err.println("Error aceptando cliente: " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error iniciando servidor: " + e.getMessage());
            }
        });
    }

    public void detener() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
        } catch (IOException e) {
            System.err.println("Error deteniendo servidor: " + e.getMessage());
        }
    }

    public void registrarCliente(String username, PrintWriter writer) {
        clientWriters.put(username, writer);
        System.out.println("Cliente registrado: " + username);
    }

    public void desregistrarCliente(String username) {
        clientWriters.remove(username);
        System.out.println("Cliente desregistrado: " + username);
    }

    public void enviarMensaje(String remitente, String destinatario, String contenido) {
        PrintWriter writer = clientWriters.get(destinatario);
        if (writer != null) {
            writer.println("MENSAJE|" + remitente + "|" + contenido);
            writer.flush();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                String mensaje;
                while ((mensaje = reader.readLine()) != null) {
                    procesarMensaje(mensaje);
                }
            } catch (IOException e) {
                System.err.println("Error en cliente: " + e.getMessage());
            } finally {
                if (username != null) {
                    desregistrarCliente(username);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void procesarMensaje(String mensaje) {
            String[] partes = mensaje.split("\\|");
            String comando = partes[0];

            switch (comando) {
                case "REGISTRAR":
                    username = partes[1];
                    registrarCliente(username, writer);
                    writer.println("OK|Registrado");
                    break;

                case "ENVIAR":
                    String destinatario = partes[1];
                    String contenido = partes[2];
                    enviarMensaje(username, destinatario, contenido);
                    break;
            }
        }
    }
}
