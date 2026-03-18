package gui;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.*;
import model.Usuario;
import services.AuthService;
import services.FollowService;

public class RightPanel extends JPanel {

    private static final Color IG_BLUE = new Color(0, 149, 246);
    private static final Color BORDER_GRAY = new Color(219, 219, 219);
    private static final Color TEXT_GRAY = new Color(142, 142, 142);

    private final MainFrame mainFrame;

    public RightPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setPreferredSize(new Dimension(320, 0));
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    public void actualizar() {
        removeAll();

        Usuario u = AuthService.getInstance().getUsuarioActual();
        if (u == null) {
            revalidate();
            repaint();
            return;
        }

        // ─── USER CARD ──────────────────────────────────────────────
        JPanel userCard = new JPanel(new BorderLayout(12, 0));
        userCard.setBackground(Color.WHITE);
        userCard.setMaximumSize(new Dimension(300, 60));
        userCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        CircularImageLabel avatar = new CircularImageLabel(
                u.getFotoPerfil() != null ? u.getFotoPerfil() : "", 56);
        avatar.setPreferredSize(new Dimension(56, 56));

        JPanel userText = new JPanel(new GridLayout(2, 1, 0, 2));
        userText.setBackground(Color.WHITE);
        JLabel uname = new JLabel(u.getUsername());
        uname.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel fullName = new JLabel(u.getNombre());
        fullName.setFont(new Font("Arial", Font.PLAIN, 12));
        fullName.setForeground(TEXT_GRAY);
        userText.add(uname);
        userText.add(fullName);

        userCard.add(avatar, BorderLayout.WEST);
        userCard.add(userText, BorderLayout.CENTER);

        add(userCard);
        add(Box.createVerticalStrut(20));

        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setForeground(BORDER_GRAY);
        sep.setMaximumSize(new Dimension(300, 1));
        add(sep);
        add(Box.createVerticalStrut(14));

        // ─── SUGGESTIONS HEADER ─────────────────────────────────────
        JPanel suggHeader = new JPanel(new BorderLayout());
        suggHeader.setBackground(Color.WHITE);
        suggHeader.setMaximumSize(new Dimension(300, 30));
        suggHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel suggTitle = new JLabel("Suggested for you");
        suggTitle.setFont(new Font("Arial", Font.BOLD, 13));
        suggTitle.setForeground(new Color(38, 38, 38));

        JLabel seeAll = new JLabel("See All");
        seeAll.setFont(new Font("Arial", Font.BOLD, 12));
        seeAll.setForeground(new Color(38, 38, 38));
        seeAll.setCursor(new Cursor(Cursor.HAND_CURSOR));

        suggHeader.add(suggTitle, BorderLayout.WEST);
        suggHeader.add(seeAll, BorderLayout.EAST);
        add(suggHeader);
        add(Box.createVerticalStrut(10));

        // ─── SUGGESTIONS LIST ───────────────────────────────────────
        List<String> followingList = java.util.Arrays.asList(
                FollowService.getInstance().obtenerFollowing(u.getUsername()).toArray(new String[0]));

        Map<String, Usuario> todos = AuthService.getInstance().getUsuariosEnMemoria();
        int count = 0;
        for (Usuario sugerido : todos.values()) {
            if (count >= 5)
                break;
            if (sugerido.getUsername().equals(u.getUsername()))
                continue;
            if (!sugerido.isActivo())
                continue;
            if (followingList.contains(sugerido.getUsername()))
                continue;

            JPanel row = buildSuggestionRow(sugerido, u.getUsername());
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(row);
            add(Box.createVerticalStrut(10));
            count++;
        }

        if (count == 0) {
            JLabel noSugg = new JLabel("No suggestions available.");
            noSugg.setFont(new Font("Arial", Font.PLAIN, 12));
            noSugg.setForeground(TEXT_GRAY);
            noSugg.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(noSugg);
        }

        add(Box.createVerticalGlue());
        revalidate();
        repaint();
    }

    private JPanel buildSuggestionRow(Usuario sugerido, String currentUser) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(300, 50));

        CircularImageLabel pic = new CircularImageLabel(
                sugerido.getFotoPerfil() != null ? sugerido.getFotoPerfil() : "", 36);
        pic.setPreferredSize(new Dimension(36, 36));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setBackground(Color.WHITE);

        JLabel uname = new JLabel(sugerido.getUsername());
        uname.setFont(new Font("Arial", Font.BOLD, 13));
        uname.setCursor(new Cursor(Cursor.HAND_CURSOR));
        uname.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mainFrame.showOtherProfile(sugerido.getUsername());
            }
        });

        JLabel sub = new JLabel("Suggested for you");
        sub.setFont(new Font("Arial", Font.PLAIN, 11));
        sub.setForeground(TEXT_GRAY);

        textPanel.add(uname);
        textPanel.add(sub);

        JButton actionBtn = new JButton("Follow");
        actionBtn.setFont(new Font("Arial", Font.BOLD, 12));
        actionBtn.setBorderPainted(false);
        actionBtn.setContentAreaFilled(false);
        actionBtn.setFocusPainted(false);
        actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Determinar estado de la relación
        Set<String> followers = services.FollowService.getInstance().obtenerFollowers(currentUser);
        boolean teSigue = followers.contains(sugerido.getUsername());
        boolean leEnviasteSolicitud = services.FollowService.getInstance().tieneSolicitudPendiente(currentUser, sugerido.getUsername());
        boolean teEnvioSolicitud = services.FollowService.getInstance().obtenerSolicitudes(currentUser).contains(sugerido.getUsername());

        if (teEnvioSolicitud) {
            actionBtn.setText("Accept");
            actionBtn.setForeground(IG_BLUE);
            actionBtn.addActionListener(e -> {
                services.FollowService.getInstance().aprobarSolicitud(currentUser, sugerido.getUsername());
                actualizar();
                mainFrame.refreshFeed();
            });
        } else if (leEnviasteSolicitud) {
            actionBtn.setText("Requested");
            actionBtn.setForeground(TEXT_GRAY);
            actionBtn.setEnabled(false);
        } else if (teSigue) {
            actionBtn.setText("Follow back");
            actionBtn.setForeground(IG_BLUE);
            actionBtn.addActionListener(e -> {
                services.FollowService.getInstance().seguir(currentUser, sugerido.getUsername());
                actualizar();
                mainFrame.refreshFeed();
            });
        } else {
            actionBtn.setText("Follow");
            actionBtn.setForeground(IG_BLUE);
            actionBtn.addActionListener(e -> {
                services.FollowService.getInstance().seguir(currentUser, sugerido.getUsername());
                actualizar();
                mainFrame.refreshFeed();
            });
        }

        row.add(pic, BorderLayout.WEST);
        row.add(textPanel, BorderLayout.CENTER);
        row.add(actionBtn, BorderLayout.EAST);

        return row;
    }
}
