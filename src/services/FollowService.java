package services;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import persistence.FileManager;

public class FollowService {
    private static FollowService instance;
    private FileManager fileManager;

    private FollowService() {
        fileManager = FileManager.getInstance();
    }

    public static FollowService getInstance() {
        if (instance == null) {
            instance = new FollowService();
        }
        return instance;
    }

    public boolean seguir(String seguidor, String seguido) {
        if (seguidor.equals(seguido)) {
            return false;
        }

        if (yaSigue(seguidor, seguido)) {
            return false;
        }

        model.Usuario usuarioSeguido = AuthService.getInstance().getUsuario(seguido);
        if (usuarioSeguido != null && !usuarioSeguido.esPublico()) {
            return solicitarSeguir(seguidor, seguido);
        }

        agregarFollowing(seguidor, seguido);
        agregarFollower(seguido, seguidor);

        return true;
    }

    public boolean solicitarSeguir(String solicitante, String destinatario) {
        if (tieneSolicitudPendiente(solicitante, destinatario)) {
            return false;
        }

        Set<String> solicitudes = obtenerSolicitudes(destinatario);
        solicitudes.add(solicitante);
        guardarArchivo(fileManager.getRutaUsuario(destinatario) + "/requests.ins", solicitudes);

        return true;
    }

    public boolean aprobarSolicitud(String usuario, String solicitante) {
        Set<String> solicitudes = obtenerSolicitudes(usuario);
        if (!solicitudes.contains(solicitante)) {
            return false;
        }

        solicitudes.remove(solicitante);
        guardarArchivo(fileManager.getRutaUsuario(usuario) + "/requests.ins", solicitudes);

        agregarFollowing(solicitante, usuario);
        agregarFollower(usuario, solicitante);

        return true;
    }

    public boolean rechazarSolicitud(String usuario, String solicitante) {
        Set<String> solicitudes = obtenerSolicitudes(usuario);
        if (!solicitudes.contains(solicitante)) {
            return false;
        }

        solicitudes.remove(solicitante);
        guardarArchivo(fileManager.getRutaUsuario(usuario) + "/requests.ins", solicitudes);

        return true;
    }

    public Set<String> obtenerSolicitudes(String username) {
        return leerArchivo(fileManager.getRutaUsuario(username) + "/requests.ins");
    }

    public boolean tieneSolicitudPendiente(String solicitante, String destinatario) {
        Set<String> solicitudes = obtenerSolicitudes(destinatario);
        return solicitudes.contains(solicitante);
    }

    public int contarSolicitudes(String username) {
        return obtenerSolicitudes(username).size();
    }

    public boolean dejarDeSeguir(String seguidor, String seguido) {
        if (!yaSigue(seguidor, seguido)) {
            return false;
        }

        removerFollowing(seguidor, seguido);
        removerFollower(seguido, seguidor);

        return true;
    }

    public boolean yaSigue(String seguidor, String seguido) {
        Set<String> following = obtenerFollowing(seguidor);
        return following.contains(seguido);
    }

    public boolean sonAmigos(String usuario1, String usuario2) {
        return yaSigue(usuario1, usuario2) && yaSigue(usuario2, usuario1);
    }

    public Set<String> obtenerFollowers(String username) {
        return leerArchivo(fileManager.getRutaUsuario(username) + "/followers.ins");
    }

    public Set<String> obtenerFollowing(String username) {
        return leerArchivo(fileManager.getRutaUsuario(username) + "/following.ins");
    }

    public int contarFollowers(String username) {
        return obtenerFollowers(username).size();
    }

    public int contarFollowing(String username) {
        return obtenerFollowing(username).size();
    }

    private void agregarFollowing(String usuario, String seguido) {
        Set<String> following = obtenerFollowing(usuario);
        following.add(seguido);
        guardarArchivo(fileManager.getRutaUsuario(usuario) + "/following.ins", following);
    }

    private void agregarFollower(String usuario, String seguidor) {
        Set<String> followers = obtenerFollowers(usuario);
        followers.add(seguidor);
        guardarArchivo(fileManager.getRutaUsuario(usuario) + "/followers.ins", followers);
    }

    private void removerFollowing(String usuario, String seguido) {
        Set<String> following = obtenerFollowing(usuario);
        following.remove(seguido);
        guardarArchivo(fileManager.getRutaUsuario(usuario) + "/following.ins", following);
    }

    private void removerFollower(String usuario, String seguidor) {
        Set<String> followers = obtenerFollowers(usuario);
        followers.remove(seguidor);
        guardarArchivo(fileManager.getRutaUsuario(usuario) + "/followers.ins", followers);
    }

    public java.util.List<model.Usuario> buscarFollowingPorCoincidencia(String currentUsername, String query) {
        java.util.List<model.Usuario> resultados = new java.util.ArrayList<>();
        java.util.Set<String> following = obtenerFollowing(currentUsername);
        String queryLower = query.toLowerCase();

        for (String username : following) {
            model.Usuario usuario = AuthService.getInstance().getUsuario(username);
            if (usuario != null && usuario.isActivo() && 
                (usuario.getUsername().toLowerCase().contains(queryLower) ||
                 usuario.getNombre().toLowerCase().contains(queryLower))) {
                resultados.add(usuario);
            }
        }
        return resultados;
    }

    private Set<String> leerArchivo(String ruta) {
        Set<String> usuarios = new HashSet<>();
        File file = new File(ruta);

        if (!file.exists()) {
            return usuarios;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            while (dis.available() > 0) {
                usuarios.add(dis.readUTF());
            }
        } catch (IOException e) {
            System.err.println("Error leyendo archivo: " + e.getMessage());
        }

        return usuarios;
    }

    private void guardarArchivo(String ruta, Set<String> usuarios) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta))) {
            for (String usuario : usuarios) {
                dos.writeUTF(usuario);
            }
        } catch (IOException e) {
            System.err.println("Error guardando archivo: " + e.getMessage());
        }
    }
}
