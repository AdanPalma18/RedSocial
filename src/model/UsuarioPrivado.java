package model;

import services.FollowService;

public class UsuarioPrivado extends Usuario {
    public UsuarioPrivado(String username, String password, String nombre, int edad, String genero, String fotoPerfil) {
        super(username, password, nombre, edad, genero, fotoPerfil);
    }

    @Override
    public boolean puedeVerPublicaciones(Usuario otro) {
        if (this.username.equals(otro.getUsername())) {
            return true;
        }
        return FollowService.getInstance().sonAmigos(this.username, otro.getUsername());
    }

    @Override
    public boolean puedeEnviarMensaje(Usuario otro) {
        return FollowService.getInstance().sonAmigos(this.username, otro.getUsername());
    }

    @Override
    public boolean esPublico() {
        return false;
    }
}
