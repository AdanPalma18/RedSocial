package model;

public class UsuarioPublico extends Usuario {
    public UsuarioPublico(String username, String password, String nombre, int edad, String genero, String fotoPerfil) {
        super(username, password, nombre, edad, genero, fotoPerfil);
    }

    @Override
    public boolean puedeVerPublicaciones(Usuario otro) {
        return true;
    }

    @Override
    public boolean puedeEnviarMensaje(Usuario otro) {
        return true;
    }

    @Override
    public boolean esPublico() {
        return true;
    }
}
