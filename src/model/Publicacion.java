package model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Publicacion {
    private String autor;
    private String contenido;
    private String imagenRuta;
    private String fecha;
    private List<String> hashtags;
    private List<String> menciones;
    private boolean liked = false;
    private boolean saved = false;
    private int likesCount = 0;
    private List<Comentario> comentarios = new ArrayList<>();

    public Publicacion(String autor, String contenido, String imagenRuta, String fecha) {
        this.autor = autor;
        this.contenido = contenido;
        this.imagenRuta = imagenRuta;
        this.fecha = fecha;
        this.hashtags = extraerHashtags(contenido);
        this.menciones = extraerMenciones(contenido);
    }

    private List<String> extraerHashtags(String texto) {
        List<String> tags = new ArrayList<>();
        Pattern pattern = Pattern.compile("#(\\w+)");
        Matcher matcher = pattern.matcher(texto);
        
        while (matcher.find()) {
            String tag = matcher.group(1);
            if (!tags.contains(tag)) {
                tags.add(tag);
            }
        }
        
        return tags;
    }

    private List<String> extraerMenciones(String texto) {
        List<String> mentions = new ArrayList<>();
        Pattern pattern = Pattern.compile("@(\\w+)");
        Matcher matcher = pattern.matcher(texto);
        
        while (matcher.find()) {
            String mention = matcher.group(1);
            if (!mentions.contains(mention)) {
                mentions.add(mention);
            }
        }
        
        return mentions;
    }

    public String getAutor() {
        return autor;
    }

    public String getContenido() {
        return contenido;
    }

    public String getImagenRuta() {
        return imagenRuta;
    }

    public String getFecha() {
        return fecha;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public List<String> getMenciones() {
        return menciones;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void agregarComentario(Comentario comentario) {
        this.comentarios.add(comentario);
    }
}
