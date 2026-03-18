package services;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.*;
import persistence.FileManager;

public class AuthService {
    private static AuthService instance;
    private final Map<String, Usuario> usuariosEnMemoria;
    private Usuario usuarioActual;
    private final FileManager fileManager;
    private FileChannel sessionChannel;
    private FileLock sessionLock;

    private AuthService() {
        usuariosEnMemoria = new HashMap<>();
        fileManager = FileManager.getInstance();
        cargarUsuarios();

        // Asegurar liberación al cerrar la aplicación
        Runtime.getRuntime().addShutdownHook(new Thread(this::liberarBloqueoSesion));
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    private synchronized void cargarUsuarios() {
        File usersFile = new File(fileManager.getRaiz() + "/users.ins");
        if (!usersFile.exists()) {
            return;
        }

        usuariosEnMemoria.clear(); // Evitar duplicados al recargar
        try (DataInputStream dis = new DataInputStream(new FileInputStream(usersFile))) {
            while (dis.available() > 0) {
                String username = dis.readUTF();
                String password = dis.readUTF();
                String nombre = dis.readUTF();
                int edad = dis.readInt();
                boolean esPublico = dis.readBoolean();
                String genero = dis.readUTF();
                String fechaRegistro = dis.readUTF();
                String fotoPerfil = dis.readUTF();
                boolean activo = dis.readBoolean();

                Usuario usuario;
                if (esPublico) {
                    usuario = new UsuarioPublico(username, password, nombre, edad, genero, fotoPerfil);
                } else {
                    usuario = new UsuarioPrivado(username, password, nombre, edad, genero, fotoPerfil);
                }
                usuario.setActivo(activo);
                usuariosEnMemoria.put(username, usuario);
                
                // Si este es el usuario logueado en esta instancia, actualizar su referencia
                if (usuarioActual != null && usuarioActual.getUsername().equals(username)) {
                    usuarioActual = usuario;
                }
            }
        } catch (IOException e) {
            // Error silencioso si es el fin del archivo o formato antiguo
        }
    }

    public boolean registrar(String username, String password, String nombre, int edad, String genero, boolean esPublico, String rutaFoto) {
        cargarUsuarios(); // Sincronizar antes de validar duplicados
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.length() < 4) {
            return false;
        }
        if (usuariosEnMemoria.containsKey(username)) {
            return false;
        }

        fileManager.crearCarpetaUsuario(username);
        fileManager.crearArchivosUsuario(username);

        String fotoPerfilPath = "";
        if (rutaFoto != null && !rutaFoto.isEmpty()) {
            fotoPerfilPath = fileManager.copiarFotoPerfil(username, rutaFoto);
        }

        Usuario nuevoUsuario;
        if (esPublico) {
            nuevoUsuario = new UsuarioPublico(username, password, nombre, edad, genero, fotoPerfilPath);
        } else {
            nuevoUsuario = new UsuarioPrivado(username, password, nombre, edad, genero, fotoPerfilPath);
        }

        usuariosEnMemoria.put(username, nuevoUsuario);
        guardarUsuario(nuevoUsuario, esPublico);

        return true;
    }

    private void guardarUsuario(Usuario usuario, boolean esPublico) {
        File usersFile = new File(fileManager.getRaiz() + "/users.ins");

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(usersFile, true))) {
            dos.writeUTF(usuario.getUsername());
            dos.writeUTF(usuario.getPassword());
            dos.writeUTF(usuario.getNombre());
            dos.writeInt(usuario.getEdad());
            dos.writeBoolean(esPublico);
            dos.writeUTF(usuario.getGenero());
            dos.writeUTF(usuario.getFechaRegistro());
            dos.writeUTF(usuario.getFotoPerfil());
            dos.writeBoolean(usuario.isActivo());
        } catch (IOException e) {
            System.err.println("Error guardando usuario: " + e.getMessage());
        }
    }

    public boolean login(String username, String password) {
        cargarUsuarios(); // Sincronizar para ver usuarios creados en otras instancias
        Usuario usuario = usuariosEnMemoria.get(username);
        if (usuario != null && usuario.getPassword().equals(password)) {
            if (!usuario.isActivo()) {
                return false; // No se puede loguear si no está activo
            }
            
            // INTENTAR BLOQUEO DE SESIÓN
            if (!intentarBloquearSesion(username)) {
                return false; // Sesión ya abierta en otra ventana
            }

            usuarioActual = usuario;
            return true;
        }
        return false;
    }

    private boolean intentarBloquearSesion(String username) {
        try {
            File sessionFile = new File(fileManager.getRutaUsuario(username) + "/session.lock");
            if (!sessionFile.exists()) {
                sessionFile.createNewFile();
            }
            
            this.sessionChannel = FileChannel.open(sessionFile.toPath(), StandardOpenOption.WRITE, StandardOpenOption.READ);
            this.sessionLock = sessionChannel.tryLock();
            
            if (this.sessionLock == null) {
                // No se pudo obtener el bloqueo (otro proceso lo tiene)
                sessionChannel.close();
                return false;
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error al intentar bloquear sesión: " + e.getMessage());
            return false;
        }
    }

    public void logout() {
        liberarBloqueoSesion();
        usuarioActual = null;
    }

    private void liberarBloqueoSesion() {
        try {
            if (sessionLock != null) {
                sessionLock.release();
                sessionLock = null;
            }
            if (sessionChannel != null) {
                sessionChannel.close();
                sessionChannel = null;
            }
        } catch (IOException e) {
            System.err.println("Error al liberar bloqueo de sesión: " + e.getMessage());
        }
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Usuario getUsuario(String username) {
        // Solo cargar si no está en memoria para evitar redundancia masiva
        if (!usuariosEnMemoria.containsKey(username)) {
            cargarUsuarios();
        }
        return usuariosEnMemoria.get(username);
    }

    public Map<String, Usuario> getUsuariosEnMemoria() {
        return usuariosEnMemoria;
    }

    public boolean existeUsuario(String username) {
        cargarUsuarios();
        return usuariosEnMemoria.containsKey(username);
    }

    public boolean actualizarPerfil(String username, String nombre, int edad, String genero, boolean esPublico, String nuevaRutaFoto) {
        Usuario u = usuariosEnMemoria.get(username);
        if (u == null) return false;

        u.setNombre(nombre);
        u.setEdad(edad);
        u.setGenero(genero);
        
        // El tipo de cuenta puede cambiar entre pública y privada
        if (esPublico != u.esPublico()) {
            Usuario nuevoU;
            if (esPublico) {
                nuevoU = new UsuarioPublico(u.getUsername(), u.getPassword(), nombre, edad, genero, u.getFotoPerfil());
            } else {
                nuevoU = new UsuarioPrivado(u.getUsername(), u.getPassword(), nombre, edad, genero, u.getFotoPerfil());
            }
            nuevoU.setActivo(u.isActivo());
            nuevoU.setFechaRegistro(u.getFechaRegistro());
            usuariosEnMemoria.put(username, nuevoU);
            u = nuevoU;
        }

        if (nuevaRutaFoto != null && !nuevaRutaFoto.isEmpty()) {
            String fotoPath = fileManager.copiarFotoPerfil(username, nuevaRutaFoto);
            u.setFotoPerfil(fotoPath);
        }

        if (usuarioActual != null && usuarioActual.getUsername().equals(username)) {
            usuarioActual = u;
        }

        reescribirArchivo();
        SocketBusClient.getInstance().notifyProfileUpdate(username);
        return true;
    }

    public List<Usuario> buscarUsuariosPorCoincidencia(String query) {
        cargarUsuarios();
        List<Usuario> resultados = new ArrayList<>();
        String queryLower = query.toLowerCase();

        for (Usuario usuario : usuariosEnMemoria.values()) {
            if (usuario.isActivo() && 
                (usuario.getUsername().toLowerCase().contains(queryLower) ||
                 usuario.getNombre().toLowerCase().contains(queryLower))) {
                resultados.add(usuario);
            }
        }

        return resultados;
    }

    public boolean desactivarCuenta(String username) {
        Usuario usuario = usuariosEnMemoria.get(username);
        if (usuario == null) {
            return false;
        }

        usuario.setActivo(false);
        reescribirArchivo();
        return true;
    }

    public boolean activarCuenta(String username) {
        Usuario usuario = usuariosEnMemoria.get(username);
        if (usuario == null) {
            return false;
        }

        usuario.setActivo(true);
        reescribirArchivo();
        return true;
    }

    public void recargarUsuariosLocal() {
        cargarUsuarios();
    }

    private void reescribirArchivo() {
        File usersFile = new File(fileManager.getRaiz() + "/users.ins");

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(usersFile))) {
            for (Usuario usuario : usuariosEnMemoria.values()) {
                dos.writeUTF(usuario.getUsername());
                dos.writeUTF(usuario.getPassword());
                dos.writeUTF(usuario.getNombre());
                dos.writeInt(usuario.getEdad());
                dos.writeBoolean(usuario.esPublico());
                dos.writeUTF(usuario.getGenero());
                dos.writeUTF(usuario.getFechaRegistro());
                dos.writeUTF(usuario.getFotoPerfil());
                dos.writeBoolean(usuario.isActivo());
            }
        } catch (IOException e) {
            System.err.println("Error reescribiendo archivo de usuarios: " + e.getMessage());
        }
    }

    public void crearUsuariosBootstrap() {
        File usersFile = new File(fileManager.getRaiz() + "/users.ins");
        if (usersFile.exists() && usersFile.length() > 0) {
            return;
        }

        // Todos con la misma password "1234"
        registrar("maria_garcia", "1234", "María García", 25, "F", true, null);
        registrar("juan_lopez", "1234", "Juan López", 28, "M", true, null);
        registrar("ana_martinez", "1234", "Ana Martínez", 23, "F", false, null);
        registrar("carlos_ruiz", "1234", "Carlos Ruiz", 30, "M", true, null);

        PostService postService = PostService.getInstance();
        postService.crearPublicacion("maria_garcia", "¡Hola a todos! Esta es mi primera publicación #primerpost", "");
        postService.crearPublicacion("maria_garcia", "Disfrutando del día ☀️ @juan_lopez", "");
        postService.crearPublicacion("juan_lopez", "Nuevo en Instagram! #nuevoaqui", "");
        postService.crearPublicacion("juan_lopez", "Gran día con amigos @maria_garcia #social", "");
        postService.crearPublicacion("ana_martinez", "Mi cuenta privada 🔒 #privado", "");
        postService.crearPublicacion("carlos_ruiz", "Explorando la ciudad 🏙️ #citylife", "");

        FollowService followService = FollowService.getInstance();
        followService.seguir("maria_garcia", "juan_lopez");
        followService.seguir("juan_lopez", "maria_garcia");
        followService.seguir("maria_garcia", "ana_martinez");
        followService.seguir("carlos_ruiz", "maria_garcia");
        followService.seguir("maria_garcia", "carlos_ruiz");

        InboxService inboxService = InboxService.getInstance();
        inboxService.enviarMensaje("maria_garcia", "juan_lopez", "¡Hola Juan! ¿Cómo estás?");
        inboxService.enviarMensaje("juan_lopez", "maria_garcia", "¡Hola María! Todo bien, gracias");

        System.out.println("Bootstrap completado: Usuarios creados con contraseña '1234'");
    }
}
