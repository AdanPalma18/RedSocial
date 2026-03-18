package services;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MessageClient {
    private static MessageClient instance;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private List<MessageListener> listeners;
    private ExecutorService threadPool;
    private boolean connected;
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5555;

    private MessageClient() {
        listeners = new CopyOnWriteArrayList<>();
        threadPool = Executors.newSingleThreadExecutor();
        connected = false;
    }

    public static MessageClient getInstance() {
        if (instance == null) {
            instance = new MessageClient();
        }
        return instance;
    }

    public boolean conectar(String username) {
        if (connected) {
            return true;
        }

        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            connected = true;

            writer.println("REGISTRAR|" + username);

            threadPool.execute(this::escucharMensajes);

            return true;
        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor: " + e.getMessage());
            System.err.println("Modo offline activado");
            return false;
        }
    }

    public void desconectar() {
        connected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error desconectando: " + e.getMessage());
        }
    }

    public void enviarMensaje(String destinatario, String contenido) {
        if (connected && writer != null) {
            writer.println("ENVIAR|" + destinatario + "|" + contenido);
            writer.flush();
        }
    }

    private void escucharMensajes() {
        try {
            String mensaje;
            while (connected && (mensaje = reader.readLine()) != null) {
                procesarMensaje(mensaje);
            }
        } catch (IOException e) {
            if (connected) {
                System.err.println("Error escuchando mensajes: " + e.getMessage());
            }
        }
    }

    private void procesarMensaje(String mensaje) {
        String[] partes = mensaje.split("\\|");
        String comando = partes[0];

        if (comando.equals("MENSAJE")) {
            String remitente = partes[1];
            String contenido = partes[2];
            notificarNuevoMensaje(remitente, contenido);
        }
    }

    public void agregarListener(MessageListener listener) {
        listeners.add(listener);
    }

    public void removerListener(MessageListener listener) {
        listeners.remove(listener);
    }

    private void notificarNuevoMensaje(String remitente, String contenido) {
        for (MessageListener listener : listeners) {
            listener.onNuevoMensaje(remitente, contenido);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public interface MessageListener {
        void onNuevoMensaje(String remitente, String contenido);
    }
}
