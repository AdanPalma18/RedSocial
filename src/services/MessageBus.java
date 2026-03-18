package services;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import persistence.FileManager;

/**
 * Sistema de mensajería simulado sin sockets.
 * Usa polling de archivos para detectar nuevos mensajes.
 */
public class MessageBus {
    private static MessageBus instance;
    private final FileManager fileManager;
    private final List<MessageListener> listeners;
    private ScheduledExecutorService scheduler;
    private String currentUser;
    private final Map<String, Long> lastChecked;

    public interface MessageListener {
        void onNuevoMensaje(String remitente, String contenido);
    }

    private MessageBus() {
        fileManager = FileManager.getInstance();
        listeners = new CopyOnWriteArrayList<>();
        lastChecked = new ConcurrentHashMap<>();
    }

    public static MessageBus getInstance() {
        if (instance == null) {
            instance = new MessageBus();
        }
        return instance;
    }

    public void iniciar(String username) {
        this.currentUser = username;
        
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        
        // Verificar nuevos mensajes cada 2 segundos
        scheduler.scheduleAtFixedRate(() -> {
            try {
                verificarNuevosMensajes();
            } catch (Exception e) {
                System.err.println("Error verificando mensajes: " + e.getMessage());
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    public void detener() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        listeners.clear();
        lastChecked.clear();
        currentUser = null;
    }

    public void agregarListener(MessageListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removerListener(MessageListener listener) {
        listeners.remove(listener);
    }

    private void verificarNuevosMensajes() {
        if (currentUser == null) {
            return;
        }

        String inboxPath = fileManager.getRutaUsuario(currentUser) + "/inbox.ins";
        File inboxFile = new File(inboxPath);

        if (!inboxFile.exists()) {
            return;
        }

        long lastModified = inboxFile.lastModified();
        Long lastCheck = lastChecked.get(currentUser);

        // Si el archivo fue modificado después de la última verificación
        if (lastCheck == null || lastModified > lastCheck) {
            lastChecked.put(currentUser, lastModified);
            
            // Solo notificar si no es la primera verificación
            if (lastCheck != null) {
                leerUltimosMensajes();
            }
        }
    }

    private void leerUltimosMensajes() {
        String inboxPath = fileManager.getRutaUsuario(currentUser) + "/inbox.ins";
        File file = new File(inboxPath);

        if (!file.exists()) {
            return;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            String ultimoRemitente = null;
            String ultimoContenido = null;
            boolean ultimoEsMio = true;

            // Leer todos los mensajes para encontrar el último que NO es mío
            while (dis.available() > 0) {
                String otroUsuario = dis.readUTF();
                String contenido = dis.readUTF();
                dis.readUTF(); // fecha
                boolean esMio = dis.readBoolean();
                
                // Saltar campos opcionales
                if (dis.available() > 0) dis.readBoolean(); // esSticker
                if (dis.available() > 0) dis.readBoolean(); // leido

                if (!esMio) {
                    ultimoRemitente = otroUsuario;
                    ultimoContenido = contenido;
                    ultimoEsMio = false;
                }
            }

            // Notificar si hay un mensaje nuevo que no es mío
            if (!ultimoEsMio && ultimoRemitente != null) {
                notificarListeners(ultimoRemitente, ultimoContenido);
            }
        } catch (IOException e) {
            System.err.println("Error leyendo mensajes: " + e.getMessage());
        }
    }

    private void notificarListeners(String remitente, String contenido) {
        for (MessageListener listener : listeners) {
            try {
                listener.onNuevoMensaje(remitente, contenido);
            } catch (Exception e) {
                System.err.println("Error notificando listener: " + e.getMessage());
            }
        }
    }

    public boolean isActivo() {
        return scheduler != null && !scheduler.isShutdown();
    }
}
