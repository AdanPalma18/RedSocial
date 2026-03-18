package model;

import java.io.Serializable;

public class Sticker implements Serializable {
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
