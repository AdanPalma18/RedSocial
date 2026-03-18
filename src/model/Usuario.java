package model;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Usuario {
    protected String username;
    protected String password;
    protected String nombre;
    protected int edad;
    protected boolean activo;
    protected String genero;
    protected String fechaRegistro;
    protected String fotoPerfil;

    public Usuario(String username, String password, String nombre, int edad, String genero, String fotoPerfil) {
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.edad = edad;
        this.activo = true;
        this.genero = genero;
        this.fechaRegistro = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        this.fotoPerfil = fotoPerfil != null ? fotoPerfil : "";
    }

    public abstract boolean puedeVerPublicaciones(Usuario otro);
    public abstract boolean puedeEnviarMensaje(Usuario otro);
    public abstract boolean esPublico();

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getGenero() {
        return genero;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
