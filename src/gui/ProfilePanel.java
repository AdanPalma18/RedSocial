package gui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.*;
import services.*;
import utils.IconHelper;

public class ProfilePanel extends JPanel {
    private GridPostsPanel gridPosts;
    private JLabel usernameLabel;
    private JLabel publicacionesLabel;
    private JLabel seguidoresLabel;
    private JLabel seguidosLabel;
    private JLabel bioLabel;
    private CircularImageLabel profilePic;

    public ProfilePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        int cols = (utils.Constants.MODO == utils.Constants.Modo.MOBILE) ? 3 : 4;
        gridPosts = new GridPostsPanel(cols);
        JScrollPane scrollPane = new JScrollPane(gridPosts);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(30, 10));
        topPanel.setBackground(Color.WHITE);

        profilePic = new CircularImageLabel("", 100);
        topPanel.add(profilePic, BorderLayout.WEST);

        JPanel infoWrapper = new JPanel(new BorderLayout());
        infoWrapper.setBackground(Color.WHITE);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(Color.WHITE);
        
        usernameLabel = new JLabel("username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 20));
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
        bioLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bioLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsPanel.add(usernameLabel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(countsPanel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(bioLabel);

        infoWrapper.add(statsPanel, BorderLayout.CENTER);

        // Settings button
        ImageIcon settingsIcon = IconHelper.get("settings", 24);
        JButton settingsBtn = (settingsIcon != null) ? new JButton(settingsIcon) : new JButton("⚙");
        if (settingsIcon == null) {
            settingsBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        }
        settingsBtn.setBorderPainted(false);
        settingsBtn.setContentAreaFilled(false);
        settingsBtn.setFocusPainted(false);
        settingsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsBtn.setToolTipText("Ajustes del perfil");
        settingsBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainFrame) {
                ProfileSettingsDialog dialog = new ProfileSettingsDialog((MainFrame) window);
                dialog.setVisible(true);
                cargarPerfil(); // Recargar después de cerrar
            }
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(settingsBtn);
        
        infoWrapper.add(btnPanel, BorderLayout.EAST);

        topPanel.add(infoWrapper, BorderLayout.CENTER);
        header.add(topPanel);

        JSeparator separator = new JSeparator();
        header.add(Box.createVerticalStrut(25));
        header.add(separator);
        header.add(Box.createVerticalStrut(10));

        JLabel publicacionesTitle = new JLabel("PUBLICACIONES");
        publicacionesTitle.setFont(new Font("Arial", Font.BOLD, 12));
        publicacionesTitle.setForeground(Color.GRAY);
        publicacionesTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(publicacionesTitle);

        return header;
    }

    public void cargarPerfil() {
        Usuario usuarioActual = AuthService.getInstance().getUsuarioActual();
        if (usuarioActual == null) {
            return;
        }

        usernameLabel.setText(usuarioActual.getUsername());
        bioLabel.setText(usuarioActual.getNombre());

        if (usuarioActual.getFotoPerfil() != null && !usuarioActual.getFotoPerfil().isEmpty()) {
            profilePic.setImagePath(usuarioActual.getFotoPerfil());
        }

        int numPublicaciones = PostService.getInstance().contarPublicaciones(usuarioActual.getUsername());
        int numSeguidores = FollowService.getInstance().contarFollowers(usuarioActual.getUsername());
        int numSeguidos = FollowService.getInstance().contarFollowing(usuarioActual.getUsername());

        publicacionesLabel.setText(numPublicaciones + " publicaciones");
        seguidoresLabel.setText(numSeguidores + " seguidores");
        seguidosLabel.setText(numSeguidos + " seguidos");

        gridPosts.limpiar();
        
        List<Publicacion> publicaciones = PostService.getInstance().obtenerPublicacionesUsuario(usuarioActual.getUsername());
        
        if (publicaciones.isEmpty()) {
            gridPosts.mostrarMensajeVacio("No tienes publicaciones aún");
        } else {
            for (Publicacion pub : publicaciones) {
                gridPosts.addPostThumbnail(pub);
            }
        }
    }
}
