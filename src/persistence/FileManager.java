package persistence;

import java.io.*;

public class FileManager {
    private static FileManager instance;
    private String raiz = "INSTA_RAIZ";

    private FileManager() {
        inicializarEstructura();
    }

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    private void inicializarEstructura() {
        File raizDir = new File(raiz);
        if (!raizDir.exists()) {
            raizDir.mkdir();
        }

        File usersFile = new File(raiz + "/users.ins");
        if (!usersFile.exists()) {
            try {
                usersFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creando users.ins: " + e.getMessage());
            }
        }

        File stickersDir = new File(raiz + "/stickers_globales");
        if (!stickersDir.exists()) {
            stickersDir.mkdir();
        }
    }

    public void crearCarpetaUsuario(String username) {
        File userDir = new File(raiz + "/" + username);
        if (!userDir.exists()) {
            userDir.mkdir();
        }

        File imagenesDir = new File(raiz + "/" + username + "/imagenes");
        if (!imagenesDir.exists()) {
            imagenesDir.mkdir();
        }
    }

    public void crearArchivosUsuario(String username) {
        String userPath = raiz + "/" + username;

        try {
            new File(userPath + "/followers.ins").createNewFile();
            new File(userPath + "/following.ins").createNewFile();
            new File(userPath + "/insta.ins").createNewFile();
            new File(userPath + "/inbox.ins").createNewFile();
            new File(userPath + "/stickers.ins").createNewFile();
            new File(userPath + "/requests.ins").createNewFile();
        } catch (IOException e) {
            System.err.println("Error creando archivos de usuario: " + e.getMessage());
        }
    }

    public String getRaiz() {
        return raiz;
    }

    public String getRutaUsuario(String username) {
        return raiz + "/" + username;
    }

    public String copiarFotoPerfil(String username, String rutaOrigen) {
        try {
            File origen = new File(rutaOrigen);
            String extension = "";
            int dotIdx = rutaOrigen.lastIndexOf(".");
            if (dotIdx >= 0) {
                extension = rutaOrigen.substring(dotIdx);
            }
            
            String destino = raiz + "/" + username + "/imagenes/perfil" + extension;
            File destinoFile = new File(destino);
            
            copiarArchivo(origen, destinoFile);
            System.out.println("Foto de perfil copiada a: " + destinoFile.getAbsolutePath());
            
            return destinoFile.getAbsolutePath();
        } catch (Exception e) {
            System.err.println("Error copiando foto de perfil: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public String copiarFotoPublicacion(String username, String rutaOrigen) {
        try {
            File origen = new File(rutaOrigen);
            String extension = rutaOrigen.substring(rutaOrigen.lastIndexOf("."));
            long timestamp = System.currentTimeMillis();
            String destino = raiz + "/" + username + "/imagenes/post_" + timestamp + extension;
            
            File destinoFile = new File(destino);
            copiarArchivo(origen, destinoFile);
            
            return destinoFile.getAbsolutePath();
        } catch (Exception e) {
            System.err.println("Error copiando foto de publicación: " + e.getMessage());
            return "";
        }
    }

    private void copiarArchivo(File origen, File destino) throws IOException {
        try (FileInputStream fis = new FileInputStream(origen);
             FileOutputStream fos = new FileOutputStream(destino)) {
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }
}
