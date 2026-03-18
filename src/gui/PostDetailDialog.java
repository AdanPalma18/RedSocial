package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import model.Publicacion;
import services.AuthService;
import model.Usuario;

public class PostDetailDialog extends JDialog {

    private static final Color BORDER_GRAY = new Color(219, 219, 219);
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color TEXT_GRAY = new Color(142, 142, 142);

    public PostDetailDialog(JFrame parent, Publicacion post) {
        super(parent, true);
        setUndecorated(true);
        setSize(820, 530);
        setLocationRelativeTo(parent);
        setBackground(BG_WHITE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_WHITE);
        root.setBorder(new LineBorder(BORDER_GRAY, 1));

        // ─── HEADER ───────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_WHITE);
        headerPanel.setPreferredSize(new Dimension(820, 44));
        headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_GRAY));

        JLabel headerTitle = new JLabel("Post", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnClose = new JButton("X");
        btnClose.setFont(new Font("Arial", Font.BOLD, 20));
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        headerPanel.add(headerTitle, BorderLayout.CENTER);
        headerPanel.add(btnClose, BorderLayout.EAST);

        // ─── CONTENT ──────────────────────────────────────────────
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG_WHITE);

        // LEFT: IMAGE
        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(490, 486));
        imageLabel.setBackground(Color.BLACK);
        imageLabel.setOpaque(true);

        if (post.getImagenRuta() != null && !post.getImagenRuta().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(post.getImagenRuta());
                int pw = 490, ph = 486;
                int iw = icon.getIconWidth(), ih = icon.getIconHeight();
                double scale = Math.min((double) pw / iw, (double) ph / ih);
                int nw = (int) (iw * scale), nh = (int) (ih * scale);
                Image scaled = icon.getImage().getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                imageLabel.setText("Image not found");
                imageLabel.setForeground(Color.WHITE);
            }
        } else {
            imageLabel.setText("No image");
            imageLabel.setForeground(Color.WHITE);
        }

        // RIGHT: INFO
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BG_WHITE);
        infoPanel.setPreferredSize(new Dimension(330, 486));
        infoPanel.setBorder(new MatteBorder(0, 1, 0, 0, BORDER_GRAY));

        // User row
        JPanel userRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        userRow.setBackground(BG_WHITE);
        userRow.setMaximumSize(new Dimension(330, 60));

        Usuario autor = AuthService.getInstance().getUsuario(post.getAutor());
        CircularImageLabel pic = new CircularImageLabel((autor != null) ? autor.getFotoPerfil() : "", 32);
        JLabel unameLabel = new JLabel(post.getAutor());
        unameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userRow.add(pic);
        userRow.add(unameLabel);

        // Caption
        JTextArea captionArea = new JTextArea(post.getContenido());
        captionArea.setEditable(false);
        captionArea.setLineWrap(true);
        captionArea.setWrapStyleWord(true);
        captionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        captionArea.setBackground(BG_WHITE);
        captionArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JScrollPane scrollCaption = new JScrollPane(captionArea);
        scrollCaption.setBorder(BorderFactory.createEmptyBorder());
        scrollCaption.setBackground(BG_WHITE);

        // Date
        JLabel dateLabel = new JLabel(post.getFecha());
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(TEXT_GRAY);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 20, 15));

        infoPanel.add(userRow);
        infoPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        infoPanel.add(scrollCaption);
        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(dateLabel);

        contentPanel.add(imageLabel, BorderLayout.CENTER);
        contentPanel.add(infoPanel, BorderLayout.EAST);

        root.add(headerPanel, BorderLayout.NORTH);
        root.add(contentPanel, BorderLayout.CENTER);

        setContentPane(root);
    }
}
