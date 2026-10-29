package emergency;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Main application frame for Emergency Contact Assistant.
 * Coordinates sidebar navigation, CardLayout screen switching,
 * and data synchronization between UI panels, ContactManager, and FileHandler.
 */
public class MainFrame extends JFrame {

    // Screen identifiers
    public static final String SCREEN_DASHBOARD           = "DASHBOARD";
    public static final String SCREEN_ALL_CONTACTS        = "ALL_CONTACTS";
    public static final String SCREEN_ADD_CONTACT         = "ADD_CONTACT";
    public static final String SCREEN_EMERGENCY_DIRECTORY = "EMERGENCY_DIRECTORY";
    public static final String SCREEN_ABOUT               = "ABOUT";

    private final ContactManager contactManager;
    private final FileHandler fileHandler;

    // UI Panels
    private CardLayout cardLayout;
    private JPanel cardsPanel;

    private DashboardPanel dashboardPanel;
    private ContactListPanel contactListPanel;
    private ContactFormPanel contactFormPanel;
    private EmergencyDirectoryPanel emergencyDirectoryPanel;
    private AboutPanel aboutPanel;

    // Sidebar navigation tracking
    private final Map<String, JButton> navButtons = new HashMap<>();
    private String currentScreen = SCREEN_DASHBOARD;

    public MainFrame(ContactManager contactManager, FileHandler fileHandler) {
        super("Emergency Contact Assistant");
        this.contactManager = contactManager;
        this.fileHandler = fileHandler;

        initFrame();
        initSidebarAndCards();

        // Show Dashboard initially
        showScreen(SCREEN_DASHBOARD);
    }

    private void initFrame() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null); // Center on screen

        // Intercept close button to prompt confirmation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmAndExit();
            }
        });
    }

    private void initSidebarAndCards() {
        setLayout(new BorderLayout());

        // 1. Sidebar Panel (West)
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // 2. CardLayout Container (Center)
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(UIUtils.BG_MAIN);

        // Instantiate child panels
        dashboardPanel = new DashboardPanel(this, contactManager);
        contactListPanel = new ContactListPanel(this, contactManager);
        contactFormPanel = new ContactFormPanel(this, contactManager);
        emergencyDirectoryPanel = new EmergencyDirectoryPanel(this, contactManager);
        aboutPanel = new AboutPanel(this, fileHandler);

        // Add panels to CardLayout
        cardsPanel.add(dashboardPanel, SCREEN_DASHBOARD);
        cardsPanel.add(contactListPanel, SCREEN_ALL_CONTACTS);
        cardsPanel.add(contactFormPanel, SCREEN_ADD_CONTACT);
        cardsPanel.add(emergencyDirectoryPanel, SCREEN_EMERGENCY_DIRECTORY);
        cardsPanel.add(aboutPanel, SCREEN_ABOUT);

        add(cardsPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(UIUtils.NAVY_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, getHeight()));

        // 1. Branding at top
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(UIUtils.NAVY_SIDEBAR);
        brandPanel.setBorder(new EmptyBorder(24, 20, 24, 20));

        JLabel logoBadge = new JLabel("[+] EMERGENCY");
        logoBadge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoBadge.setForeground(UIUtils.RED_DANGER);

        JLabel brandTitle = new JLabel("Contact Assistant");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandTitle.setForeground(Color.WHITE);

        JLabel brandSub = new JLabel("College OOP Mini Project");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        brandSub.setForeground(new Color(148, 163, 184));

        brandPanel.add(logoBadge);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(brandTitle);
        brandPanel.add(Box.createVerticalStrut(2));
        brandPanel.add(brandSub);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        // 2. Navigation Items (Center)
        JPanel navListPanel = new JPanel();
        navListPanel.setLayout(new BoxLayout(navListPanel, BoxLayout.Y_AXIS));
        navListPanel.setBackground(UIUtils.NAVY_SIDEBAR);
        navListPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        addNavItem(navListPanel, "Dashboard", SCREEN_DASHBOARD);
        navListPanel.add(Box.createVerticalStrut(6));
        addNavItem(navListPanel, "All Contacts", SCREEN_ALL_CONTACTS);
        navListPanel.add(Box.createVerticalStrut(6));
        addNavItem(navListPanel, "Add Contact", SCREEN_ADD_CONTACT);
        navListPanel.add(Box.createVerticalStrut(6));
        addNavItem(navListPanel, "Emergency Directory", SCREEN_EMERGENCY_DIRECTORY);
        navListPanel.add(Box.createVerticalStrut(6));
        addNavItem(navListPanel, "About", SCREEN_ABOUT);

        sidebar.add(navListPanel, BorderLayout.CENTER);

        // 3. Exit Button at bottom
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UIUtils.NAVY_SIDEBAR);
        bottomPanel.setBorder(new EmptyBorder(16, 12, 20, 12));

        JButton exitButton = createNavButton("Exit Application", e -> confirmAndExit());
        exitButton.setForeground(new Color(248, 113, 113)); // Soft red
        bottomPanel.add(exitButton, BorderLayout.SOUTH);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private void addNavItem(JPanel container, String text, String screenKey) {
        JButton btn = createNavButton(text, e -> {
            if (screenKey.equals(SCREEN_ADD_CONTACT)) {
                contactFormPanel.setAddMode();
            }
            showScreen(screenKey);
        });
        navButtons.put(screenKey, btn);
        container.add(btn);
    }

    private JButton createNavButton(String text, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getBackground() != null && getBackground().getAlpha() > 0) {
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UIUtils.FONT_NAV_BTN);
        btn.setForeground(new Color(226, 232, 240));
        btn.setBackground(new Color(0, 0, 0, 0)); // transparent initially
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(216, 44));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addActionListener(listener);

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getForeground().equals(Color.WHITE)) {
                    btn.setBackground(UIUtils.NAVY_SIDEBAR_HOVER);
                    btn.repaint();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                updateNavStyles();
            }
        });

        return btn;
    }

    /**
     * Switches the active screen in CardLayout and updates sidebar highlight.
     *
     * @param screenKey Target screen key.
     */
    public void showScreen(String screenKey) {
        currentScreen = screenKey;
        cardLayout.show(cardsPanel, screenKey);
        updateNavStyles();

        // Refresh destination screen
        if (screenKey.equals(SCREEN_DASHBOARD)) {
            dashboardPanel.refreshData();
        } else if (screenKey.equals(SCREEN_ALL_CONTACTS)) {
            contactListPanel.refreshTable();
        } else if (screenKey.equals(SCREEN_EMERGENCY_DIRECTORY)) {
            emergencyDirectoryPanel.refreshData();
        }
    }

    private void updateNavStyles() {
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(currentScreen)) {
                btn.setBackground(UIUtils.NAVY_SIDEBAR_ACTIVE);
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                btn.setBackground(new Color(0, 0, 0, 0));
                btn.setForeground(new Color(203, 213, 225));
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
            btn.repaint();
        }
    }

    /**
     * Refreshes all data panels whenever contacts are added, modified, or deleted.
     */
    public void refreshAllScreens() {
        dashboardPanel.refreshData();
        contactListPanel.refreshTable();
        emergencyDirectoryPanel.refreshData();
    }

    /**
     * Navigates to All Contacts screen and applies a search query.
     */
    public void navigateToAllContactsWithSearch(String query) {
        contactListPanel.setSearchQuery(query);
        showScreen(SCREEN_ALL_CONTACTS);
    }

    /**
     * Opens Add Contact form pre-filled with a specific category.
     */
    public void addContactWithCategory(String category) {
        contactFormPanel.setAddModeWithCategory(category);
        showScreen(SCREEN_ADD_CONTACT);
    }

    /**
     * Opens Edit Contact screen for the specified contact.
     */
    public void openEditContactScreen(Contact contact) {
        if (contact != null) {
            contactFormPanel.setEditMode(contact);
            showScreen(SCREEN_ADD_CONTACT);
        }
    }

    /**
     * Displays a clean dialog with formatted contact information.
     */
    public void showContactDetailsDialog(Contact contact) {
        if (contact == null) return;

        JDialog dialog = new JDialog(this, "Contact Details - " + contact.getName(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(480, 480);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UIUtils.CARD_BG);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel titleLbl = new JLabel(contact.getName());
        titleLbl.setFont(UIUtils.FONT_HEADER_TITLE);
        titleLbl.setForeground(contact.isEmergencyService() ? UIUtils.RED_DANGER : UIUtils.BLUE_PRIMARY);

        JLabel catLbl = new JLabel(contact.getCategory() + " " + (contact.isEmergencyService() ? "(Emergency Service)" : "(Personal)"));
        catLbl.setFont(UIUtils.FONT_BOLD);
        catLbl.setForeground(UIUtils.TEXT_MUTED);

        JPanel topInfo = new JPanel();
        topInfo.setLayout(new BoxLayout(topInfo, BoxLayout.Y_AXIS));
        topInfo.setOpaque(false);
        topInfo.add(titleLbl);
        topInfo.add(Box.createVerticalStrut(4));
        topInfo.add(catLbl);
        panel.add(topInfo, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(contact.getFormattedDescription());
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setBackground(new Color(248, 250, 252));
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setOpaque(false);

        JButton editBtn = UIUtils.createStyledButton("Edit", UIUtils.BLUE_PRIMARY, Color.WHITE);
        editBtn.addActionListener(e -> {
            dialog.dispose();
            openEditContactScreen(contact);
        });

        JButton closeBtn = UIUtils.createStyledButton("Close", UIUtils.SLATE_SECONDARY, Color.WHITE);
        closeBtn.addActionListener(e -> dialog.dispose());

        btnBar.add(editBtn);
        btnBar.add(closeBtn);
        panel.add(btnBar, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Handles adding a new contact, saving to file, and updating UI.
     *
     * @param contact New contact to add.
     * @return true if successful, false otherwise.
     */
    public boolean handleAddContact(Contact contact) {
        try {
            contactManager.addContact(contact);
            persistContacts();
            refreshAllScreens();
            return true;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Cannot Add Contact",
                JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException ex) {
            // Rollback memory addition to keep memory in sync with file
            contactManager.deleteContact(contact.getContactId());
            JOptionPane.showMessageDialog(this,
                "Failed to save contact to file storage:\n" + ex.getMessage(),
                "File Storage Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Handles updating an existing contact, saving to file, and updating UI.
     *
     * @param id Contact ID.
     * @param updated Contact with updated details.
     * @return true if successful, false otherwise.
     */
    public boolean handleUpdateContact(String id, Contact updated) {
        Contact previous = contactManager.findById(id);
        if (previous == null) {
            JOptionPane.showMessageDialog(this,
                "Contact not found for update: " + id,
                "Update Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            contactManager.updateContact(id, updated);
            persistContacts();
            refreshAllScreens();
            return true;
        } catch (Exception ex) {
            // Rollback
            contactManager.updateContact(id, previous);
            JOptionPane.showMessageDialog(this,
                "Failed to update contact:\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Handles deleting a contact, saving to file, and updating UI.
     *
     * @param id Contact ID to delete.
     * @return true if successful, false otherwise.
     */
    public boolean handleDeleteContact(String id) {
        Contact previous = contactManager.findById(id);
        if (previous == null) return false;

        boolean deleted = contactManager.deleteContact(id);
        if (deleted) {
            try {
                persistContacts();
                refreshAllScreens();
                return true;
            } catch (IOException ex) {
                // Rollback
                contactManager.addContact(previous);
                JOptionPane.showMessageDialog(this,
                    "Failed to save deletion to file storage:\n" + ex.getMessage(),
                    "File Storage Error",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return false;
    }

    /**
     * Persists all in-memory contacts to the local data file.
     *
     * @throws IOException If write fails.
     */
    private void persistContacts() throws IOException {
        fileHandler.saveContacts(contactManager.getAllContacts());
    }

    /**
     * Loads the 3 sample records specified in Section 9 for testing and demonstration.
     */
    public void loadSampleData() {
        List<Contact> samples = new ArrayList<>();
        samples.add(new Contact(
            contactManager.generateNextId(),
            "Rahul",
            "9876543210",
            "Hospital",
            "Coimbatore Medical College Hospital, Coimbatore",
            "rahul@cmch.org",
            "Emergency Ward Contact (Sample Data)"
        ));

        // Generate next ID for 2nd
        contactManager.addContact(samples.get(0));

        samples.add(new Contact(
            contactManager.generateNextId(),
            "Arun",
            "9876501234",
            "Family",
            "Peelamedu, Coimbatore",
            "arun.family@example.com",
            "Brother - Personal Contact (Sample Data)"
        ));
        contactManager.addContact(samples.get(1));

        samples.add(new Contact(
            contactManager.generateNextId(),
            "Priya",
            "9876512345",
            "Police",
            "Gandhipuram Police Station, Coimbatore",
            "priya.police@dept.gov.in",
            "Sub-Inspector Helpline (Sample Data)"
        ));
        contactManager.addContact(samples.get(2));

        try {
            persistContacts();
            refreshAllScreens();
            JOptionPane.showMessageDialog(this,
                "Sample test records loaded successfully:\n\n" +
                "1. Rahul (Hospital, Coimbatore)\n" +
                "2. Arun (Family, Coimbatore)\n" +
                "3. Priya (Police, Coimbatore)",
                "Sample Data Loaded",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to save sample data to storage: " + ex.getMessage(),
                "Storage Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Prompts confirmation before closing the application safely.
     */
    public void confirmAndExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit Emergency Contact Assistant?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Cleanup and exit
            dispose();
            System.exit(0);
        }
    }
}

