package ui.screens;

import javax.swing.*;
import java.awt.*;
import ui.components.*;
import ui.style.UIConstants;

public class InboxScreen extends JPanel {
    public InboxScreen() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND);

        HeaderBar header = new HeaderBar();

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIConstants.BACKGROUND);
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel title = new JLabel("Mensajes");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        titlePanel.add(title, BorderLayout.WEST);

        JPanel chatListPanel = new JPanel();
        chatListPanel.setLayout(new BoxLayout(chatListPanel, BoxLayout.Y_AXIS));
        chatListPanel.setBackground(UIConstants.BACKGROUND);

        chatListPanel.add(createChatItem("usuario1", "Hola! Cómo estás?", "10 min"));
        chatListPanel.add(createChatItem("usuario2", "Gracias por seguirme!", "1 h"));
        chatListPanel.add(createChatItem("usuario3", "Me gustó tu publicación", "3 h"));

        JScrollPane scrollPane = new JScrollPane(chatListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(header, BorderLayout.NORTH);
        add(titlePanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private JPanel createChatItem(String username, String lastMessage, String time) {
        JPanel item = new JPanel(new BorderLayout(10, 5));
        item.setBackground(UIConstants.BACKGROUND);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        CircularImage profilePic = new CircularImage((ImageIcon) null, 50);
        item.add(profilePic, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(UIConstants.BACKGROUND);

        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(UIConstants.TITLE_FONT);

        JLabel messageLabel = new JLabel(lastMessage);
        messageLabel.setFont(UIConstants.NORMAL_FONT);
        messageLabel.setForeground(UIConstants.SUBTEXT);

        textPanel.add(usernameLabel);
        textPanel.add(messageLabel);

        item.add(textPanel, BorderLayout.CENTER);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        timeLabel.setForeground(UIConstants.SUBTEXT);
        item.add(timeLabel, BorderLayout.EAST);

        return item;
    }
}
