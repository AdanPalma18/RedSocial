package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Comentario;
import model.Publicacion;
import services.AuthService;
import utils.IconHelper;

public class CommentsDialog extends JDialog {

    private final Publicacion post;
    private final JPanel commentsContainer;
    private final JTextField commentInput;

    public CommentsDialog(Window owner, Publicacion post) {
        super(owner, "Comentarios", ModalityType.APPLICATION_MODAL);
        this.post = post;

        setSize(500, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ── HEADER ──────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(219, 219, 219)));
        
        JLabel title = new JLabel("Comentarios", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(new EmptyBorder(12, 0, 12, 0));
        header.add(title, BorderLayout.CENTER);

        JButton closeBtn = new JButton("X");
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        header.add(closeBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ── COMMENTS LIST ───────────────────────────────────────
        commentsContainer = new JPanel();
        commentsContainer.setLayout(new BoxLayout(commentsContainer, BoxLayout.Y_AXIS));
        commentsContainer.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(commentsContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // ── INPUT AREA ──────────────────────────────────────────
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(219, 219, 219)),
                new EmptyBorder(12, 12, 12, 12)));

        commentInput = new JTextField();
        commentInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        commentInput.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Simular un placeholder si está vacío
        commentInput.setText("Escribe un comentario...");
        commentInput.setForeground(Color.LIGHT_GRAY);
        commentInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (commentInput.getText().equals("Escribe un comentario...")) {
                    commentInput.setText("");
                    commentInput.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (commentInput.getText().isEmpty()) {
                    commentInput.setText("Escribe un comentario...");
                    commentInput.setForeground(Color.LIGHT_GRAY);
                }
            }
        });

        JButton postBtn = new JButton("Publicar");
        postBtn.setFont(new Font("Arial", Font.BOLD, 14));
        postBtn.setForeground(new Color(0, 149, 246));
        postBtn.setBorderPainted(false);
        postBtn.setContentAreaFilled(false);
        postBtn.setFocusPainted(false);
        postBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        postBtn.addActionListener(e -> publicarComentario());
        commentInput.addActionListener(e -> publicarComentario());

        inputPanel.add(commentInput, BorderLayout.CENTER);
        inputPanel.add(postBtn, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        cargarComentarios();
    }

    private void cargarComentarios() {
        commentsContainer.removeAll();
        List<Comentario> comentarios = post.getComentarios();
        
        for (Comentario c : comentarios) {
            commentsContainer.add(createCommentRow(c));
            commentsContainer.add(Box.createVerticalStrut(16));
        }
        
        commentsContainer.revalidate();
        commentsContainer.repaint();
    }

    private JPanel createCommentRow(Comentario c) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(12, 16, 12, 16));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90)); // Más compacto

        model.Usuario autor = AuthService.getInstance().getUsuario(c.getAutor());
        String foto = (autor != null) ? autor.getFotoPerfil() : "";
        
        // Avatar con borde de story para estilo premium
        CircularImageLabel avatar = new CircularImageLabel(foto, 42);
        avatar.setHasStory(true); 
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Línea 1: Username + Tiempo
        JPanel topLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topLine.setBackground(Color.WHITE);
        topLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        topLine.setBorder(new EmptyBorder(0, -5, 0, 0));

        JLabel username = new JLabel(c.getAutor());
        username.setFont(new Font("Arial", Font.BOLD, 13));
        
        JLabel time = new JLabel("5min"); // Podríamos calcularlo, pero por ahora estático para el look
        time.setFont(new Font("Arial", Font.PLAIN, 12));
        time.setForeground(new Color(142, 142, 142));

        topLine.add(username);
        topLine.add(time);

        // Línea 2: Texto del comentario
        String textoProcesado = c.getTexto().replaceAll("@(\\w+)", "<span style='color: #00376b;'>@$1</span>");
        JLabel commentText = new JLabel("<html><div style='width: 320px; font-family: Arial; font-size: 14px;'>" 
                                    + textoProcesado + "</div></html>");
        commentText.setAlignmentX(Component.LEFT_ALIGNMENT);
        commentText.setBorder(new EmptyBorder(2, 0, 2, 0));

        // Línea 3: Responder + Likes
        JPanel bottomLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        bottomLine.setBackground(Color.WHITE);
        bottomLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomLine.setBorder(new EmptyBorder(2, -5, 0, 0));

        JLabel reply = new JLabel("Responder");
        reply.setFont(new Font("Arial", Font.BOLD, 12));
        reply.setForeground(new Color(142, 142, 142));
        reply.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottomLine.add(reply);

        if (c.getLikes() > 0) {
            JLabel likesLbl = new JLabel(c.getLikes() + (c.getLikes() == 1 ? " like" : " likes"));
            likesLbl.setFont(new Font("Arial", Font.BOLD, 11));
            likesLbl.setForeground(new Color(142, 142, 142));
            bottomLine.add(likesLbl);
        }

        contentPanel.add(topLine);
        contentPanel.add(commentText);
        contentPanel.add(bottomLine);

        // Icono corazón
        ImageIcon icon = IconHelper.get(c.isLiked() ? "filled-heart" : "heart", 14);
        JLabel heart = new JLabel(icon);
        heart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        heart.setBorder(new EmptyBorder(0, 10, 0, 10));
        heart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                c.setLiked(!c.isLiked());
                c.setLikes(c.getLikes() + (c.isLiked() ? 1 : -1));
                cargarComentarios();
            }
        });

        row.add(avatar, BorderLayout.WEST);
        row.add(contentPanel, BorderLayout.CENTER);
        row.add(heart, BorderLayout.EAST);

        return row;
    }

    private void publicarComentario() {
        String texto = commentInput.getText().trim();
        if (texto.isEmpty() || texto.equals("Escribe un comentario...")) return;

        String currentUser = AuthService.getInstance().getUsuarioActual().getUsername();
        Comentario nuevo = new Comentario(currentUser, texto, "ahora");
        post.agregarComentario(nuevo);
        
        // Persistir en disco
        services.PostService.getInstance().guardarComentario(post.getAutor(), post.getFecha(), nuevo);
        
        commentInput.setText("");
        commentInput.requestFocus();
        cargarComentarios();
    }
}
