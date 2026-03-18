package ui.components;

import javax.swing.*;
import java.awt.*;
import ui.style.UIConstants;

public class PostCard extends JPanel {
    public PostCard(String username, ImageIcon profile, ImageIcon postImage, String caption) {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));
        setMaximumSize(new Dimension(600, Integer.MAX_VALUE));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        header.setBackground(UIConstants.BACKGROUND);

        CircularImage profilePic = new CircularImage(profile, 40);
        JLabel name = new JLabel(username);
        name.setFont(UIConstants.TITLE_FONT);
        name.setForeground(UIConstants.TEXT);

        header.add(profilePic);
        header.add(name);

        JLabel image = new JLabel();
        image.setHorizontalAlignment(SwingConstants.CENTER);
        if (postImage != null && postImage.getImage() != null) {
            Image img = postImage.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
            image.setIcon(new ImageIcon(img));
        } else {
            image.setText("Imagen no disponible");
            image.setPreferredSize(new Dimension(500, 300));
            image.setBackground(Color.LIGHT_GRAY);
            image.setOpaque(true);
        }

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        actionsPanel.setBackground(UIConstants.BACKGROUND);
        actionsPanel.add(new JLabel("❤️"));
        actionsPanel.add(new JLabel("💬"));
        actionsPanel.add(new JLabel("📤"));

        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(UIConstants.BACKGROUND);
        footer.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel likes = new JLabel("10,547 likes");
        likes.setFont(new Font("SansSerif", Font.BOLD, 14));
        likes.setForeground(UIConstants.TEXT);

        JLabel captionLabel = new JLabel("<html><b>" + username + "</b> " + caption + "</html>");
        captionLabel.setFont(UIConstants.NORMAL_FONT);
        captionLabel.setForeground(UIConstants.TEXT);

        JLabel viewComments = new JLabel("Ver los 234 comentarios");
        viewComments.setFont(new Font("SansSerif", Font.PLAIN, 12));
        viewComments.setForeground(UIConstants.SUBTEXT);

        JLabel time = new JLabel("Hace 2 horas");
        time.setFont(new Font("SansSerif", Font.PLAIN, 11));
        time.setForeground(UIConstants.SUBTEXT);

        footer.add(likes);
        footer.add(Box.createVerticalStrut(5));
        footer.add(captionLabel);
        footer.add(Box.createVerticalStrut(3));
        footer.add(viewComments);
        footer.add(Box.createVerticalStrut(3));
        footer.add(time);

        add(header, BorderLayout.NORTH);
        add(image, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UIConstants.BACKGROUND);
        bottomPanel.add(actionsPanel, BorderLayout.NORTH);
        bottomPanel.add(footer, BorderLayout.CENTER);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
