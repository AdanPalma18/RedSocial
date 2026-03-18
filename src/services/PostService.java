package services;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import model.Publicacion;
import model.Usuario;
import persistence.FileManager;

public class PostService {
    private static PostService instance;
    private FileManager fileManager;
    private FollowService followService;
    private AuthService authService;

    private PostService() {
        fileManager = FileManager.getInstance();
        followService = FollowService.getInstance();
        authService = AuthService.getInstance();
    }

    public static PostService getInstance() {
        if (instance == null) {
            instance = new PostService();
        }
        return instance;
    }

    /**
     * Overloaded version that accepts a separate hashtags string.
     * Hashtags are appended to the content before saving.
     */
    public boolean crearPublicacion(String autor, String contenido, String hashtags, String imagenRuta) {
        String contenidoFinal = contenido;
        if (hashtags != null && !hashtags.trim().isEmpty()) {
            contenidoFinal = (contenido.isEmpty() ? "" : contenido + " ") + hashtags.trim();
        }
        return crearPublicacion(autor, contenidoFinal, imagenRuta);
    }

    public boolean crearPublicacion(String autor, String contenido, String imagenRuta) {
        if (contenido.length() > 220) {
            return false;
        }

        String imagenFinal = "";
        if (imagenRuta != null && !imagenRuta.isEmpty()) {
            imagenFinal = fileManager.copiarFotoPublicacion(autor, imagenRuta);
        }

        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String rutaInsta = fileManager.getRutaUsuario(autor) + "/insta.ins";

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(rutaInsta, true))) {
            dos.writeUTF(autor);
            dos.writeUTF(contenido);
            dos.writeUTF(imagenFinal);
            dos.writeUTF(fecha);
            return true;
        } catch (IOException e) {
            System.err.println("Error creando publicación: " + e.getMessage());
            return false;
        }
    }

    public void guardarComentario(String postAutor, String postFecha, model.Comentario c) {
        String rutaComments = fileManager.getRutaUsuario(postAutor) + "/comments.ins";
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(rutaComments, true))) {
            dos.writeUTF(postFecha);
            dos.writeUTF(c.getAutor());
            dos.writeUTF(c.getTexto());
            dos.writeUTF(c.getFecha());
            dos.writeInt(c.getLikes());
        } catch (IOException e) {
            System.err.println("Error guardando comentario: " + e.getMessage());
        }
    }

    private void cargarComentariosParaPost(Publicacion post) {
        String rutaComments = fileManager.getRutaUsuario(post.getAutor()) + "/comments.ins";
        File file = new File(rutaComments);
        if (!file.exists()) return;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            while (dis.available() > 0) {
                String postFecha = dis.readUTF();
                String autorC = dis.readUTF();
                String textoC = dis.readUTF();
                String fechaC = dis.readUTF();
                int likesC = dis.readInt();

                if (postFecha.equals(post.getFecha())) {
                    model.Comentario c = new model.Comentario(autorC, textoC, fechaC);
                    c.setLikes(likesC);
                    post.agregarComentario(c);
                }
            }
        } catch (IOException e) {
            System.err.println("Error cargando comentarios: " + e.getMessage());
        }
    }

    public List<Publicacion> obtenerFeed(String username) {
        List<Publicacion> feed = new ArrayList<>();
        Usuario usuarioActual = authService.getUsuario(username);

        feed.addAll(obtenerPublicacionesUsuario(username));

        Set<String> following = followService.obtenerFollowing(username);
        for (String seguido : following) {
            Usuario usuarioSeguido = authService.getUsuario(seguido);

            if (usuarioSeguido != null && usuarioSeguido.isActivo()) {
                if (usuarioSeguido.puedeVerPublicaciones(usuarioActual)) {
                    feed.addAll(obtenerPublicacionesUsuario(seguido));
                }
            }
        }

        feed.sort((p1, p2) -> p2.getFecha().compareTo(p1.getFecha()));

        return feed;
    }

    public List<Publicacion> obtenerPublicacionesUsuario(String username) {
        List<Publicacion> publicaciones = new ArrayList<>();
        String rutaInsta = fileManager.getRutaUsuario(username) + "/insta.ins";
        File file = new File(rutaInsta);

        if (!file.exists()) {
            return publicaciones;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            while (dis.available() > 0) {
                String autor = dis.readUTF();
                String contenido = dis.readUTF();
                String imagenRuta = dis.readUTF();
                String fecha = dis.readUTF();

                Publicacion p = new Publicacion(autor, contenido, imagenRuta, fecha);
                cargarComentariosParaPost(p);
                publicaciones.add(p);
            }
        } catch (IOException e) {
            System.err.println("Error leyendo publicaciones: " + e.getMessage());
        }

        return publicaciones;
    }

    public int contarPublicaciones(String username) {
        return obtenerPublicacionesUsuario(username).size();
    }

    public List<Publicacion> buscarPorHashtag(String hashtag) {
        List<Publicacion> resultados = new ArrayList<>();
        Map<String, Usuario> usuarios = authService.getUsuariosEnMemoria();

        for (Usuario usuario : usuarios.values()) {
            if (!usuario.isActivo()) {
                continue;
            }

            List<Publicacion> publicaciones = obtenerPublicacionesUsuario(usuario.getUsername());
            for (Publicacion pub : publicaciones) {
                if (pub.getHashtags().contains(hashtag) && !resultados.contains(pub)) {
                    resultados.add(pub);
                }
            }
        }

        return resultados;
    }

    public List<Publicacion> buscarPorMencion(String username) {
        List<Publicacion> resultados = new ArrayList<>();
        Map<String, Usuario> usuarios = authService.getUsuariosEnMemoria();

        for (Usuario usuario : usuarios.values()) {
            if (!usuario.isActivo()) {
                continue;
            }

            List<Publicacion> publicaciones = obtenerPublicacionesUsuario(usuario.getUsername());
            for (Publicacion pub : publicaciones) {
                if (pub.getMenciones().contains(username) && !resultados.contains(pub)) {
                    resultados.add(pub);
                }
            }
        }

        return resultados;
    }
}
