package gui;

import java.awt.*;
import java.util.Set;
import javax.swing.*;
import model.Usuario;
import services.*;

public class RequestsPanel extends JPanel {
    private JPanel requestsListPanel;
    private MainFrame mainFrame;

    public RequestsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel title = new JLabel("Solicitudes de seguimiento");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);

        JButton btnVolver = new JButton("← Volver");
        btnVolver.setBackground(Color.WHITE);
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 12));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> mainFrame.showPanel("profile"));
        headerPanel.add(btnVolver, BorderLayout.EAST);

        requestsListPanel = new JPanel();
        requestsListPanel.setLayout(new BoxLayout(requestsListPanel, BoxLayout.Y_AXIS));
        requestsListPanel.setBackground(Color.WHITE);
        requestsListPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(requestsListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void cargarSolicitudes() {
        requestsListPanel.removeAll();

        Usuario usuarioActual = AuthService.getInstance().getUsuarioActual();
        if (usuarioActual == null) {
            return;
        }

        Set<String> solicitudes = FollowService.getInstance().obtenerSolicitudes(usuarioActual.getUsername());

        if (solicitudes.isEmpty()) {
            JLabel emptyLabel = new JLabel("No tienes solicitudes pendientes");
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            requestsListPanel.add(emptyLabel);
        } else {
            for (String solicitante : solicitudes) {
                requestsListPanel.add(crearRequestItem(solicitante));
            }
        }

        requestsListPanel.revalidate();
        requestsListPanel.repaint();
    }

    private JPanel crearRequestItem(String username) {
        JPanel item = new JPanel(new BorderLayout(10, 5));
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 0, 15, 0)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        Usuario usuario = AuthService.getInstance().getUsuario(username);
        String foto = (usuario != null) ? usuario.getFotoPerfil() : "";
        CircularImageLabel profilePic = new CircularImageLabel(foto, 50);
        item.add(profilePic, BorderLayout.WEST);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel nameLabel = new JLabel(usuario.getNombre());
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        nameLabel.setForeground(Color.GRAY);

        textPanel.add(usernameLabel);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(nameLabel);

        item.add(textPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);

        JButton aprobarBtn = new JButton("Aprobar");
        aprobarBtn.setBackground(new Color(0, 149, 246));
        aprobarBtn.setForeground(Color.WHITE);
        aprobarBtn.setFont(new Font("Arial", Font.BOLD, 12));
        aprobarBtn.setFocusPainted(false);
        aprobarBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        aprobarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        aprobarBtn.addActionListener(e -> {
            Usuario usuarioActual = AuthService.getInstance().getUsuarioActual();
            FollowService.getInstance().aprobarSolicitud(usuarioActual.getUsername(), username);
            cargarSolicitudes();
        });

        JButton rechazarBtn = new JButton("Rechazar");
        rechazarBtn.setBackground(Color.LIGHT_GRAY);
        rechazarBtn.setForeground(Color.BLACK);
        rechazarBtn.setFont(new Font("Arial", Font.BOLD, 12));
        rechazarBtn.setFocusPainted(false);
        rechazarBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        rechazarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rechazarBtn.addActionListener(e -> {
            Usuario usuarioActual = AuthService.getInstance().getUsuarioActual();
            FollowService.getInstance().rechazarSolicitud(usuarioActual.getUsername(), username);
            cargarSolicitudes();
        });

        buttonsPanel.add(aprobarBtn);
        buttonsPanel.add(rechazarBtn);

        item.add(buttonsPanel, BorderLayout.EAST);

        return item;
    }
}
