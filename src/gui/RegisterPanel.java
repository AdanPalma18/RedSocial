package gui;

import java.awt.*;
import javax.swing.*;

public class RegisterPanel extends JPanel {
    private final MainFrame mainFrame;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 10, 6, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel logo = new JLabel("Instagram");
        logo.setFont(new Font("Serif", Font.BOLD, 28));
        logo.setForeground(Color.BLACK);
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitle = new JLabel("Regístrate para ver fotos de tus amigos");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(Color.GRAY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel usernameLabel = new JLabel("Usuario:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        usernameLabel.setForeground(Color.DARK_GRAY);

        JTextField username = new JTextField(15);
        username.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        username.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        passwordLabel.setForeground(Color.DARK_GRAY);

        JPasswordField password = new JPasswordField(15);
        password.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        password.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel nombreLabel = new JLabel("Nombre completo:");
        nombreLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        nombreLabel.setForeground(Color.DARK_GRAY);

        JTextField nombre = new JTextField(15);
        nombre.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        nombre.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel edadLabel = new JLabel("Edad:");
        edadLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        edadLabel.setForeground(Color.DARK_GRAY);

        JTextField edad = new JTextField(15);
        edad.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        edad.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel generoLabel = new JLabel("Género:");
        generoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        generoLabel.setForeground(Color.DARK_GRAY);

        JPanel generoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        generoPanel.setBackground(Color.WHITE);
        JRadioButton masculinoBtn = new JRadioButton("M", true);
        JRadioButton femeninoBtn = new JRadioButton("F");
        masculinoBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        femeninoBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ButtonGroup generoGroup = new ButtonGroup();
        generoGroup.add(masculinoBtn);
        generoGroup.add(femeninoBtn);
        generoPanel.add(masculinoBtn);
        generoPanel.add(femeninoBtn);

        JLabel tipoLabel = new JLabel("Tipo de cuenta:");
        tipoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tipoLabel.setForeground(Color.DARK_GRAY);

        JPanel tipoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tipoPanel.setBackground(Color.WHITE);
        JRadioButton publicoBtn = new JRadioButton("Pública", true);
        JRadioButton privadoBtn = new JRadioButton("Privada");
        publicoBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        privadoBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ButtonGroup tipoGroup = new ButtonGroup();
        tipoGroup.add(publicoBtn);
        tipoGroup.add(privadoBtn);
        tipoPanel.add(publicoBtn);
        tipoPanel.add(privadoBtn);

        JLabel fotoLabel = new JLabel("Foto de perfil:");
        fotoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fotoLabel.setForeground(Color.DARK_GRAY);

        JButton btnSeleccionarFoto = new JButton("Seleccionar foto");
        btnSeleccionarFoto.setBackground(Color.LIGHT_GRAY);
        btnSeleccionarFoto.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnSeleccionarFoto.setFocusPainted(false);
        btnSeleccionarFoto.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblFotoSeleccionada = new JLabel("Ninguna foto seleccionada");
        lblFotoSeleccionada.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblFotoSeleccionada.setForeground(Color.GRAY);

        final String[] rutaFotoSeleccionada = {null};

        btnSeleccionarFoto.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images (JPG, PNG)", "jpg", "jpeg", "png"));
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                rutaFotoSeleccionada[0] = fileChooser.getSelectedFile().getAbsolutePath();
                lblFotoSeleccionada.setText(fileChooser.getSelectedFile().getName());
                lblFotoSeleccionada.setForeground(new Color(0, 149, 246));
            }
        });

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton register = new JButton("Registrarse");
        register.setBackground(new Color(0, 149, 246));
        register.setForeground(Color.WHITE);
        register.setFont(new Font("SansSerif", Font.BOLD, 13));
        register.setFocusPainted(false);
        register.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        register.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        register.addActionListener(e -> {
            String user = username.getText().trim();
            String pass = new String(password.getPassword());
            String nom = nombre.getText().trim();
            String edadText = edad.getText().trim();
            String gen = masculinoBtn.isSelected() ? "M" : "F";
            
            if (user.isEmpty() || pass.isEmpty() || nom.isEmpty() || edadText.isEmpty()) {
                errorLabel.setText("Todos los campos son obligatorios");
                return;
            }
            
            try {
                int ed = Integer.parseInt(edadText);
                boolean esPublico = publicoBtn.isSelected();
                
                if (services.AuthService.getInstance().registrar(user, pass, nom, ed, gen, esPublico, rutaFotoSeleccionada[0])) {
                    if (services.AuthService.getInstance().login(user, pass)) {
                        mainFrame.showPanel("feed");
                    }
                } else {
                    errorLabel.setText("El usuario ya existe o los datos son inválidos");
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("La edad debe ser un número válido");
            }
        });

        JLabel backToLogin = new JLabel("¿Ya tienes cuenta? Inicia sesión");
        backToLogin.setForeground(new Color(0, 149, 246));
        backToLogin.setFont(new Font("SansSerif", Font.PLAIN, 13));
        backToLogin.setHorizontalAlignment(SwingConstants.CENTER);
        backToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        backToLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainFrame.showPanel("login");
            }
        });

        c.gridy = 0;
        add(logo, c);

        c.gridy = 1;
        add(subtitle, c);

        c.gridy = 2;
        c.anchor = GridBagConstraints.WEST;
        add(usernameLabel, c);

        c.gridy = 3;
        c.anchor = GridBagConstraints.CENTER;
        add(username, c);

        c.gridy = 4;
        c.anchor = GridBagConstraints.WEST;
        add(passwordLabel, c);

        c.gridy = 5;
        c.anchor = GridBagConstraints.CENTER;
        add(password, c);

        c.gridy = 6;
        c.anchor = GridBagConstraints.WEST;
        add(nombreLabel, c);

        c.gridy = 7;
        c.anchor = GridBagConstraints.CENTER;
        add(nombre, c);

        c.gridy = 8;
        c.anchor = GridBagConstraints.WEST;
        add(edadLabel, c);

        c.gridy = 9;
        c.anchor = GridBagConstraints.CENTER;
        add(edad, c);

        c.gridy = 10;
        c.anchor = GridBagConstraints.WEST;
        add(generoLabel, c);

        c.gridy = 11;
        c.anchor = GridBagConstraints.CENTER;
        add(generoPanel, c);

        c.gridy = 12;
        c.anchor = GridBagConstraints.WEST;
        add(tipoLabel, c);

        c.gridy = 13;
        c.anchor = GridBagConstraints.CENTER;
        add(tipoPanel, c);

        c.gridy = 14;
        c.anchor = GridBagConstraints.WEST;
        add(fotoLabel, c);

        c.gridy = 15;
        c.anchor = GridBagConstraints.CENTER;
        add(btnSeleccionarFoto, c);

        c.gridy = 16;
        add(lblFotoSeleccionada, c);

        c.gridy = 17;
        add(errorLabel, c);

        c.gridy = 18;
        add(register, c);

        c.gridy = 19;
        add(backToLogin, c);
    }
}
