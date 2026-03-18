package utils;

import java.awt.*;
import java.net.URL;
import javax.swing.*;

/**
 * Utility for loading and scaling icons from src/resources/icons/.
 */
public class IconHelper {

    private static final String BASE = "/resources/icons/";

    /**
     * Load an icon from the resources folder and scale it to the given size.
     * @param name  filename without extension (e.g. "home")
     * @param size  target width and height in pixels
     * @return scaled ImageIcon, or null if the resource is not found
     */
    public static ImageIcon get(String name, int size) {
        try {
            URL url = IconHelper.class.getResource(BASE + name + ".png");
            if (url == null) return null;
            Image img = new ImageIcon(url).getImage()
                .getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a JLabel that shows only an icon (no text), sized to 'size' x 'size'.
     */
    public static JLabel iconLabel(String name, int size) {
        JLabel lbl = new JLabel();
        ImageIcon icon = get(name, size);
        if (icon != null) {
            lbl.setIcon(icon);
        } else {
            lbl.setText("[" + name + "]");
        }
        lbl.setPreferredSize(new Dimension(size, size));
        return lbl;
    }
}
