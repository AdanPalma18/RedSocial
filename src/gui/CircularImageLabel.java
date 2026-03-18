package gui;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

public class CircularImageLabel extends JLabel {
    private Image image;
    private boolean hasStory = false;
    private int size;

    public CircularImageLabel(String imagePath, int size) {
        this.size = size;
        Dimension d = new Dimension(size, size);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
        setSize(d);
        setOpaque(false);
        
        setImagePath(imagePath);
    }

    public void setImagePath(String imagePath) {
        this.image = null; // Reset image
        cargarImagen(imagePath);
        repaint();
    }

    public void setHasStory(boolean hasStory) {
        this.hasStory = hasStory;
        repaint();
    }

    private synchronized void cargarImagen(String imagePath) {
        this.image = null;
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            try {
                java.io.File imgFile = new java.io.File(imagePath);
                if (imgFile.exists()) {
                    // ImageIO.read es síncrono y más fiable para detectar si el archivo es una imagen válida
                    Image raw = javax.imageio.ImageIO.read(imgFile);
                    if (raw != null) {
                        image = raw.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error cargando foto de perfil desde " + imagePath + ": " + e.getMessage());
            }
        }
        
        if (image == null) {
            ImageIcon defaultIcon = utils.IconHelper.get("user", size);
            if (defaultIcon != null) {
                image = defaultIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        int squareSize = Math.min(w, h);
        int xOffset = (w - squareSize) / 2;
        int yOffset = (h - squareSize) / 2;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int storyPadding = hasStory ? 5 : 0;
        if (hasStory) {
            GradientPaint gradient = new GradientPaint(
                xOffset, yOffset + squareSize, new Color(255, 214, 0),
                xOffset + squareSize, yOffset, new Color(131, 58, 180)
            );
            g2.setPaint(gradient);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawOval(xOffset + 1, yOffset + 1, squareSize - 3, squareSize - 3);
            
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(xOffset + 3, yOffset + 3, squareSize - 7, squareSize - 7);
        }

        int imgSize = squareSize - (storyPadding * 2);
        int finalX = xOffset + storyPadding;
        int finalY = yOffset + storyPadding;
        
        // Fondo circular base (Gris muy claro)
        g2.setColor(new Color(230, 230, 230));
        g2.fillOval(finalX, finalY, imgSize, imgSize);
        
        if (image != null) {
            Shape oldClip = g2.getClip();
            g2.setClip(new Ellipse2D.Float(finalX, finalY, imgSize, imgSize));
            g2.drawImage(image, finalX, finalY, imgSize, imgSize, this);
            g2.setClip(oldClip);
        } else {
            // Silhouette fallback fuerte (Gris oscuro)
            g2.setColor(new Color(140, 140, 140));
            int headSize = imgSize / 3;
            // Cabeza
            g2.fillOval(finalX + (imgSize - headSize) / 2, finalY + imgSize / 5, headSize, headSize);
            // Cuerpo (arco)
            g2.fillArc(finalX + imgSize / 6, finalY + imgSize / 2, (imgSize * 2) / 3, (imgSize * 2) / 3, 0, 180);
        }
        
        // Borde fino para dar relieve
        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(1f));
        g2.drawOval(finalX, finalY, imgSize - 1, imgSize - 1);
        
        g2.dispose();
    }
}
