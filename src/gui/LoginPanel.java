package gui;

import java.awt.*;
import javax.swing.*;

public class LoginPanel extends JPanel {
    private final MainFrame mainFrame;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel logo = new JLabel("Instagram");
        logo.setFont(new Font("Serif", Font.BOLD, 30));
        logo.setForeground(Color.BLACK);
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel usernameLabel = new JLabel("Usuario:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameLabel.setForeground(Color.DARK_GRAY);

        JTextField username = new JTextField(15);
        username.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        username.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordLabel.setForeground(Color.DARK_GRAY);

        JPasswordField password = new JPasswordField(15);
        password.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        password.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton login = new JButton("Log In");
        login.setBackground(new Color(0, 149, 246));
        login.setForeground(Color.WHITE);
        login.setFont(new Font("SansSerif", Font.BOLD, 14));
        login.setFocusPainted(false);
        login.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        login.addActionListener(e -> {
            String user = username.getText().trim();
            String pass = new String(password.getPassword());
            
            if (user.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Por favor ingrese usuario y contraseña");
                return;
            }
            
            services.AuthService auth = services.AuthService.getInstance();
            if (auth.login(user, pass)) {
                errorLabel.setText(" ");
                services.InboxService.getInstance().conectarCliente(user);
                mainFrame.actualizarTodo();
                mainFrame.showPanel("feed");
            } else {
                // Si las credenciales eran correctas pero falló el login, es por el bloqueo de sesión
                model.Usuario u = auth.getUsuario(user);
                if (u != null && u.getPassword().equals(pass)) {
                    errorLabel.setText("Sesión ya abierta en otra ventana");
                } else {
                    errorLabel.setText("Usuario o contraseña incorrectos");
                }
            }
        });

        JLabel signup = new JLabel("Sign up");
        signup.setForeground(new Color(0, 149, 246));
        signup.setFont(new Font("SansSerif", Font.PLAIN, 14));
        signup.setHorizontalAlignment(SwingConstants.CENTER);
        signup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        signup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainFrame.showPanel("register");
            }
        });

        c.gridy = 0;
        add(logo, c);

        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        add(usernameLabel, c);

        c.gridy = 2;
        c.anchor = GridBagConstraints.CENTER;
        add(username, c);

        c.gridy = 3;
        c.anchor = GridBagConstraints.WEST;
        add(passwordLabel, c);

        c.gridy = 4;
        c.anchor = GridBagConstraints.CENTER;
        add(password, c);

        c.gridy = 5;
        add(errorLabel, c);

        c.gridy = 6;
        add(login, c);

        c.gridy = 7;
        add(signup, c);
    }
}
