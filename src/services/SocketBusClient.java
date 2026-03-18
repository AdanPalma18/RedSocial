package services;

import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;
import gui.MainFrame;

public class SocketBusClient {
    private static SocketBusClient instance;
    private Socket socket;
    private PrintWriter writer;
    private MainFrame mainFrame;
    private boolean running = false;

    private SocketBusClient() {}

    public static SocketBusClient getInstance() {
        if (instance == null) {
            instance = new SocketBusClient();
        }
        return instance;
    }

    public void connect(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        if (running) return;
        
        new Thread(() -> {
            while (true) {
                try {
                    socket = new Socket("localhost", 5000);
                    writer = new PrintWriter(socket.getOutputStream(), true);
                    running = true;
                    
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("NOTIFY_PROFILE_UPDATE")) {
                            String[] parts = line.split(":");
                            if (parts.length > 1) {
                                String user = parts[1];
                                System.out.println("Sync: Recargando perfil actualizado de: " + user);
                                AuthService.getInstance().recargarUsuariosLocal();
                                SwingUtilities.invokeLater(() -> mainFrame.actualizarTodo());
                            }
                        }
                    }
                } catch (IOException e) {
                    running = false;
                    try { Thread.sleep(5000); } catch (InterruptedException ie) {} // Reintentar conexión
                }
            }
        }).start();
    }

    public void notifyProfileUpdate(String username) {
        if (writer != null) {
            writer.println("NOTIFY_PROFILE_UPDATE:" + username);
        }
    }
}
