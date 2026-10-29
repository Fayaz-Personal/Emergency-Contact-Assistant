package emergency;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Implements the Dashboard interface showing application statistics,
 * quick-action buttons, quick search, and recently added contacts.
 */
public class DashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ContactManager contactManager;

    // Stat labels
    private JLabel totalContactsLabel;
    private JLabel emergencyContactsLabel;
    private JLabel personalContactsLabel;

    // Table
    private DefaultTableModel tableModel;
    private JTable recentTable;

    // Search
    private JTextField quickSearchField;

    public DashboardPanel(MainFrame mainFrame, ContactManager contactManager) {
        this.mainFrame = mainFrame;
        this.contactManager = contactManager;

        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_MAIN);

        initUI();
    }

    private void initUI() {
        // 1. Header
        JPanel headerPanel = UIUtils.createHeaderPanel(
            "Dashboard Overview",
            "Welcome to Emergency Contact Assistant - Manage and access crucial contacts quickly"
        );
        add(headerPanel, BorderLayout.NORTH);

        // 2. Central content container
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIUtils.BG_MAIN);
        contentPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // 2A. Statistics Cards Grid (3 cards)
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        totalContactsLabel = new JLabel("0", SwingConstants.CENTER);
        emergencyContactsLabel = new JLabel("0", SwingConstants.CENTER);
        personalContactsLabel = new JLabel("0", SwingConstants.CENTER);

        statsPanel.add(createStatCard("Total Contacts", totalContactsLabel, UIUtils.BLUE_PRIMARY, "All registered entries"));
        statsPanel.add(createStatCard("Emergency Services", emergencyContactsLabel, UIUtils.RED_DANGER, "Police, Hospital, Ambulance, Fire"));
        statsPanel.add(createStatCard("Personal Contacts", personalContactsLabel, UIUtils.GREEN_SUCCESS, "Family, Friends, Others"));

        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // 2B. Quick Actions Bar
        JPanel actionsCard = UIUtils.createCardPanel();
        actionsCard.setLayout(new BorderLayout());
        actionsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftActions.setOpaque(false);

        JButton addContactBtn = UIUtils.createStyledButton("+ Add New Contact", UIUtils.BLUE_PRIMARY, Color.WHITE);
        addContactBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.SCREEN_ADD_CONTACT));

        JButton viewAllBtn = UIUtils.createStyledButton("View All Contacts", UIUtils.SLATE_SECONDARY, Color.WHITE);
        viewAllBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.SCREEN_ALL_CONTACTS));

        JButton loadSampleBtn = UIUtils.createStyledButton("Load Sample Data", new Color(14, 116, 144), Color.WHITE);
        loadSampleBtn.setToolTipText("Load verified sample records (Rahul, Arun, Priya) for testing");
        loadSampleBtn.addActionListener(e -> promptLoadSampleData());

        leftActions.add(addContactBtn);
        leftActions.add(viewAllBtn);
        leftActions.add(loadSampleBtn);

        // Right side quick search
        JPanel rightSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightSearch.setOpaque(false);

        quickSearchField = new JTextField(16);
        UIUtils.styleTextField(quickSearchField);
        quickSearchField.setToolTipText("Quick search by name or phone");

        JButton searchBtn = UIUtils.createStyledButton("Search", UIUtils.BLUE_PRIMARY, Color.WHITE);
        searchBtn.addActionListener(e -> performQuickSearch());
        quickSearchField.addActionListener(e -> performQuickSearch());

        rightSearch.add(new JLabel("Quick Search:"));
        rightSearch.add(quickSearchField);
        rightSearch.add(searchBtn);

        actionsCard.add(leftActions, BorderLayout.WEST);
        actionsCard.add(rightSearch, BorderLayout.EAST);

        contentPanel.add(actionsCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // 2C. Recently Added Contacts Section
        JPanel recentPanel = UIUtils.createCardPanel();
        recentPanel.setLayout(new BorderLayout(0, 12));

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);

        JLabel recentTitle = new JLabel("Recently Added Contacts");
        recentTitle.setFont(UIUtils.FONT_SECTION_TITLE);
        recentTitle.setForeground(UIUtils.TEXT_DARK);

        JLabel recentHint = new JLabel("Double-click any row to view full details");
        recentHint.setFont(UIUtils.FONT_SMALL);
        recentHint.setForeground(UIUtils.TEXT_MUTED);

        recentHeader.add(recentTitle, BorderLayout.WEST);
        recentHeader.add(recentHint, BorderLayout.EAST);

        recentPanel.add(recentHeader, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Contact ID", "Name", "Phone Number", "Category", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recentTable = new JTable(tableModel);
        UIUtils.styleTable(recentTable);

        // Double click listener to view details
        recentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && recentTable.getSelectedRow() != -1) {
                    viewSelectedContact();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(recentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        recentPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom detail button
        JPanel recentBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        recentBottom.setOpaque(false);

        JButton viewDetailBtn = UIUtils.createStyledButton("View Contact Details", UIUtils.SLATE_SECONDARY, Color.WHITE);
        viewDetailBtn.addActionListener(e -> viewSelectedContact());
        recentBottom.add(viewDetailBtn);

        recentPanel.add(recentBottom, BorderLayout.SOUTH);

        contentPanel.add(recentPanel);

        add(contentPanel, BorderLayout.CENTER);

        // Refresh initially
        refreshData();
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor, String subtitle) {
        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new BorderLayout(0, 6));

        // Top accent bar
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(accentBar.getWidth(), 4));
        card.add(accentBar, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(8, 0, 0, 0));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UIUtils.FONT_BOLD);
        titleLbl.setForeground(UIUtils.TEXT_MUTED);

        valueLabel.setFont(UIUtils.FONT_STAT_VAL);
        valueLabel.setForeground(accentColor);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(UIUtils.FONT_SMALL);
        subLbl.setForeground(UIUtils.TEXT_MUTED);

        body.add(titleLbl);
        body.add(Box.createVerticalStrut(4));
        body.add(valueLabel);
        body.add(Box.createVerticalStrut(4));
        body.add(subLbl);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    /**
     * Refreshes the dashboard stats and recent contacts table from ContactManager.
     */
    public void refreshData() {
        // Update stats
        totalContactsLabel.setText(String.valueOf(contactManager.getTotalContacts()));
        emergencyContactsLabel.setText(String.valueOf(contactManager.getEmergencyContactCount()));
        personalContactsLabel.setText(String.valueOf(contactManager.getPersonalContactCount()));

        // Update table with latest 10 contacts
        tableModel.setRowCount(0);
        List<Contact> recentList = contactManager.getRecentContacts(10);
        for (Contact c : recentList) {
            tableModel.addRow(new Object[]{
                c.getContactId(),
                c.getName(),
                c.getPhoneNumber(),
                c.getCategory(),
                c.getAddress()
            });
        }
    }

    private void performQuickSearch() {
        String query = quickSearchField.getText().trim();
        if (!query.isEmpty()) {
            mainFrame.navigateToAllContactsWithSearch(query);
            quickSearchField.setText("");
        } else {
            mainFrame.showScreen(MainFrame.SCREEN_ALL_CONTACTS);
        }
    }

    private void viewSelectedContact() {
        int selectedRow = recentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a contact from the table to view details.",
                "Selection Required",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String contactId = (String) tableModel.getValueAt(selectedRow, 0);
        Contact contact = contactManager.findById(contactId);
        if (contact != null) {
            mainFrame.showContactDetailsDialog(contact);
        }
    }

    private void promptLoadSampleData() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Do you want to load the 3 verified sample contacts?\n\n" +
            "1. Rahul - 9876543210 (Hospital, Coimbatore)\n" +
            "2. Arun - 9876501234 (Family, Coimbatore)\n" +
            "3. Priya - 9876512345 (Police, Coimbatore)\n\n" +
            "Note: These will be added to your contacts and saved locally.",
            "Confirm Load Sample Data",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            mainFrame.loadSampleData();
        }
    }
}

