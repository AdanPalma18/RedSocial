package ui.screens;

import javax.swing.*;
import java.awt.*;
import ui.style.UIConstants;
import ui.MainFrame;

public class LoginScreen extends JPanel {
    public LoginScreen(MainFrame mainFrame) {
        setLayout(new GridBagLayout());
        setBackground(UIConstants.BACKGROUND);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel logo = new JLabel("Instagram");
        logo.setFont(new Font("Serif", Font.BOLD, 30));
        logo.setForeground(UIConstants.TEXT);
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField username = new JTextField(15);
        username.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        username.setFont(UIConstants.NORMAL_FONT);

        JPasswordField password = new JPasswordField(15);
        password.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        password.setFont(UIConstants.NORMAL_FONT);

        JButton login = new JButton("Log In");
        login.setBackground(new Color(0, 149, 246));
        login.setForeground(Color.WHITE);
        login.setFont(new Font("SansSerif", Font.BOLD, 14));
        login.setFocusPainted(false);
        login.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        login.addActionListener(e -> {
            mainFrame.showScreen("feed");
        });

        JLabel signup = new JLabel("Sign up");
        signup.setForeground(new Color(0, 149, 246));
        signup.setFont(UIConstants.NORMAL_FONT);
        signup.setHorizontalAlignment(SwingConstants.CENTER);
        signup.setCursor(new Cursor(Cursor.HAND_CURSOR));

        c.gridy = 0;
        add(logo, c);

        c.gridy = 1;
        add(username, c);

        c.gridy = 2;
        add(password, c);

        c.gridy = 3;
        add(login, c);

        c.gridy = 4;
        add(signup, c);
    }
}
