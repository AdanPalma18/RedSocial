package model;

public class Sticker {
    private String nombre;
    private String rutaImagen;

    public Sticker(String nombre, String rutaImagen) {
        this.nombre = nombre;
        this.rutaImagen = rutaImagen;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }
}
