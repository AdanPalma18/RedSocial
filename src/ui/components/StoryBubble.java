package ui.components;

import javax.swing.*;
import java.awt.*;
import ui.style.UIConstants;

public class StoryBubble extends JPanel {
    public StoryBubble(String username, ImageIcon icon) {
        setLayout(new BorderLayout(5, 5));
        setOpaque(false);
        setPreferredSize(new Dimension(80, 100));

        CircularImage image = new CircularImage(icon, 60);
        
        JPanel imageWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        imageWrapper.setOpaque(false);
        imageWrapper.add(image);

        JLabel name = new JLabel(username);
        name.setHorizontalAlignment(SwingConstants.CENTER);
        name.setFont(new Font("SansSerif", Font.PLAIN, 11));
        name.setForeground(UIConstants.TEXT);

        add(imageWrapper, BorderLayout.CENTER);
        add(name, BorderLayout.SOUTH);
    }
}
