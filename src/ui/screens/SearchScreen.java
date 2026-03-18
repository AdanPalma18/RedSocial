package ui.screens;

import javax.swing.*;
import java.awt.*;
import ui.components.*;
import ui.style.UIConstants;

public class SearchScreen extends JPanel {
    public SearchScreen() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND);

        HeaderBar header = new HeaderBar();

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        searchPanel.setBackground(UIConstants.BACKGROUND);
        searchPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JTextField searchField = new JTextField(25);
        searchField.setFont(UIConstants.NORMAL_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setText("🔍 Buscar usuarios, hashtags...");
        searchField.setForeground(UIConstants.SUBTEXT);

        searchPanel.add(searchField);

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(UIConstants.BACKGROUND);
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        resultsPanel.add(createUserResult("usuario1", "Usuario Uno"));
        resultsPanel.add(createUserResult("usuario2", "Usuario Dos"));
        resultsPanel.add(createUserResult("usuario3", "Usuario Tres"));

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(header, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private JPanel createUserResult(String username, String fullName) {
        JPanel result = new JPanel(new BorderLayout(10, 5));
        result.setBackground(UIConstants.BACKGROUND);
        result.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        result.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        CircularImage profilePic = new CircularImage((ImageIcon) null, 45);
        result.add(profilePic, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(UIConstants.BACKGROUND);

        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(UIConstants.TITLE_FONT);

        JLabel nameLabel = new JLabel(fullName);
        nameLabel.setFont(UIConstants.NORMAL_FONT);
        nameLabel.setForeground(UIConstants.SUBTEXT);

        textPanel.add(usernameLabel);
        textPanel.add(nameLabel);

        result.add(textPanel, BorderLayout.CENTER);

        JButton followBtn = new JButton("Seguir");
        followBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        followBtn.setBackground(new Color(0, 149, 246));
        followBtn.setForeground(Color.WHITE);
        followBtn.setFocusPainted(false);
        followBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        result.add(followBtn, BorderLayout.EAST);

        return result;
    }
}
