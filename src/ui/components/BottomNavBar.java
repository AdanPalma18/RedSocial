package ui.components;

import javax.swing.*;
import java.awt.*;
import ui.style.UIConstants;
import ui.MainFrame;

public class BottomNavBar extends JPanel {
    private MainFrame mainFrame;

    public BottomNavBar(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridLayout(1, 4));
        setBackground(UIConstants.BACKGROUND);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        add(createNavButton("🏠", "feed"));
        add(createNavButton("🔍", "search"));
        add(createNavButton("✉️", "inbox"));
        add(createNavButton("👤", "profile"));
    }

    private JButton createNavButton(String icon, String action) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 20));
        btn.setBackground(UIConstants.BACKGROUND);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> {
            mainFrame.showScreen(action);
        });

        return btn;
    }
}
