package services;

import model.Usuario;
import model.Publicacion;
import java.util.*;

public class SearchService {
    private static SearchService instance;
    private AuthService authService;
    private PostService postService;

    private SearchService() {
        authService = AuthService.getInstance();
        postService = PostService.getInstance();
    }

    public static SearchService getInstance() {
        if (instance == null) {
            instance = new SearchService();
        }
        return instance;
    }

    public List<String> buscarUsuarios(String query) {
        List<String> resultados = new ArrayList<>();
        
        // Aquí se buscaría en todos los usuarios registrados
        // Por ahora retornamos una lista vacía
        
        return resultados;
    }

    public List<Publicacion> buscarPorHashtag(String hashtag) {
        List<Publicacion> resultados = new ArrayList<>();
        
        // Buscar en todas las publicaciones que contengan el hashtag
        
        return resultados;
    }

    public List<Publicacion> buscarPorMencion(String username) {
        List<Publicacion> resultados = new ArrayList<>();
        
        // Buscar publicaciones que mencionen al usuario
        
        return resultados;
    }
}
