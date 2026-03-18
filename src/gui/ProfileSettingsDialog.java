package gui;

import java.awt.*;
import javax.swing.*;
import model.*;
import services.*;

public class ProfileSettingsDialog extends JDialog {
    private MainFrame mainFrame;
    private JTextField nombreField;
    private JTextField edadField;
    private JRadioButton masculinoBtn, femeninoBtn;
    private JRadioButton publicoBtn, privadoBtn;
    private String rutaNuevaFoto = null;
    private JLabel fotoStatus;

    public ProfileSettingsDialog(MainFrame mainFrame) {
        super(mainFrame, "Ajustes de Perfil", true);
        this.mainFrame = mainFrame;
        setSize(400, 500);
        setLocationRelativeTo(mainFrame);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        Usuario actual = AuthService.getInstance().getUsuarioActual();
        if (actual == null) return;

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Foto actual y cambio
        gbc.gridx = 0; gbc.gridy = 0;
        CircularImageLabel fotoPreview = new CircularImageLabel(actual.getFotoPerfil(), 80);
        content.add(fotoPreview, gbc);

        gbc.gridx = 1;
        JButton btnCambiarFoto = new JButton("Cambiar foto");
        btnCambiarFoto.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                rutaNuevaFoto = fc.getSelectedFile().getAbsolutePath();
                fotoStatus.setText("Nueva foto seleccionada");
                fotoPreview.setImagePath(rutaNuevaFoto);
            }
        });
        content.add(btnCambiarFoto, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        fotoStatus = new JLabel(" ");
        fotoStatus.setFont(new Font("Arial", Font.ITALIC, 11));
        content.add(fotoStatus, gbc);

        // Nombre
        gbc.gridy = 2; gbc.gridwidth = 1;
        content.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        nombreField = new JTextField(actual.getNombre(), 15);
        content.add(nombreField, gbc);

        // Edad
        gbc.gridx = 0; gbc.gridy = 3;
        content.add(new JLabel("Edad:"), gbc);
        gbc.gridx = 1;
        edadField = new JTextField(String.valueOf(actual.getEdad()), 15);
        content.add(edadField, gbc);

        // Genero
        gbc.gridx = 0; gbc.gridy = 4;
        content.add(new JLabel("Género:"), gbc);
        gbc.gridx = 1;
        JPanel gp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gp.setBackground(Color.WHITE);
        masculinoBtn = new JRadioButton("M", actual.getGenero().equals("M"));
        femeninoBtn = new JRadioButton("F", actual.getGenero().equals("F"));
        ButtonGroup bgG = new ButtonGroup();
        bgG.add(masculinoBtn); bgG.add(femeninoBtn);
        gp.add(masculinoBtn); gp.add(femeninoBtn);
        content.add(gp, gbc);

        // Privacidad
        gbc.gridx = 0; gbc.gridy = 5;
        content.add(new JLabel("Privacidad:"), gbc);
        gbc.gridx = 1;
        JPanel pp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pp.setBackground(Color.WHITE);
        publicoBtn = new JRadioButton("Pública", actual.esPublico());
        privadoBtn = new JRadioButton("Privada", !actual.esPublico());
        ButtonGroup bgP = new ButtonGroup();
        bgP.add(publicoBtn); bgP.add(privadoBtn);
        pp.add(publicoBtn); pp.add(privadoBtn);
        content.add(pp, gbc);

        // Boton Desactivar
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JButton btnDesactivar = new JButton("Desactivar Cuenta");
        btnDesactivar.setBackground(new Color(237, 73, 86));
        btnDesactivar.setForeground(Color.WHITE);
        btnDesactivar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                AuthService.getInstance().desactivarCuenta(actual.getUsername());
                AuthService.getInstance().logout();
                dispose();
                mainFrame.showPanel("login");
            }
        });
        content.add(btnDesactivar, gbc);

        add(new JScrollPane(content), BorderLayout.CENTER);

        // Botones guardar/cancelar
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Guardar");
        btnSave.addActionListener(e -> {
            try {
                String nom = nombreField.getText().trim();
                int ed = Integer.parseInt(edadField.getText().trim());
                String gen = masculinoBtn.isSelected() ? "M" : "F";
                boolean esPub = publicoBtn.isSelected();

                if (nom.isEmpty()) throw new Exception("Nombre vacío");

                AuthService.getInstance().actualizarPerfil(actual.getUsername(), nom, ed, gen, esPub, rutaNuevaFoto);
                mainFrame.actualizarTodo();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Datos inválidos: " + ex.getMessage());
            }
        });

        JButton btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> dispose());
        actions.add(btnCancel);
        actions.add(btnSave);
        add(actions, BorderLayout.SOUTH);
    }
}
