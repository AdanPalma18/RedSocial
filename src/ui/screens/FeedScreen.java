package ui.screens;

import javax.swing.*;
import java.awt.*;
import ui.components.*;
import ui.style.UIConstants;

public class FeedScreen extends JPanel {
    public FeedScreen() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND);

        HeaderBar header = new HeaderBar();
        
        StoryBar stories = new StoryBar();
        JScrollPane storyScroll = new JScrollPane(stories);
        storyScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        storyScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        storyScroll.setBorder(BorderFactory.createEmptyBorder());
        storyScroll.setPreferredSize(new Dimension(0, 120));

        JPanel posts = new JPanel();
        posts.setLayout(new BoxLayout(posts, BoxLayout.Y_AXIS));
        posts.setBackground(UIConstants.BACKGROUND);

        posts.add(new PostCard("usuario1", null, null, "Mi primera publicación en Instagram! 🎉 #instagram #java"));
        posts.add(new PostCard("usuario2", null, null, "Hermoso día para programar ☀️💻 #programming #coding"));
        posts.add(new PostCard("usuario3", null, null, "Proyecto de Java terminado! #java #project #success"));

        JScrollPane scroll = new JScrollPane(posts);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(storyScroll, BorderLayout.NORTH);
        centerPanel.add(scroll, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
}
