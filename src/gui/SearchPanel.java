package gui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.*;
import services.*;

public class SearchPanel extends JPanel {
    private JPanel resultsPanel;
    private JTextField searchField;
    private JToggleButton followingToggle;

    public SearchPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        searchBarPanel.setBackground(Color.WHITE);
        searchBarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        searchField = new JTextField(25);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JButton searchBtn = new JButton("Buscar");
        searchBtn.setBackground(new Color(0, 149, 246));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchBtn.addActionListener(e -> buscar());
        searchField.addActionListener(e -> buscar());

        followingToggle = new JToggleButton("Solo Seguidos");
        followingToggle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        followingToggle.setFocusPainted(false);
        followingToggle.setBackground(Color.WHITE);
        followingToggle.addActionListener(e -> buscar());

        searchBarPanel.add(searchField);
        searchBarPanel.add(searchBtn);
        searchBarPanel.add(followingToggle);

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel infoLabel = new JLabel("<html><center>Busca por <b>@usuario</b>, <b>#hashtag</b><br>o nombre de usuario</center></html>", SwingConstants.CENTER);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsPanel.add(infoLabel);

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(searchBarPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }



    public void buscar() {
        String query = searchField.getText().trim();
        
        resultsPanel.removeAll();

        if (query.isEmpty()) {
            JLabel errorLabel = new JLabel("Por favor ingresa un término de búsqueda");
            errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            errorLabel.setForeground(Color.RED);
            resultsPanel.add(errorLabel);
            resultsPanel.revalidate();
            resultsPanel.repaint();
            return;
        }

        if (followingToggle.isSelected()) {
            buscarFollowing(query);
            return;
        }

        if (query.startsWith("#")) {
            buscarHashtags(query);
        } else if (query.startsWith("@")) {
            buscarMenciones(query);
        } else {
            buscarUsuarios(query);
        }
    }

    private void buscarFollowing(String query) {
        String current = AuthService.getInstance().getUsuarioActual().getUsername();
        List<Usuario> usuarios = FollowService.getInstance().buscarFollowingPorCoincidencia(current, query);

        if (usuarios.isEmpty()) {
            JLabel notFoundLabel = new JLabel("No sigues a ningún usuario que coincida con '" + query + "'");
            notFoundLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            notFoundLabel.setForeground(Color.GRAY);
            resultsPanel.add(notFoundLabel);
        } else {
            JLabel titleLabel = new JLabel("Siguendo (" + usuarios.size() + ")");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            resultsPanel.add(titleLabel);

            for (Usuario usuario : usuarios) {
                resultsPanel.add(createUserResult(usuario));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void buscarUsuarios(String query) {
        List<Usuario> usuarios = AuthService.getInstance().buscarUsuariosPorCoincidencia(query);
        Usuario usuarioActual = AuthService.getInstance().getUsuarioActual();

        if (usuarios.isEmpty()) {
            JLabel notFoundLabel = new JLabel("No se encontraron usuarios");
            notFoundLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            notFoundLabel.setForeground(Color.GRAY);
            resultsPanel.add(notFoundLabel);
        } else {
            for (Usuario usuario : usuarios) {
                if (usuarioActual != null && !usuario.getUsername().equals(usuarioActual.getUsername())) {
                    resultsPanel.add(createUserResult(usuario));
                }
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void buscarHashtags(String query) {
        String hashtag = query.startsWith("#") ? query.substring(1) : query;
        List<Publicacion> publicaciones = PostService.getInstance().buscarPorHashtag(hashtag);

        if (publicaciones.isEmpty()) {
            JLabel notFoundLabel = new JLabel("No se encontraron publicaciones con #" + hashtag);
            notFoundLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            notFoundLabel.setForeground(Color.GRAY);
            resultsPanel.add(notFoundLabel);
        } else {
            JLabel titleLabel = new JLabel("Publicaciones con #" + hashtag + " (" + publicaciones.size() + ")");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            resultsPanel.add(titleLabel);

            GridPostsPanel grid = new GridPostsPanel(3);
            for (Publicacion pub : publicaciones) {
                grid.addPostThumbnail(pub, true); // true para mostrar info de autor
            }
            resultsPanel.add(grid);
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void buscarMenciones(String query) {
        String username = query.startsWith("@") ? query.substring(1) : query;
        List<Publicacion> publicaciones = PostService.getInstance().buscarPorMencion(username);

        if (publicaciones.isEmpty()) {
            JLabel notFoundLabel = new JLabel("No se encontraron menciones de @" + username);
            notFoundLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            notFoundLabel.setForeground(Color.GRAY);
            resultsPanel.add(notFoundLabel);
        } else {
            JLabel titleLabel = new JLabel("Menciones de @" + username + " (" + publicaciones.size() + ")");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            resultsPanel.add(titleLabel);

            GridPostsPanel grid = new GridPostsPanel(3);
            for (Publicacion pub : publicaciones) {
                grid.addPostThumbnail(pub, true); // true para mostrar info de autor
            }
            resultsPanel.add(grid);
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JPanel createUserResult(Usuario usuario) {
        JPanel result = new JPanel(new BorderLayout(10, 5));
        result.setBackground(Color.WHITE);
        result.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        result.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        CircularImageLabel profilePic = new CircularImageLabel(usuario.getFotoPerfil(), 50);
        result.add(profilePic, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        JLabel usernameLabel = new JLabel(usuario.getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(new Color(0, 149, 246));
        usernameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        usernameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Window window = SwingUtilities.getWindowAncestor(SearchPanel.this);
                if (window instanceof MainFrame) {
                    ((MainFrame) window).showOtherProfile(usuario.getUsername());
                }
            }
        });

        JLabel nameLabel = new JLabel(usuario.getNombre() + " · " + usuario.getGenero() + " · " + usuario.getEdad() + " años");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        nameLabel.setForeground(Color.GRAY);

        int followers = FollowService.getInstance().contarFollowers(usuario.getUsername());
        int following = FollowService.getInstance().contarFollowing(usuario.getUsername());
        String tipoLabel = usuario.esPublico() ? "Pública" : "Privada";
        JLabel statsLabel = new JLabel(followers + " seguidores · " + following + " seguidos · " + tipoLabel);
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statsLabel.setForeground(Color.GRAY);

        textPanel.add(usernameLabel);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(statsLabel);

        result.add(textPanel, BorderLayout.CENTER);

        Usuario usuarioActual = AuthService.getInstance().getUsuarioActual();
        boolean yaSigue = FollowService.getInstance().yaSigue(
            usuarioActual.getUsername(),
            usuario.getUsername()
        );
        boolean solicitudPendiente = FollowService.getInstance().tieneSolicitudPendiente(
            usuarioActual.getUsername(),
            usuario.getUsername()
        );

        JButton followBtn;
        if (yaSigue) {
            followBtn = new JButton("Siguiendo");
            followBtn.setBackground(Color.LIGHT_GRAY);
            followBtn.setForeground(Color.BLACK);
        } else if (solicitudPendiente) {
            followBtn = new JButton("Solicitado");
            followBtn.setBackground(Color.LIGHT_GRAY);
            followBtn.setForeground(Color.BLACK);
            followBtn.setEnabled(false);
        } else {
            followBtn = new JButton(usuario.esPublico() ? "Seguir" : "Solicitar");
            followBtn.setBackground(new Color(0, 149, 246));
            followBtn.setForeground(Color.WHITE);
        }
        
        followBtn.setFont(new Font("Arial", Font.BOLD, 12));
        followBtn.setFocusPainted(false);
        followBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        followBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        followBtn.addActionListener(e -> {
            if (yaSigue) {
                FollowService.getInstance().dejarDeSeguir(
                    usuarioActual.getUsername(),
                    usuario.getUsername()
                );
            } else if (!solicitudPendiente) {
                FollowService.getInstance().seguir(
                    usuarioActual.getUsername(),
                    usuario.getUsername()
                );
            }
            buscar();
        });

        result.add(followBtn, BorderLayout.EAST);

        return result;
    }

    private JPanel createPostResult(Publicacion pub) {
        JPanel result = new JPanel(new BorderLayout(10, 5));
        result.setBackground(Color.WHITE);
        result.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));
        result.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);

        JLabel authorLabel = new JLabel("@" + pub.getAutor());
        authorLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JTextArea contentArea = new JTextArea(pub.getContenido());
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font("Arial", Font.PLAIN, 13));
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel dateLabel = new JLabel(pub.getFecha());
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        dateLabel.setForeground(Color.GRAY);

        leftPanel.add(authorLabel);
        leftPanel.add(contentArea);
        leftPanel.add(dateLabel);

        result.add(leftPanel, BorderLayout.CENTER);

        return result;
    }
}
