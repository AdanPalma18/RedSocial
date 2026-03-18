package gui;

import java.awt.*;
import javax.swing.*;
import services.*;
import model.*;
import java.util.List;

public class OtherProfilePanel extends JPanel {
    private GridPostsPanel gridPosts;
    private JLabel usernameLabel;
    private JLabel publicacionesLabel;
    private JLabel seguidoresLabel;
    private JLabel seguidosLabel;
    private JLabel bioLabel;
    private CircularImageLabel profilePic;
    private JButton followBtn;
    private Usuario usuarioMostrado;
    private MainFrame mainFrame;

    public OtherProfilePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        gridPosts = new GridPostsPanel(4);
        JScrollPane scrollPane = new JScrollPane(gridPosts);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        topPanel.setBackground(Color.WHITE);

        profilePic = new CircularImageLabel("", 100);
        topPanel.add(profilePic);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(Color.WHITE);
        
        usernameLabel = new JLabel("username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel countsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        countsPanel.setBackground(Color.WHITE);
        countsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        publicacionesLabel = new JLabel("0 publicaciones");
        publicacionesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        seguidoresLabel = new JLabel("0 seguidores");
        seguidoresLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        seguidosLabel = new JLabel("0 seguidos");
        seguidosLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        countsPanel.add(publicacionesLabel);
        countsPanel.add(seguidoresLabel);
        countsPanel.add(seguidosLabel);
        
        bioLabel = new JLabel("Biografía del usuario");
        bioLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        bioLabel.setForeground(Color.GRAY);
        bioLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        followBtn = new JButton("Seguir");
        followBtn.setBackground(new Color(0, 149, 246));
        followBtn.setForeground(Color.WHITE);
        followBtn.setFont(new Font("Arial", Font.BOLD, 12));
        followBtn.setFocusPainted(false);
        followBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        followBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        statsPanel.add(usernameLabel);
        statsPanel.add(Box.createVerticalStrut(5));
        statsPanel.add(countsPanel);
        statsPanel.add(Box.createVerticalStrut(5));
        statsPanel.add(bioLabel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(followBtn);

        topPanel.add(statsPanel);
        header.add(topPanel);

        JButton btnVolver = new JButton("← Volver");
        btnVolver.setBackground(Color.WHITE);
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 12));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> mainFrame.showPanel("search"));
        
        JPanel volverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        volverPanel.setBackground(Color.WHITE);
        volverPanel.add(btnVolver);
        header.add(volverPanel);

        JSeparator separator = new JSeparator();
        header.add(Box.createVerticalStrut(15));
        header.add(separator);
        header.add(Box.createVerticalStrut(10));

        JLabel publicacionesTitle = new JLabel("PUBLICACIONES");
        publicacionesTitle.setFont(new Font("Arial", Font.BOLD, 12));
        publicacionesTitle.setForeground(Color.GRAY);
        header.add(publicacionesTitle);

        return header;
    }

    public void cargarPerfil(String username) {
        usuarioMostrado = AuthService.getInstance().getUsuario(username);
        if (usuarioMostrado == null) {
            return;
        }

        Usuario usuarioActual = AuthService.getInstance().getUsuarioActual();

        usernameLabel.setText(usuarioMostrado.getUsername());
        
        String info = String.format("<html>%s<br>%s · %d años · %s<br>Registrado: %s · Estado: %s</html>",
            usuarioMostrado.getNombre(),
            usuarioMostrado.getGenero().equals("M") ? "Masculino" : "Femenino",
            usuarioMostrado.getEdad(),
            usuarioMostrado.esPublico() ? "Cuenta Pública" : "Cuenta Privada",
            usuarioMostrado.getFechaRegistro(),
            usuarioMostrado.isActivo() ? "Activo" : "Inactivo"
        );
        bioLabel.setText(info);

        if (usuarioMostrado.getFotoPerfil() != null && !usuarioMostrado.getFotoPerfil().isEmpty()) {
            profilePic.setImagePath(usuarioMostrado.getFotoPerfil());
        }

        int numPublicaciones = PostService.getInstance().contarPublicaciones(usuarioMostrado.getUsername());
        int numSeguidores = FollowService.getInstance().contarFollowers(usuarioMostrado.getUsername());
        int numSeguidos = FollowService.getInstance().contarFollowing(usuarioMostrado.getUsername());

        publicacionesLabel.setText(numPublicaciones + " publicaciones");
        seguidoresLabel.setText(numSeguidores + " seguidores");
        seguidosLabel.setText(numSeguidos + " seguidos");

        boolean yaSigue = FollowService.getInstance().yaSigue(
            usuarioActual.getUsername(),
            usuarioMostrado.getUsername()
        );
        boolean solicitudPendiente = FollowService.getInstance().tieneSolicitudPendiente(
            usuarioActual.getUsername(),
            usuarioMostrado.getUsername()
        );

        if (yaSigue) {
            followBtn.setText("Siguiendo");
            followBtn.setBackground(Color.LIGHT_GRAY);
            followBtn.setForeground(Color.BLACK);
        } else if (solicitudPendiente) {
            followBtn.setText("Solicitado");
            followBtn.setBackground(Color.LIGHT_GRAY);
            followBtn.setForeground(Color.BLACK);
            followBtn.setEnabled(false);
        } else {
            followBtn.setText(usuarioMostrado.esPublico() ? "Seguir" : "Solicitar");
            followBtn.setBackground(new Color(0, 149, 246));
            followBtn.setForeground(Color.WHITE);
            followBtn.setEnabled(true);
        }

        for (java.awt.event.ActionListener al : followBtn.getActionListeners()) {
            followBtn.removeActionListener(al);
        }
        
        followBtn.addActionListener(e -> {
            if (yaSigue) {
                FollowService.getInstance().dejarDeSeguir(
                    usuarioActual.getUsername(),
                    usuarioMostrado.getUsername()
                );
            } else if (!solicitudPendiente) {
                FollowService.getInstance().seguir(
                    usuarioActual.getUsername(),
                    usuarioMostrado.getUsername()
                );
            }
            cargarPerfil(username);
        });

        gridPosts.limpiar();
        
        if (usuarioMostrado.puedeVerPublicaciones(usuarioActual)) {
            List<Publicacion> publicaciones = PostService.getInstance().obtenerPublicacionesUsuario(usuarioMostrado.getUsername());
            
            if (publicaciones.isEmpty()) {
                gridPosts.mostrarMensajeVacio("No hay publicaciones");
            } else {
                for (Publicacion pub : publicaciones) {
                    gridPosts.addPostThumbnail(pub);
                }
            }
        } else {
            gridPosts.mostrarMensajeVacio("Esta cuenta es privada");
        }
    }
}
