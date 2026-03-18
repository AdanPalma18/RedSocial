package gui;

import java.util.List;
import java.awt.*;
import javax.swing.*;
import model.Publicacion;
import services.*;

public class FeedPanel extends JPanel {
    private JPanel postsContainer;

    public FeedPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250));

        postsContainer = new JPanel();
        postsContainer.setLayout(new BoxLayout(postsContainer, BoxLayout.Y_AXIS));
        postsContainer.setBackground(new Color(250, 250, 250));
        postsContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Wrapper to center the posts column
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperPanel.setBackground(new Color(250, 250, 250));
        wrapperPanel.add(postsContainer);

        JScrollPane scroll = new JScrollPane(wrapperPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    public void cargarFeed() {
        postsContainer.removeAll();

        model.Usuario usuarioActual = AuthService.getInstance().getUsuarioActual();
        if (usuarioActual == null)
            return;

        List<Publicacion> feed = PostService.getInstance().obtenerFeed(usuarioActual.getUsername());

        if (feed.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(new Color(250, 250, 250));
            emptyPanel.setPreferredSize(new Dimension(470, 300));

            JLabel emptyLabel = new JLabel(
                    "<html><center>No hay publicaciones todavía.<br>Sigue a otros usuarios o crea tu primer post.</center></html>",
                    SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(142, 142, 142));
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            postsContainer.add(emptyPanel);
        } else {
            for (Publicacion publicacion : feed) {
                PostComponent postComponent = new PostComponent(publicacion);
                postsContainer.add(postComponent);
                postsContainer.add(Box.createVerticalStrut(8));
            }
        }

        postsContainer.revalidate();
        postsContainer.repaint();
    }
}
