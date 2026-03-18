package gui;

import java.awt.*;
import javax.swing.*;
import utils.Constants;
import utils.IconHelper;

public class SidebarPanel extends JPanel {
    private MainFrame mainFrame;
    private JPanel menuContainer;

    public SidebarPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setPreferredSize(new Dimension(Constants.SIDEBAR_WIDTH, Constants.DESKTOP_HEIGHT));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(219, 219, 219)));

        add(Box.createVerticalStrut(20));

        JLabel logo = new JLabel("Instagram");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));
        add(logo);

        menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(Color.WHITE);
        add(menuContainer);

        actualizar();

        add(Box.createVerticalGlue());
        add(createLogoutButton());
        add(Box.createVerticalStrut(16));
        
        services.InboxService.getInstance().agregarListener(new services.MessageBus.MessageListener() {
            @Override
            public void onNuevoMensaje(String remitente, String contenido) {
                SwingUtilities.invokeLater(() -> actualizar());
            }
        });
    }

    public void actualizar() {
        menuContainer.removeAll();
        menuContainer.add(createMenuButton("  Home", "home", "feed"));
        menuContainer.add(createMenuButton("  Search", "search", "search"));
        
        int unread = services.InboxService.getInstance().contarTotalMensajesNoLeidos(
            services.AuthService.getInstance().getUsuarioActual() != null ? 
            services.AuthService.getInstance().getUsuarioActual().getUsername() : ""
        );
        String inboxText = "  Inbox" + (unread > 0 ? " (" + unread + ")" : "");
        menuContainer.add(createMenuButton(inboxText, "inbox", "inbox"));

        menuContainer.add(createMenuButton("  Notifications", "heart", "requests"));
        menuContainer.add(createCreateButton());
        
        // Botón de Perfil con Foto Real
        menuContainer.add(createProfileButton());
        
        menuContainer.revalidate();
        menuContainer.repaint();
    }

    private JButton createProfileButton() {
        model.Usuario u = services.AuthService.getInstance().getUsuarioActual();
        String foto = (u != null) ? u.getFotoPerfil() : "";
        
        JButton btn = createMenuButton("  Profile", null, "profile");
        
        // Crear un icono que combine el CircularImageLabel
        CircularImageLabel cil = new CircularImageLabel(foto, 24);
        // Convertimos el componente cil a un icono
        btn.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                cil.setBounds(0, 0, 24, 24);
                g.translate(x, y);
                cil.paint(g);
                g.translate(-x, -y);
            }
            @Override public int getIconWidth() { return 24; }
            @Override public int getIconHeight() { return 24; }
        });
        
        return btn;
    }

    private JButton createMenuButton(String text, String iconName, String panelName) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Constants.SIDEBAR_WIDTH - 20, 50));
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 20));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon icon = (iconName != null) ? IconHelper.get(iconName, 22) : null;
        if (icon != null) btn.setIcon(icon);

        btn.addActionListener(e -> mainFrame.showPanel(panelName));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(250, 250, 250)); }
            public void mouseExited(java.awt.event.MouseEvent evt)  { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }

    private JButton createCreateButton() {
        JButton btn = new JButton("  Create");
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Constants.SIDEBAR_WIDTH - 20, 50));
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 20));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon icon = IconHelper.get("create", 22);
        if (icon != null) btn.setIcon(icon);

        btn.addActionListener(e -> {
            CreatePostDialog dialog = new CreatePostDialog(mainFrame);
            dialog.setVisible(true);
        });
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(250, 250, 250)); }
            public void mouseExited(java.awt.event.MouseEvent evt)  { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }

    private JButton createLogoutButton() {
        JButton btn = new JButton("  Log out");
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Constants.SIDEBAR_WIDTH - 20, 50));
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 20));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(142, 142, 142));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon icon = IconHelper.get("logout", 20);
        if (icon != null) btn.setIcon(icon);

        btn.addActionListener(e -> {
            services.AuthService.getInstance().logout();
            mainFrame.showPanel("login");
        });
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(250, 250, 250)); }
            public void mouseExited(java.awt.event.MouseEvent evt)  { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }
}
