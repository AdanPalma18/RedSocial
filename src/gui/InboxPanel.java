package gui;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import model.*;
import services.*;

public class InboxPanel extends JPanel implements MessageBus.MessageListener {
    private JPanel chatListPanel;
    private JPanel chatPanel;
    private String chatActual;
    private JPanel messagesPanel;
    private JScrollPane chatScrollPane;
    private JSplitPane splitPane;

    public InboxPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel leftPanel = crearPanelIzquierdo();
        chatPanel = crearPanelChat();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, chatPanel);
        splitPane.setDividerLocation(300);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        add(splitPane, BorderLayout.CENTER);

        InboxService.getInstance().agregarListener(this);
    }

    @Override
    public void onNuevoMensaje(String remitente, String contenido) {
        SwingUtilities.invokeLater(() -> {
            if (chatActual != null && chatActual.equals(remitente)) {
                actualizarChat();
            }
            cargarChats();
        });
    }

    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel title = new JLabel("Mensajes");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.NORTH);

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(260, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(219, 219, 219), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        headerPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        headerPanel.add(searchField, BorderLayout.SOUTH);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { buscar(searchField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { buscar(searchField.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { buscar(searchField.getText()); }
        });

        chatListPanel = new JPanel();
        chatListPanel.setLayout(new BoxLayout(chatListPanel, BoxLayout.Y_AXIS));
        chatListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(chatListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelChat() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel emptyLabel = new JLabel("Selecciona un chat para comenzar");
        emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emptyLabel.setForeground(Color.GRAY);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(emptyLabel, BorderLayout.CENTER);

        return panel;
    }

    public void cerrarChatActual() {
        chatActual = null;
        if (splitPane != null) {
            splitPane.setRightComponent(crearPanelChat());
        }
    }

    public void cargarChats() {
        chatListPanel.removeAll();

        Usuario usuarioActual = AuthService.getInstance().getUsuarioActual();
        if (usuarioActual == null) {
            return;
        }

        Map<String, String> chats = InboxService.getInstance().obtenerChats(usuarioActual.getUsername());

        if (chats.isEmpty()) {
            JLabel emptyLabel = new JLabel("No tienes conversaciones");
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            chatListPanel.add(emptyLabel);
        } else {
            for (Map.Entry<String, String> entry : chats.entrySet()) {
                chatListPanel.add(crearChatItem(entry.getKey(), entry.getValue()));
            }
        }

        chatListPanel.revalidate();
        chatListPanel.repaint();
    }

    private JPanel crearChatItem(String username, String ultimoMensaje) {
        JPanel item = new JPanel(new BorderLayout(10, 5));
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Usuario u = AuthService.getInstance().getUsuario(username);
        String foto = (u != null) ? u.getFotoPerfil() : "";
        CircularImageLabel profilePic = new CircularImageLabel(foto, 50);
        item.add(profilePic, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        String preview = ultimoMensaje.length() > 30 ? 
            ultimoMensaje.substring(0, 30) + "..." : ultimoMensaje;
        JLabel messageLabel = new JLabel(preview);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setForeground(Color.GRAY);

        textPanel.add(usernameLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(messageLabel);

        item.add(textPanel, BorderLayout.CENTER);

        Usuario usuarioActual = AuthService.getInstance().getUsuarioActual();
        int noLeidos = InboxService.getInstance().contarMensajesNoLeidos(
            usuarioActual.getUsername(), username
        );

        if (noLeidos > 0) {
            JLabel badge = new JLabel(String.valueOf(noLeidos));
            badge.setFont(new Font("Arial", Font.BOLD, 11));
            badge.setForeground(Color.WHITE);
            badge.setBackground(new Color(0, 149, 246));
            badge.setOpaque(true);
            badge.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 7));
            badge.setHorizontalAlignment(SwingConstants.CENTER);
            item.add(badge, BorderLayout.EAST);
        }

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                abrirChat(username);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(250, 250, 250));
                textPanel.setBackground(new Color(250, 250, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(Color.WHITE);
                textPanel.setBackground(Color.WHITE);
            }
        });

        return item;
    }

    private void abrirChat(String username) {
        chatActual = username;
        
        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftHeader.setBackground(Color.WHITE);
        
        Usuario u = AuthService.getInstance().getUsuario(username);
        String foto = (u != null) ? u.getFotoPerfil() : "";
        CircularImageLabel pic = new CircularImageLabel(foto, 40);
        
        JLabel chatTitle = new JLabel(username);
        chatTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        leftHeader.add(pic);
        leftHeader.add(chatTitle);
        headerPanel.add(leftHeader, BorderLayout.WEST);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        headerActions.setBackground(Color.WHITE);
        
        JButton deleteBtn = new JButton("Eliminar");
        deleteBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        deleteBtn.setForeground(Color.RED);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Estás seguro de eliminar esta conversación?", "Eliminar", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Usuario actual = AuthService.getInstance().getUsuarioActual();
                if (actual != null) {
                    InboxService.getInstance().eliminarConversacion(
                        actual.getUsername(), username);
                    chatActual = null;
                    splitPane.setRightComponent(crearPanelChat());
                    cargarChats();
                }
            }
        });
        headerActions.add(deleteBtn);
        
        JButton closeBtn = new JButton(utils.IconHelper.get("close", 18));
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> {
            chatActual = null;
            splitPane.setRightComponent(crearPanelChat());
        });
        headerActions.add(closeBtn);
        headerPanel.add(headerActions, BorderLayout.EAST);

        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(Color.WHITE);
        messagesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cargarMensajes();

        chatScrollPane = new JScrollPane(messagesPanel);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JTextField messageField = new JTextField();
        messageField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JButton stickerBtn = new JButton("😊");
        stickerBtn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        stickerBtn.setBackground(Color.WHITE);
        stickerBtn.setFocusPainted(false);
        stickerBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        stickerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        stickerBtn.setToolTipText("Enviar sticker");
        
        stickerBtn.addActionListener(e -> mostrarSelectorStickers(username));

        JButton sendBtn = new JButton("Enviar");
        sendBtn.setBackground(new Color(0, 149, 246));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        sendBtn.setFocusPainted(false);
        sendBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        sendBtn.addActionListener(e -> {
            String texto = messageField.getText().trim();
            if (!texto.isEmpty()) {
                Usuario usuarioActual = AuthService.getInstance().getUsuarioActual();
                InboxService.getInstance().enviarMensaje(
                    usuarioActual.getUsername(),
                    username,
                    texto
                );
                messageField.setText("");
                actualizarChat();
                cargarChats();
            }
        });

        messageField.addActionListener(e -> sendBtn.doClick());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(stickerBtn);
        rightPanel.add(sendBtn);

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(rightPanel, BorderLayout.EAST);

        chatPanel.add(headerPanel, BorderLayout.NORTH);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(chatPanel);
        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private void cargarMensajes() {
        Usuario usuarioActual = AuthService.getInstance().getUsuarioActual();
        List<Mensaje> mensajes = InboxService.getInstance().leerConversacion(
            usuarioActual.getUsername(), chatActual
        );

        for (Mensaje mensaje : mensajes) {
            messagesPanel.add(crearMensajeBurbuja(mensaje, usuarioActual.getUsername()));
            messagesPanel.add(Box.createVerticalStrut(5));
        }
    }

    private void actualizarChat() {
        if (messagesPanel != null && chatActual != null) {
            messagesPanel.removeAll();
            cargarMensajes();
            messagesPanel.revalidate();
            messagesPanel.repaint();

            SwingUtilities.invokeLater(() -> {
                JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            });
        }
    }

    private JPanel crearMensajeBurbuja(Mensaje mensaje, String usuarioActual) {
        boolean esMio = mensaje.getRemitente().equals(usuarioActual);

        JPanel container = new JPanel(new FlowLayout(
            esMio ? FlowLayout.RIGHT : FlowLayout.LEFT, 5, 0
        ));
        container.setBackground(Color.WHITE);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel bubble = new JPanel(new BorderLayout());
        
        if (mensaje.esSticker()) {
            bubble.setBackground(Color.WHITE);
            bubble.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            try {
                ImageIcon icon = StickerService.getInstance().getCachedIcon(mensaje.getContenido(), 120);
                JLabel stickerLabel = new JLabel(icon);
                bubble.add(stickerLabel, BorderLayout.CENTER);
            } catch (Exception e) {
                JLabel stickerLabel = new JLabel("[Sticker]");
                bubble.add(stickerLabel, BorderLayout.CENTER);
            }
        } else {
            bubble.setBackground(esMio ? new Color(0, 149, 246) : new Color(240, 240, 240));
            bubble.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            JTextArea textArea = new JTextArea(mensaje.getContenido());
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
            textArea.setForeground(esMio ? Color.WHITE : Color.BLACK);
            textArea.setBackground(esMio ? new Color(0, 149, 246) : new Color(240, 240, 240));
            textArea.setBorder(BorderFactory.createEmptyBorder());

            bubble.add(textArea, BorderLayout.CENTER);
        }

        container.add(bubble);

        return container;
    }

    private void buscar(String query) {
        if (query == null || query.trim().isEmpty()) {
            cargarChats();
            return;
        }

        chatListPanel.removeAll();
        Usuario actual = AuthService.getInstance().getUsuarioActual();
        if (actual == null) return;

        Set<String> seguidos = FollowService.getInstance().obtenerFollowing(actual.getUsername());
        String q = query.toLowerCase().trim();

        for (String seguido : seguidos) {
            if (seguido.toLowerCase().contains(q)) {
                chatListPanel.add(crearChatItem(seguido, "Nuevo mensaje..."));
            }
        }

        chatListPanel.revalidate();
        chatListPanel.repaint();
    }

    private void mostrarSelectorStickers(String destinatario) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Seleccionar Sticker", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel stickersPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        stickersPanel.setBackground(Color.WHITE);
        stickersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Usuario usuarioActual = AuthService.getInstance().getUsuarioActual();
        
        List<Sticker> globales = StickerService.getInstance().obtenerStickersGlobales();
        List<Sticker> personales = (usuarioActual != null) ? StickerService.getInstance().obtenerStickersPersonales(usuarioActual.getUsername()) : new ArrayList<>();
        
        List<Sticker> todos = new ArrayList<>(globales);
        todos.addAll(personales);

        for (Sticker sticker : todos) {
            String rutaImg = sticker.getRutaImagen();
            ImageIcon iconPreview = StickerService.getInstance().getCachedIcon(rutaImg, 60);
                
            JButton stickerBtn = new JButton(iconPreview);
            stickerBtn.setBackground(Color.WHITE);
            stickerBtn.setFocusPainted(false);
            stickerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            stickerBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            
            stickerBtn.addActionListener(e -> {
                InboxService.getInstance().enviarSticker(
                    usuarioActual.getUsername(),
                    destinatario,
                    rutaImg
                );
                dialog.dispose();
                actualizarChat();
                cargarChats();
            });
            
            stickersPanel.add(stickerBtn);
        }

        JScrollPane scrollPane = new JScrollPane(stickersPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JButton btnImportar = new JButton("Importar Sticker (Imagen)");
        btnImportar.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imágenes (PNG, JPG, JPEG)", "png", "jpg", "jpeg"));
            if (fc.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                String ruta = fc.getSelectedFile().getAbsolutePath();
                String nombre = fc.getSelectedFile().getName();
                StickerService.getInstance().importarStickerPersonal(usuarioActual.getUsername(), ruta, nombre);
                dialog.dispose();
                mostrarSelectorStickers(destinatario); // Recargar
            }
        });

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(btnImportar, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
