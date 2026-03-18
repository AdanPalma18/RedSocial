package services;

import model.Sticker;
import persistence.FileManager;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StickerService {
    private static StickerService instance;
    private FileManager fileManager;

    private StickerService() {
        fileManager = FileManager.getInstance();
        inicializarStickersGlobales();
    }

    public static StickerService getInstance() {
        if (instance == null) {
            instance = new StickerService();
        }
        return instance;
    }

    private void inicializarStickersGlobales() {
        String stickersPath = fileManager.getRaiz() + "/stickers_globales";
        File stickersDir = new File(stickersPath);
        
        if (!stickersDir.exists()) {
            stickersDir.mkdirs();
        }
    }

    public List<Sticker> obtenerStickersGlobales() {
        List<Sticker> stickers = new ArrayList<>();
        String stickersPath = fileManager.getRaiz() + "/stickers_globales";
        File stickersDir = new File(stickersPath);

        if (!stickersDir.exists()) return stickers;

        File[] files = stickersDir.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
        });
        
        if (files != null) {
            for (File file : files) {
                String nombre = file.getName().substring(0, file.getName().lastIndexOf('.'));
                stickers.add(new Sticker(nombre, file.getAbsolutePath()));
            }
        }
        return stickers;
    }

    public String leerContenidoSticker(String rutaSticker) {
        return rutaSticker; // Ahora devolvemos la ruta de la imagen, no su contenido de texto.
    }

    public boolean importarStickerPersonal(String username, String rutaOrigen, String nombreSticker) {
        try {
            File origen = new File(rutaOrigen);
            String destino = fileManager.getRutaUsuario(username) + "/stickers/" + nombreSticker;
            
            File destinoDir = new File(fileManager.getRutaUsuario(username) + "/stickers");
            if (!destinoDir.exists()) {
                destinoDir.mkdir();
            }

            try (FileInputStream fis = new FileInputStream(origen);
                 FileOutputStream fos = new FileOutputStream(destino)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }

            guardarStickerEnRegistro(username, nombreSticker, destino);
            return true;
        } catch (IOException e) {
            System.err.println("Error importando sticker: " + e.getMessage());
            return false;
        }
    }

    private void guardarStickerEnRegistro(String username, String nombre, String ruta) {
        String registroPath = fileManager.getRutaUsuario(username) + "/stickers.ins";
        
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(registroPath, true))) {
            dos.writeUTF(nombre);
            dos.writeUTF(ruta);
        } catch (IOException e) {
            System.err.println("Error guardando registro de sticker: " + e.getMessage());
        }
    }

    public List<Sticker> obtenerStickersPersonales(String username) {
        List<Sticker> stickers = new ArrayList<>();
        String registroPath = fileManager.getRutaUsuario(username) + "/stickers.ins";
        File file = new File(registroPath);

        if (!file.exists()) {
            return stickers;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            while (dis.available() > 0) {
                String nombre = dis.readUTF();
                String ruta = dis.readUTF();
                stickers.add(new Sticker(nombre, ruta));
            }
        } catch (IOException e) {
            System.err.println("Error leyendo stickers personales: " + e.getMessage());
        }

        return stickers;
    }
}
