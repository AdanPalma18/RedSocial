package services;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import model.Mensaje;
import model.Usuario;
import persistence.FileManager;

public class InboxService {
    private static InboxService instance;
    private FileManager fileManager;
    private AuthService authService;

    private InboxService() {
        fileManager = FileManager.getInstance();
        authService = AuthService.getInstance();
    }

    public static InboxService getInstance() {
        if (instance == null) {
            instance = new InboxService();
        }
        return instance;
    }

    public boolean enviarMensaje(String remitente, String destinatario, String contenido) {
        return enviarMensaje(remitente, destinatario, contenido, false);
    }

    public boolean enviarSticker(String remitente, String destinatario, String stickerContenido) {
        return enviarMensaje(remitente, destinatario, stickerContenido, true);
    }

    private boolean enviarMensaje(String remitente, String destinatario, String contenido, boolean esSticker) {
        if (!esSticker && contenido.length() > 300) {
            return false;
        }

        Usuario usuarioRemitente = authService.getUsuario(remitente);
        Usuario usuarioDestinatario = authService.getUsuario(destinatario);

        if (usuarioRemitente == null || usuarioDestinatario == null) {
            return false;
        }

        if (!usuarioDestinatario.puedeEnviarMensaje(usuarioRemitente)) {
            return false;
        }

        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        
        guardarMensaje(remitente, destinatario, contenido, fecha, true, esSticker, true);
        guardarMensaje(destinatario, remitente, contenido, fecha, false, esSticker, false);
        
        return true;
    }

    private void guardarMensaje(String usuario, String otroUsuario, String contenido, String fecha, boolean esMio, boolean esSticker, boolean leido) {
        String rutaInbox = fileManager.getRutaUsuario(usuario) + "/inbox.ins";
        
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(rutaInbox, true))) {
            dos.writeUTF(otroUsuario);
            dos.writeUTF(contenido);
            dos.writeUTF(fecha);
            dos.writeBoolean(esMio);
            dos.writeBoolean(esSticker);
            dos.writeBoolean(leido);
        } catch (IOException e) {
            System.err.println("Error guardando mensaje: " + e.getMessage());
        }
    }

    public List<Mensaje> leerConversacion(String usuario1, String usuario2) {
        List<Mensaje> mensajes = new ArrayList<>();
        String rutaInbox = fileManager.getRutaUsuario(usuario1) + "/inbox.ins";
        File file = new File(rutaInbox);

        if (!file.exists()) {
            return mensajes;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            while (dis.available() > 0) {
                String otroUsuario = dis.readUTF();
                String contenido = dis.readUTF();
                String fecha = dis.readUTF();
                boolean esMio = dis.readBoolean();
                boolean esSticker = dis.readBoolean();
                boolean leido = dis.readBoolean();

                if (otroUsuario.equals(usuario2)) {
                    String remitente = esMio ? usuario1 : usuario2;
                    String destinatario = esMio ? usuario2 : usuario1;
                    mensajes.add(new Mensaje(remitente, destinatario, contenido, fecha, esSticker, leido));
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo conversación: " + e.getMessage());
        }

        marcarMensajesComoLeidos(usuario1, usuario2);

        return mensajes;
    }

    private void marcarMensajesComoLeidos(String usuario, String otroUsuario) {
        String rutaInbox = fileManager.getRutaUsuario(usuario) + "/inbox.ins";
        File file = new File(rutaInbox);

        if (!file.exists()) {
            return;
        }

        List<MensajeTemp> mensajes = new ArrayList<>();

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            while (dis.available() > 0) {
                String otro = dis.readUTF();
                String contenido = dis.readUTF();
                String fecha = dis.readUTF();
                boolean esMio = dis.readBoolean();
                boolean esSticker = dis.readBoolean();
                boolean leido = dis.readBoolean();

                if (otro.equals(otroUsuario) && !esMio) {
                    leido = true;
                }

                mensajes.add(new MensajeTemp(otro, contenido, fecha, esMio, esSticker, leido));
            }
        } catch (IOException e) {
            System.err.println("Error leyendo mensajes: " + e.getMessage());
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            for (MensajeTemp msg : mensajes) {
                dos.writeUTF(msg.otroUsuario);
                dos.writeUTF(msg.contenido);
                dos.writeUTF(msg.fecha);
                dos.writeBoolean(msg.esMio);
                dos.writeBoolean(msg.esSticker);
                dos.writeBoolean(msg.leido);
            }
        } catch (IOException e) {
            System.err.println("Error actualizando mensajes: " + e.getMessage());
        }
    }

    public void eliminarConversacion(String usuario, String otroUsuario) {
        String rutaInbox = fileManager.getRutaUsuario(usuario) + "/inbox.ins";
        File file = new File(rutaInbox);

        if (!file.exists()) {
            return;
        }

        List<MensajeTemp> mensajesRestantes = new ArrayList<>();

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            while (dis.available() > 0) {
                String otro = dis.readUTF();
                String contenido = dis.readUTF();
                String fecha = dis.readUTF();
                boolean esMio = dis.readBoolean();
                boolean esSticker = dis.readBoolean();
                boolean leido = dis.readBoolean();

                if (!otro.equals(otroUsuario)) {
                    mensajesRestantes.add(new MensajeTemp(otro, contenido, fecha, esMio, esSticker, leido));
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo mensajes para eliminar: " + e.getMessage());
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            for (MensajeTemp msg : mensajesRestantes) {
                dos.writeUTF(msg.otroUsuario);
                dos.writeUTF(msg.contenido);
                dos.writeUTF(msg.fecha);
                dos.writeBoolean(msg.esMio);
                dos.writeBoolean(msg.esSticker);
                dos.writeBoolean(msg.leido);
            }
        } catch (IOException e) {
            System.err.println("Error actualizando inbox tras eliminar: " + e.getMessage());
        }
    }

    private static class MensajeTemp {
        String otroUsuario;
        String contenido;
        String fecha;
        boolean esMio;
        boolean esSticker;
        boolean leido;

        MensajeTemp(String otroUsuario, String contenido, String fecha, boolean esMio, boolean esSticker, boolean leido) {
            this.otroUsuario = otroUsuario;
            this.contenido = contenido;
            this.fecha = fecha;
            this.esMio = esMio;
            this.esSticker = esSticker;
            this.leido = leido;
        }
    }

    public Map<String, String> obtenerChats(String usuario) {
        Map<String, String> chats = new LinkedHashMap<>();
        String rutaInbox = fileManager.getRutaUsuario(usuario) + "/inbox.ins";
        File file = new File(rutaInbox);

        if (!file.exists()) {
            return chats;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            while (dis.available() > 0) {
                String otroUsuario = dis.readUTF();
                String contenido = dis.readUTF();
                String fecha = dis.readUTF();
                dis.readBoolean(); // esMio
                dis.readBoolean(); // esSticker
                dis.readBoolean(); // leido

                chats.put(otroUsuario, contenido);
            }
        } catch (IOException e) {
            System.err.println("Error leyendo chats: " + e.getMessage());
        }

        return chats;
    }

    public int contarTotalMensajesNoLeidos(String usuario) {
        int count = 0;
        String rutaInbox = fileManager.getRutaUsuario(usuario) + "/inbox.ins";
        File file = new File(rutaInbox);

        if (!file.exists()) {
            return 0;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            while (dis.available() > 0) {
                dis.readUTF(); // otro
                dis.readUTF(); // contenido
                dis.readUTF(); // fecha
                boolean esMio = dis.readBoolean();
                dis.readBoolean(); // esSticker
                boolean leido = dis.readBoolean();

                if (!esMio && !leido) {
                    count++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error contando mensajes no leídos: " + e.getMessage());
        }

        return count;
    }

    public int contarMensajesNoLeidos(String usuario, String otroUsuario) {
        int count = 0;
        String rutaInbox = fileManager.getRutaUsuario(usuario) + "/inbox.ins";
        File file = new File(rutaInbox);

        if (!file.exists()) {
            return 0;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            while (dis.available() > 0) {
                String otro = dis.readUTF();
                dis.readUTF();
                dis.readUTF(); // fecha
                boolean esMio = dis.readBoolean();
                dis.readBoolean(); // esSticker
                boolean leido = dis.readBoolean();

                if (otro.equals(otroUsuario) && !esMio && !leido) {
                    count++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error contando mensajes no leídos: " + e.getMessage());
        }

        return count;
    }

    public void conectarCliente(String username) {
        MessageBus.getInstance().iniciar(username);
    }

    public void desconectarCliente() {
        MessageBus.getInstance().detener();
    }

    public void agregarListener(MessageBus.MessageListener listener) {
        MessageBus.getInstance().agregarListener(listener);
    }

    public void removerListener(MessageBus.MessageListener listener) {
        MessageBus.getInstance().removerListener(listener);
    }
}
