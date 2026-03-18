package ui.components;

import javax.swing.*;
import java.awt.*;
import ui.style.UIConstants;

public class HeaderBar extends JPanel {
    public HeaderBar() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel logo = new JLabel("Instagram");
        logo.setFont(UIConstants.LOGO_FONT);
        logo.setForeground(UIConstants.TEXT);

        JPanel iconsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        iconsPanel.setBackground(UIConstants.BACKGROUND);
        iconsPanel.add(new JLabel("❤️"));
        iconsPanel.add(new JLabel("✉️"));

        add(logo, BorderLayout.WEST);
        add(iconsPanel, BorderLayout.EAST);
    }
}
