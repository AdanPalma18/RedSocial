package gui;

import java.awt.*;
import javax.swing.*;
import utils.Constants;

public class MainFrame extends JFrame {
    private final JPanel mainContainer;
    private final CardLayout cardLayout;
    private final SidebarPanel sidebar;
    private final RightPanel rightPanel;
    private final JPanel contentPanel;
    private FeedPanel feedPanel;
    private ProfilePanel profilePanel;
    private InboxPanel inboxPanel;
    private OtherProfilePanel otherProfilePanel;
    private RequestsPanel requestsPanel;
    private SearchPanel searchPanel;
    private BottomNavPanel bottomNav;

    public MainFrame() {
        setTitle("Mini Instagram");
        if (utils.Constants.MODO == utils.Constants.Modo.MOBILE) {
            setSize(utils.Constants.MOBILE_WIDTH, utils.Constants.MOBILE_HEIGHT);
        } else {
            setSize(utils.Constants.DESKTOP_WIDTH, utils.Constants.DESKTOP_HEIGHT);
        }
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        services.AuthService.getInstance().crearUsuariosBootstrap();
        services.StickerService.getInstance();
        
        // Iniciar Servidor de Sockets local (Puerto 5000)
        services.SocketServer.getInstance().start();

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        feedPanel = new FeedPanel();
        profilePanel = new ProfilePanel();
        inboxPanel = new InboxPanel();
        otherProfilePanel = new OtherProfilePanel(this);
        requestsPanel = new RequestsPanel(this);
        searchPanel = new SearchPanel();

        mainContainer.add(new LoginPanel(this), "login");
        mainContainer.add(new RegisterPanel(this), "register");
        mainContainer.add(feedPanel, "feed");
        mainContainer.add(profilePanel, "profile");
        mainContainer.add(inboxPanel, "inbox");
        mainContainer.add(searchPanel, "search");
        mainContainer.add(otherProfilePanel, "otherProfile");
        mainContainer.add(requestsPanel, "requests");

        sidebar = new SidebarPanel(this);
        rightPanel = new RightPanel(this);
        bottomNav = new BottomNavPanel(this);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainContainer, BorderLayout.CENTER);

        add(contentPanel);

        cardLayout.show(mainContainer, "login");

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                services.InboxService.getInstance().desconectarCliente();
            }
        });

        setVisible(true);
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainContainer, panelName);

        if (panelName.equals("login") || panelName.equals("register")) {
            contentPanel.remove(sidebar);
            contentPanel.remove(rightPanel);
            contentPanel.remove(bottomNav);
        } else {
            if (utils.Constants.MODO == utils.Constants.Modo.DESKTOP) {
                if (!isComponentInPanel(sidebar)) {
                    contentPanel.add(sidebar, BorderLayout.WEST);
                }
                // Right panel only on feed
                if (panelName.equals("feed")) {
                    if (!isComponentInPanel(rightPanel)) {
                        contentPanel.add(rightPanel, BorderLayout.EAST);
                    }
                    rightPanel.actualizar();
                } else {
                    contentPanel.remove(rightPanel);
                }
            } else {
                // Modo MOBILE
                contentPanel.remove(sidebar);
                contentPanel.remove(rightPanel);
                if (!isComponentInPanel(bottomNav)) {
                    contentPanel.add(bottomNav, BorderLayout.SOUTH);
                }
            }

            if (panelName.equals("feed")) {
                feedPanel.cargarFeed();
                sidebar.actualizar();
            } else if (panelName.equals("profile")) {
                profilePanel.cargarPerfil();
            } else if (panelName.equals("inbox")) {
                inboxPanel.cargarChats();
            } else if (panelName.equals("requests")) {
                requestsPanel.cargarSolicitudes();
            } else if (panelName.equals("search")) {
                searchPanel.buscar();
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void refreshFeed() {
        feedPanel.cargarFeed();
    }

    public void actualizarTodo() {
        sidebar.actualizar();
        bottomNav.actualizar();
        rightPanel.actualizar();
        feedPanel.cargarFeed();
        profilePanel.cargarPerfil();
    }

    public void showOtherProfile(String username) {
        otherProfilePanel.cargarPerfil(username);
        showPanel("otherProfile");
    }

    private boolean isComponentInPanel(Component comp) {
        for (Component c : contentPanel.getComponents()) {
            if (c == comp)
                return true;
        }
        return false;
    }
}
