package ui.screens;

import javax.swing.*;
import java.awt.*;
import ui.components.*;
import ui.style.UIConstants;

public class ProfileScreen extends JPanel {
    public ProfileScreen() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND);

        HeaderBar header = new HeaderBar();

        JPanel profileInfo = new JPanel();
        profileInfo.setLayout(new BoxLayout(profileInfo, BoxLayout.Y_AXIS));
        profileInfo.setBackground(UIConstants.BACKGROUND);
        profileInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        topPanel.setBackground(UIConstants.BACKGROUND);

        CircularImage profilePic = new CircularImage((ImageIcon) null, 100);
        topPanel.add(profilePic);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(UIConstants.BACKGROUND);

        JLabel username = new JLabel("username");
        username.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel countsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        countsPanel.setBackground(UIConstants.BACKGROUND);
        countsPanel.add(createStatLabel("0", "publicaciones"));
        countsPanel.add(createStatLabel("0", "seguidores"));
        countsPanel.add(createStatLabel("0", "seguidos"));

        JLabel bio = new JLabel("Biografía del usuario");
        bio.setFont(UIConstants.NORMAL_FONT);

        statsPanel.add(username);
        statsPanel.add(countsPanel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(bio);

        topPanel.add(statsPanel);
        profileInfo.add(topPanel);

        JSeparator separator = new JSeparator();
        profileInfo.add(Box.createVerticalStrut(15));
        profileInfo.add(separator);

        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 2, 2));
        gridPanel.setBackground(UIConstants.BACKGROUND);

        for (int i = 0; i < 9; i++) {
            JLabel thumbnail = new JLabel();
            thumbnail.setPreferredSize(new Dimension(150, 150));
            thumbnail.setBackground(Color.LIGHT_GRAY);
            thumbnail.setOpaque(true);
            thumbnail.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            gridPanel.add(thumbnail);
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(header, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(profileInfo, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createStatLabel(String count, String label) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(UIConstants.BACKGROUND);

        JLabel countLabel = new JLabel(count + " ");
        countLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(UIConstants.NORMAL_FONT);
        textLabel.setForeground(UIConstants.SUBTEXT);

        panel.add(countLabel);
        panel.add(textLabel);

        return panel;
    }
}
