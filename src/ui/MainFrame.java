package ui;

import javax.swing.*;
import java.awt.*;
import ui.screens.*;
import ui.components.BottomNavBar;
import ui.style.UIConstants;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private BottomNavBar bottomNavBar;
    private JPanel contentPanel;

    public MainFrame() {
        setTitle("Mini Instagram");
        setSize(400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(UIConstants.BACKGROUND);

        mainContainer.add(new LoginScreen(this), "login");
        mainContainer.add(new FeedScreen(), "feed");
        mainContainer.add(new ProfileScreen(), "profile");
        mainContainer.add(new SearchScreen(), "search");
        mainContainer.add(new InboxScreen(), "inbox");

        bottomNavBar = new BottomNavBar(this);
        
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainContainer, BorderLayout.CENTER);

        add(contentPanel);

        showScreen("login");
    }

    public void showScreen(String screenName) {
        cardLayout.show(mainContainer, screenName);
        
        if (screenName.equals("login")) {
            contentPanel.remove(bottomNavBar);
        } else {
            if (contentPanel.getComponentCount() == 1) {
                contentPanel.add(bottomNavBar, BorderLayout.SOUTH);
            }
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
