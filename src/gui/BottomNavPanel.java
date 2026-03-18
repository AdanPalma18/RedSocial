package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import utils.Constants;
import utils.IconHelper;

public class BottomNavPanel extends JPanel {
    private final MainFrame mainFrame;

    public BottomNavPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(219, 219, 219)));
        setPreferredSize(new Dimension(Constants.MOBILE_WIDTH, 50));
        actualizar();
        
        services.InboxService.getInstance().agregarListener(new services.MessageBus.MessageListener() {
            @Override
            public void onNuevoMensaje(String remitente, String contenido) {
                SwingUtilities.invokeLater(() -> actualizar());
            }
        });
    }

    public void actualizar() {
        removeAll();
        setLayout(new GridLayout(1, 6));

        add(createNavButton("home", "feed"));
        add(createNavButton("search", "search"));
        add(createCreateButton());
        add(createNavButton("heart", "requests"));
        
        // Inbox con Badge
        int unread = services.InboxService.getInstance().contarTotalMensajesNoLeidos(
            services.AuthService.getInstance().getUsuarioActual() != null ? 
            services.AuthService.getInstance().getUsuarioActual().getUsername() : ""
        );
        add(createInboxButton(unread));
        
        add(createProfileButton());
        
        revalidate();
        repaint();
    }

    private JButton createInboxButton(int unread) {
        JButton btn = createNavButton("inbox", "inbox");
        if (unread > 0) {
            Icon original = btn.getIcon();
            btn.setIcon(new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    original.paintIcon(c, g, x, y);
                    g.setColor(Color.RED);
                    g.fillOval(x + 15, y, 10, 10);
                }
                @Override public int getIconWidth() { return original.getIconWidth(); }
                @Override public int getIconHeight() { return original.getIconHeight(); }
            });
        }
        return btn;
    }

    private JButton createNavButton(String iconName, String panelName) {
        JButton btn = new JButton();
        btn.setIcon(IconHelper.get(iconName, 24));
        btn.setBorder(new EmptyBorder(5, 5, 5, 5));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> mainFrame.showPanel(panelName));
        return btn;
    }

    private JButton createCreateButton() {
        JButton btn = new JButton();
        btn.setIcon(IconHelper.get("create", 24));
        btn.setBorder(new EmptyBorder(5, 5, 5, 5));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            CreatePostDialog dialog = new CreatePostDialog(mainFrame);
            dialog.setVisible(true);
        });
        return btn;
    }

    private JButton createProfileButton() {
        JButton btn = new JButton();
        btn.setBorder(new EmptyBorder(5, 5, 5, 5));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> mainFrame.showPanel("profile"));
        
        model.Usuario u = services.AuthService.getInstance().getUsuarioActual();
        String foto = (u != null) ? u.getFotoPerfil() : "";
        CircularImageLabel cil = new CircularImageLabel(foto, 24);
        
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
}
