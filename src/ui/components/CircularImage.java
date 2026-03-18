package ui.components;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

public class CircularImage extends JLabel {
    public CircularImage(ImageIcon icon, int size) {
        if (icon != null && icon.getImage() != null) {
            Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(img));
        }
        setPreferredSize(new Dimension(size, size));
    }

    public CircularImage(String imagePath, int size) {
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // Si no hay imagen, mostrar círculo gris
        }
        setPreferredSize(new Dimension(size, size));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setClip(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
        
        if (getIcon() != null) {
            super.paintComponent(g2);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillOval(0, 0, getWidth(), getHeight());
        }
        
        g2.dispose();
    }
}
