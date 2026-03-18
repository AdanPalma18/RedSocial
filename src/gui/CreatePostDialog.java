package gui;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import services.AuthService;
import services.PostService;

public class CreatePostDialog extends JDialog {

    private static final Color IG_BLUE = new Color(0, 149, 246);
    private static final Color BORDER_GRAY = new Color(219, 219, 219);
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color TEXT_GRAY = new Color(142, 142, 142);

    private final JFrame parent;
    private String rutaImagenSeleccionada = null;

    // Step 1 components
    private JPanel step1Panel;

    // Step 2 components
    private JPanel step2Panel;
    private JLabel previewLabel;
    private JTextArea captionArea;
    private JLabel charCounter;
    private JTextField hashtagsField;

    // Main layout
    private JPanel headerPanel;
    private JLabel headerTitle;
    private JButton btnBack;
    private JButton btnShare;
    private JPanel cardContainer;
    private CardLayout cardLayout;

    public CreatePostDialog(JFrame parent) {
        super(parent, true);
        this.parent = parent;
        setTitle("Create new post");
        setUndecorated(true);
        setSize(820, 530);
        setLocationRelativeTo(parent);
        setBackground(BG_WHITE);

        initComponents();
        showStep1();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_WHITE);
        root.setBorder(new LineBorder(BORDER_GRAY, 1));

        // ─── HEADER ───────────────────────────────────────────────
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_WHITE);
        headerPanel.setPreferredSize(new Dimension(820, 44));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_GRAY),
            new EmptyBorder(6, 12, 6, 12)
        ));

        btnBack = new JButton("←");
        btnBack.setFont(new Font("Arial", Font.BOLD, 18));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> showStep1());
        btnBack.setVisible(false);

        headerTitle = new JLabel("Create new post", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnClose = new JButton("X");
        btnClose.setFont(new Font("Arial", Font.BOLD, 20));
        btnClose.setForeground(new Color(38, 38, 38));
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        btnShare = new JButton("Share");
        btnShare.setFont(new Font("Arial", Font.BOLD, 14));
        btnShare.setForeground(IG_BLUE);
        btnShare.setBorderPainted(false);
        btnShare.setContentAreaFilled(false);
        btnShare.setFocusPainted(false);
        btnShare.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShare.setVisible(false);
        btnShare.addActionListener(e -> publicar());

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftHeader.setOpaque(false);
        leftHeader.add(btnBack);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightHeader.setOpaque(false);
        rightHeader.add(btnShare);
        rightHeader.add(btnClose);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        // ─── CARD CONTAINER ───────────────────────────────────────
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(BG_WHITE);

        buildStep1();
        buildStep2();

        cardContainer.add(step1Panel, "step1");
        cardContainer.add(step2Panel, "step2");

        root.add(headerPanel, BorderLayout.NORTH);
        root.add(cardContainer, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ─── STEP 1: SELECT IMAGE ──────────────────────────────────────
    private void buildStep1() {
        step1Panel = new JPanel(new GridBagLayout());
        step1Panel.setBackground(BG_WHITE);

        JPanel dropZone = new JPanel(new GridBagLayout());
        dropZone.setBackground(BG_WHITE);
        dropZone.setPreferredSize(new Dimension(820, 486));

        // Icon
        JLabel iconLabel = new JLabel("🖼", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));

        JLabel dragText = new JLabel("Drag photos and videos here", SwingConstants.CENTER);
        dragText.setFont(new Font("Arial", Font.PLAIN, 18));
        dragText.setForeground(new Color(38, 38, 38));

        JButton selectBtn = createBlueButton("Select from computer");
        selectBtn.addActionListener(e -> abrirFileChooser());

        // Drag and drop support
        new DropTarget(dropZone, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    @SuppressWarnings("unchecked")
                    List<File> files = (List<File>) dtde.getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) {
                        File f = files.get(0);
                        String name = f.getName().toLowerCase();
                        if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) {
                            rutaImagenSeleccionada = f.getAbsolutePath();
                            showStep2();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.insets = new Insets(0, 0, 10, 0);
        dropZone.add(iconLabel, c);
        c.gridy = 1; c.insets = new Insets(0, 0, 16, 0);
        dropZone.add(dragText, c);
        c.gridy = 2; c.insets = new Insets(0, 0, 0, 0);
        dropZone.add(selectBtn, c);

        GridBagConstraints outer = new GridBagConstraints();
        step1Panel.add(dropZone, outer);
    }

    // ─── STEP 2: CAPTION + HASHTAGS ───────────────────────────────
    private void buildStep2() {
        step2Panel = new JPanel(new BorderLayout());
        step2Panel.setBackground(BG_WHITE);

        // Left: image preview
        previewLabel = new JLabel("", SwingConstants.CENTER);
        previewLabel.setPreferredSize(new Dimension(490, 486));
        previewLabel.setBackground(Color.BLACK);
        previewLabel.setOpaque(true);

        // Right: caption panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(BG_WHITE);
        rightPanel.setPreferredSize(new Dimension(330, 486));
        rightPanel.setBorder(new MatteBorder(0, 1, 0, 0, BORDER_GRAY));

        // User info row
        JPanel userRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        userRow.setBackground(BG_WHITE);
        userRow.setMaximumSize(new Dimension(330, 60));

        model.Usuario u = AuthService.getInstance().getUsuarioActual();
        if (u != null) {
            CircularImageLabel pic = new CircularImageLabel(
                u.getFotoPerfil() != null ? u.getFotoPerfil() : "", 32);
            JLabel unameLabel = new JLabel(u.getUsername());
            unameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            userRow.add(pic);
            userRow.add(unameLabel);
        }

        // Caption textarea
        captionArea = new JTextArea();
        captionArea.setLineWrap(true);
        captionArea.setWrapStyleWord(true);
        captionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        captionArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        captionArea.setBackground(BG_WHITE);
        captionArea.setRows(8);
        captionArea.setMaximumSize(new Dimension(330, 200));

        // Placeholder
        setPlaceholder(captionArea, "Write a caption...");

        JScrollPane captionScroll = new JScrollPane(captionArea);
        captionScroll.setBorder(BorderFactory.createEmptyBorder());
        captionScroll.setMaximumSize(new Dimension(330, 180));

        // Char counter
        charCounter = new JLabel("0/220");
        charCounter.setFont(new Font("Arial", Font.PLAIN, 12));
        charCounter.setForeground(TEXT_GRAY);
        charCounter.setAlignmentX(Component.RIGHT_ALIGNMENT);
        charCounter.setBorder(BorderFactory.createEmptyBorder(2, 0, 6, 12));

        captionArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateCount(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateCount(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateCount(); }
            private void updateCount() {
                String txt = captionArea.getText();
                int len = txt.equals("Write a caption...") ? 0 : txt.length();
                if (len > 220) {
                    captionArea.setText(txt.substring(0, 220));
                    len = 220;
                }
                charCounter.setText(len + "/220");
            }
        });

        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setForeground(BORDER_GRAY);
        sep.setMaximumSize(new Dimension(330, 1));

        // Hashtags field
        JPanel hashRow = new JPanel(new BorderLayout());
        hashRow.setBackground(BG_WHITE);
        hashRow.setMaximumSize(new Dimension(330, 50));
        hashRow.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel hashIcon = new JLabel("#");
        hashIcon.setFont(new Font("Arial", Font.BOLD, 20));
        hashIcon.setForeground(TEXT_GRAY);
        hashIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));

        hashtagsField = new JTextField();
        hashtagsField.setFont(new Font("Arial", Font.PLAIN, 14));
        hashtagsField.setBorder(BorderFactory.createEmptyBorder());
        hashtagsField.setBackground(BG_WHITE);
        setTextFieldPlaceholder(hashtagsField, "Add hashtags...");

        hashRow.add(hashIcon, BorderLayout.WEST);
        hashRow.add(hashtagsField, BorderLayout.CENTER);

        rightPanel.add(userRow);
        rightPanel.add(captionScroll);
        rightPanel.add(charCounter);
        rightPanel.add(sep);
        rightPanel.add(hashRow);
        rightPanel.add(Box.createVerticalGlue());

        step2Panel.add(previewLabel, BorderLayout.CENTER);
        step2Panel.add(rightPanel, BorderLayout.EAST);
    }

    // ─── NAVIGATION ───────────────────────────────────────────────
    private void showStep1() {
        headerTitle.setText("Create new post");
        btnBack.setVisible(false);
        btnShare.setVisible(false);
        cardLayout.show(cardContainer, "step1");
    }

    private void showStep2() {
        // Load preview
        try {
            ImageIcon icon = new ImageIcon(rutaImagenSeleccionada);
            int pw = 490, ph = 486;
            int iw = icon.getIconWidth(), ih = icon.getIconHeight();
            double scale = Math.min((double) pw / iw, (double) ph / ih);
            int nw = (int) (iw * scale), nh = (int) (ih * scale);
            Image scaled = icon.getImage().getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
            previewLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            previewLabel.setText("Preview unavailable");
        }

        // Refresh user info panel (in case it wasn't built yet)
        headerTitle.setText("New post");
        btnBack.setVisible(true);
        btnShare.setVisible(true);
        captionArea.setText("");
        hashtagsField.setText("");
        charCounter.setText("0/220");
        cardLayout.show(cardContainer, "step2");
    }

    // ─── FILE CHOOSER ─────────────────────────────────────────────
    private void abrirFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Images (JPG, PNG)", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            rutaImagenSeleccionada = chooser.getSelectedFile().getAbsolutePath();
            showStep2();
        }
    }

    // ─── PUBLISH ──────────────────────────────────────────────────
    private void publicar() {
        model.Usuario u = AuthService.getInstance().getUsuarioActual();
        if (u == null) return;

        String desc = captionArea.getText().trim();
        if (desc.equals("Write a caption...")) desc = "";

        String tags = hashtagsField.getText().trim();
        if (tags.equals("Add hashtags...")) tags = "";

        // Merge hashtags into caption
        String contenidoFinal = desc;
        if (!tags.isEmpty()) {
            contenidoFinal = (desc.isEmpty() ? "" : desc + " ") + tags;
        }

        PostService.getInstance().crearPublicacion(
            u.getUsername(),
            contenidoFinal,
            rutaImagenSeleccionada != null ? rutaImagenSeleccionada : ""
        );

        dispose();

        // Refresh feed
        if (parent instanceof MainFrame) {
            ((MainFrame) parent).refreshFeed();
        }
    }

    // ─── HELPERS ──────────────────────────────────────────────────
    private JButton createBlueButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(IG_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(210, 36));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0, 122, 210));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(IG_BLUE);
            }
        });
        return btn;
    }

    private void setPlaceholder(JTextArea area, String placeholder) {
        area.setForeground(TEXT_GRAY);
        area.setText(placeholder);
        area.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (area.getText().equals(placeholder)) {
                    area.setText("");
                    area.setForeground(new Color(38, 38, 38));
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (area.getText().isEmpty()) {
                    area.setForeground(TEXT_GRAY);
                    area.setText(placeholder);
                }
            }
        });
    }

    private void setTextFieldPlaceholder(JTextField field, String placeholder) {
        field.setForeground(TEXT_GRAY);
        field.setText(placeholder);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(38, 38, 38));
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(TEXT_GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }
}
