package gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import model.Publicacion;
import services.AuthService;
import utils.IconHelper;

public class PostComponent extends JPanel {

    private static final Color IG_BLUE = new Color(0, 149, 246);
    private static final Color BORDER_GRAY = new Color(219, 219, 219);
    private static final Color TEXT_GRAY = new Color(142, 142, 142);
    private static final int ICON_SIZE = 24;

    private Publicacion post;
    private JLabel heartLabel;
    private JLabel bookmarkLabel;
    private JLabel likesLabel;
    private JLabel commentsCountLabel;
    private ImageIcon heartIcon;
    private ImageIcon heartFilledIcon;
    private ImageIcon bookmarkIcon;
    private ImageIcon bookmarkFilledIcon;
    private final int post_width;

    public PostComponent(Publicacion p) {
        this.post = p;
        this.post_width = (utils.Constants.MODO == utils.Constants.Modo.MOBILE) ? 370 : 470;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, BORDER_GRAY),
                new EmptyBorder(0, 0, 0, 0)));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setMaximumSize(new Dimension(post_width, Integer.MAX_VALUE));
        
        // Load icons
        heartIcon = IconHelper.get("heart", ICON_SIZE);
        heartFilledIcon = IconHelper.get("filled-heart", ICON_SIZE);
        ImageIcon commentIcon = IconHelper.get("comment", ICON_SIZE);
        ImageIcon shareIcon = IconHelper.get("paper-plane", ICON_SIZE);
        bookmarkIcon = IconHelper.get("bookmark", ICON_SIZE);
        bookmarkFilledIcon = IconHelper.get("filled-bookmark", ICON_SIZE);

        setPreferredSize(new Dimension(post_width, computeHeight(p)));

        // ── 1. HEADER ─────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(12, 12, 12, 12));
        header.setMaximumSize(new Dimension(post_width, 70));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        model.Usuario autorUser = AuthService.getInstance().getUsuario(p.getAutor());
        String fotoPerfil = (autorUser != null && autorUser.getFotoPerfil() != null)
                ? autorUser.getFotoPerfil()
                : "";
        CircularImageLabel profilePic = new CircularImageLabel(fotoPerfil, 40);

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBackground(Color.WHITE);
        JLabel usernameLabel = new JLabel(p.getAutor());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(new Color(38, 38, 38));
        usernameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Window w = SwingUtilities.getWindowAncestor(PostComponent.this);
                if (w instanceof MainFrame)
                    ((MainFrame) w).showOtherProfile(p.getAutor());
            }
        });
        JLabel locationLabel = new JLabel("");
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        locationLabel.setForeground(TEXT_GRAY);
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfo.add(usernameLabel);
        if (!locationLabel.getText().isEmpty()) {
            userInfo.add(locationLabel);
        }

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftHeader.setBackground(Color.WHITE);
        leftHeader.add(profilePic);
        leftHeader.add(Box.createHorizontalStrut(12));
        leftHeader.add(userInfo);

        JLabel menuDots = new JLabel("···");
        menuDots.setFont(new Font("Arial", Font.BOLD, 18));
        menuDots.setForeground(new Color(38, 38, 38));

        header.add(leftHeader, BorderLayout.WEST);
        header.add(menuDots, BorderLayout.EAST);
        add(header);

        // ── 2. IMAGE (only if present) ────────────────────────────
        if (p.getImagenRuta() != null && !p.getImagenRuta().isEmpty()) {
            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setBackground(Color.BLACK);
            imageLabel.setOpaque(true);
            imageLabel.setMaximumSize(new Dimension(post_width, 600));
            imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            try {
                ImageIcon icon = new ImageIcon(p.getImagenRuta());
                int iw = icon.getIconWidth(), ih = icon.getIconHeight();
                if (iw > 0 && ih > 0) {
                    int nh = (int) (ih * ((double) post_width / iw));
                    Image scaled = icon.getImage().getScaledInstance(post_width, nh, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));
                    imageLabel.setPreferredSize(new Dimension(post_width, nh));
                } else {
                    throw new Exception("Zero dimensions");
                }
            } catch (Exception ex) {
                imageLabel.setText("Imagen no disponible");
                imageLabel.setPreferredSize(new Dimension(post_width, 300));
            }
            add(imageLabel);
        }

        // ── 3. ACTION BUTTONS ─────────────────────────────────────
        JPanel actionsRow = new JPanel(new BorderLayout());
        actionsRow.setBackground(Color.WHITE);
        actionsRow.setBorder(new EmptyBorder(12, 0, 8, 0));
        actionsRow.setMaximumSize(new Dimension(post_width, 44));

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftActions.setBackground(Color.WHITE);
        
        heartLabel = createActionButton(post.isLiked() ? heartFilledIcon : heartIcon, "Me gusta");
        heartLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleLike();
            }
        });
        
        leftActions.add(heartLabel);
        
        likesLabel = new JLabel(String.valueOf(post.getLikesCount()));
        likesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        likesLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        leftActions.add(likesLabel);
        
        leftActions.add(Box.createHorizontalStrut(16));
        
        JLabel commentBtn = createActionButton(commentIcon, "Comentar");
        commentBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirComentarios();
            }
        });
        leftActions.add(Box.createHorizontalStrut(16));
        leftActions.add(commentBtn);
        leftActions.add(Box.createHorizontalStrut(16));
        leftActions.add(createActionButton(shareIcon, "Compartir"));

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightActions.setBackground(Color.WHITE);
        bookmarkLabel = createActionButton(post.isSaved() ? bookmarkFilledIcon : bookmarkIcon, "Guardar");
        bookmarkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleBookmark();
            }
        });
        rightActions.add(bookmarkLabel);

        actionsRow.add(leftActions, BorderLayout.WEST);
        actionsRow.add(rightActions, BorderLayout.EAST);
        actionsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionsRow.setBorder(new EmptyBorder(12, 12, 8, 12));
        add(actionsRow);

        // ── 4. CAPTION ────────────────────────────────────────────
        JPanel captionPanel = new JPanel();
        captionPanel.setLayout(new BoxLayout(captionPanel, BoxLayout.Y_AXIS));
        captionPanel.setBackground(Color.WHITE);
        captionPanel.setBorder(new EmptyBorder(8, 0, 16, 0));
        captionPanel.setMaximumSize(new Dimension(post_width, Integer.MAX_VALUE));
        captionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Caption text with username bold
        String rawContent = p.getContenido();
        JTextPane captionPane = new JTextPane();
        captionPane.setEditable(false);
        captionPane.setOpaque(false);
        captionPane.setFocusable(false);
        captionPane.setBorder(null);
        captionPane.setMargin(new Insets(0, 0, 0, 0));
        captionPane.setMaximumSize(new Dimension(post_width - 24, Integer.MAX_VALUE));
        captionPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        javax.swing.text.StyledDocument doc = captionPane.getStyledDocument();
        javax.swing.text.Style base = captionPane.addStyle("base", null);
        javax.swing.text.StyleConstants.setFontFamily(base, "Arial");
        javax.swing.text.StyleConstants.setFontSize(base, 14);

        javax.swing.text.Style boldStyle = captionPane.addStyle("bold", base);
        javax.swing.text.StyleConstants.setBold(boldStyle, true);

        javax.swing.text.Style blueStyle = captionPane.addStyle("blue", base);
        javax.swing.text.StyleConstants.setForeground(blueStyle, IG_BLUE);

        try {
            doc.insertString(doc.getLength(), p.getAutor() + " ", boldStyle);
            // Parse and colorize hashtags/mentions
            String[] tokens = rawContent.split(" ");
            for (String token : tokens) {
                if (token.startsWith("#") || token.startsWith("@")) {
                    doc.insertString(doc.getLength(), token + " ", blueStyle);
                } else {
                    doc.insertString(doc.getLength(), token + " ", base);
                }
            }
        } catch (Exception ex) {
            captionPane.setText(p.getAutor() + " " + rawContent);
        }

        captionPanel.add(captionPane);
        captionPanel.setBorder(new EmptyBorder(8, 12, 16, 12));

        // ── 5. TIMESTAMP ──────────────────────────────────────────
        JLabel timestamp = new JLabel(formatTimestamp(p.getFecha()));
        timestamp.setFont(new Font("Arial", Font.PLAIN, 10));
        timestamp.setForeground(TEXT_GRAY);
        timestamp.setAlignmentX(Component.LEFT_ALIGNMENT);
        captionPanel.add(Box.createVerticalStrut(4));
        captionPanel.add(timestamp);

        // Enlace de comentarios
        commentsCountLabel = new JLabel("Ver los " + post.getComentarios().size() + " comentarios");
        commentsCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        commentsCountLabel.setForeground(TEXT_GRAY);
        commentsCountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        commentsCountLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        commentsCountLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirComentarios();
            }
        });
        captionPanel.add(commentsCountLabel);

        add(captionPanel);
    }

    private void toggleLike() {
        boolean newState = !post.isLiked();
        post.setLiked(newState);
        post.setLikesCount(post.getLikesCount() + (newState ? 1 : -1));
        heartLabel.setIcon(newState ? heartFilledIcon : heartIcon);
        likesLabel.setText(String.valueOf(post.getLikesCount()));
    }

    private void abrirComentarios() {
        Window w = SwingUtilities.getWindowAncestor(this);
        CommentsDialog dialog = new CommentsDialog(w, post);
        dialog.setVisible(true);
        // Al cerrar, actualizar el conteo
        commentsCountLabel.setText("Ver los " + post.getComentarios().size() + " comentarios");
    }

    private void toggleBookmark() {
        boolean newState = !post.isSaved();
        post.setSaved(newState);
        bookmarkLabel.setIcon(newState ? bookmarkFilledIcon : bookmarkIcon);
    }

    private JLabel createActionButton(ImageIcon icon, String tooltip) {
        JLabel btn = new JLabel(icon);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(tooltip);
        return btn;
    }

    private String formatTimestamp(String fecha) {
        // fecha is "yyyy-MM-dd HH:mm:ss"
        if (fecha == null || fecha.length() < 10)
            return fecha;
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date postDate = sdf.parse(fecha);
            long diffMs = System.currentTimeMillis() - postDate.getTime();
            long diffMin = diffMs / 60000;
            if (diffMin < 1)
                return "just now";
            if (diffMin < 60)
                return diffMin + " minutes ago";
            long diffH = diffMin / 60;
            if (diffH < 24)
                return diffH + " hours ago";
            long diffD = diffH / 24;
            if (diffD < 7)
                return diffD + " days ago";
            return new java.text.SimpleDateFormat("MMMM d, yyyy", java.util.Locale.ENGLISH).format(postDate);
        } catch (Exception e) {
            return fecha;
        }
    }

    private int computeHeight(Publicacion p) {
        boolean hasImage = p.getImagenRuta() != null && !p.getImagenRuta().isEmpty();
        // Generous estimate to allow scrolling/layout to work
        return 60 + (hasImage ? 450 : 0) + 50 + 100;
    }
}
