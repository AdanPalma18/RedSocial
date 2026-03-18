package gui;

import java.awt.*;
import javax.swing.*;

public class GridPostsPanel extends JPanel {
    private JPanel gridContainer;

    public GridPostsPanel(int columns) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        gridContainer = new JPanel(new GridLayout(0, columns, 2, 2));
        gridContainer.setBackground(Color.WHITE);
        
        add(gridContainer, BorderLayout.NORTH);
    }

    public void addPostThumbnail(model.Publicacion post) {
        addPostThumbnail(post, false);
    }

    public void addPostThumbnail(model.Publicacion post, boolean showUserHeader) {
        JPanel thumbnail = new JPanel(new BorderLayout());
        thumbnail.setPreferredSize(new Dimension(200, 200));
        thumbnail.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        thumbnail.setBackground(Color.WHITE);
        thumbnail.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (showUserHeader) {
            JPanel userHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            userHeader.setBackground(new Color(255, 255, 255, 200));
            model.Usuario autor = services.AuthService.getInstance().getUsuario(post.getAutor());
            CircularImageLabel pic = new CircularImageLabel((autor != null) ? autor.getFotoPerfil() : "", 20);
            JLabel name = new JLabel(post.getAutor());
            name.setFont(new Font("Arial", Font.BOLD, 11));
            userHeader.add(pic);
            userHeader.add(name);
            thumbnail.add(userHeader, BorderLayout.NORTH);
        }

        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        if (post.getImagenRuta() != null && !post.getImagenRuta().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(post.getImagenRuta());
                Image img = icon.getImage();
                // Simple scale to fill (not perfect but works)
                Image scaled = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                imageLabel.setText("[Err]");
            }
        } else {
            imageLabel.setText("No Image");
            imageLabel.setForeground(Color.LIGHT_GRAY);
        }

        thumbnail.add(imageLabel, BorderLayout.CENTER);
        
        thumbnail.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Window window = SwingUtilities.getWindowAncestor(GridPostsPanel.this);
                if (window instanceof JFrame) {
                    PostDetailDialog dialog = new PostDetailDialog((JFrame) window, post);
                    dialog.setVisible(true);
                }
            }
        });

        gridContainer.add(thumbnail);
        gridContainer.revalidate();
        gridContainer.repaint();
    }

    public void limpiar() {
        gridContainer.removeAll();
        gridContainer.revalidate();
        gridContainer.repaint();
    }

    public void mostrarMensajeVacio(String mensaje) {
        limpiar();
        JLabel emptyLabel = new JLabel(mensaje);
        emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emptyLabel.setForeground(Color.GRAY);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyLabel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
        add(emptyLabel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
