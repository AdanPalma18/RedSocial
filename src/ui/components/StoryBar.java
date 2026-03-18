package ui.components;

import javax.swing.*;
import java.awt.*;
import ui.style.UIConstants;

public class StoryBar extends JPanel {
    public StoryBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setBackground(UIConstants.BACKGROUND);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        for (int i = 0; i < 6; i++) {
            add(new StoryBubble("user" + i, null));
        }
    }
}
