package model;

public class Comentario implements Interactuable {
    private String autor;
    private String texto;
    private String fecha;
    private int likes = 0;
    private boolean liked = false;

    public Comentario(String autor, String texto, String fecha) {
        this.autor = autor;
        this.texto = texto;
        this.fecha = fecha;
    }

    public String getAutor() {
        return autor;
    }

    public String getTexto() {
        return texto;
    }

    public String getFecha() {
        return fecha;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
