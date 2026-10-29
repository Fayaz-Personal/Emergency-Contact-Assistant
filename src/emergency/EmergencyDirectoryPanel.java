package emergency;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
 * Dedicated Emergency Directory screen displaying exclusively emergency-service contacts:
 * Police, Hospital, Ambulance, and Fire Service.
 * Does not hard-code universal numbers; displays verified user-entered local records.
 */
public class EmergencyDirectoryPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ContactManager contactManager;

    private DefaultTableModel tableModel;
    private JTable emergencyTable;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JLabel statusLabel;

    private JPanel tableContainerCard;
    private CardLayout contentCardLayout;
    private static final String CARD_TABLE = "TABLE";
    private static final String CARD_EMPTY = "EMPTY";

    public EmergencyDirectoryPanel(MainFrame mainFrame, ContactManager contactManager) {
        this.mainFrame = mainFrame;
        this.contactManager = contactManager;

        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_MAIN);

        initUI();
    }

    private void initUI() {
        // 1. Header
        JPanel headerPanel = UIUtils.createHeaderPanel(
            "Emergency Services Directory",
            "Fast access to verified local Police, Hospital, Ambulance, and Fire Service contacts"
        );
        add(headerPanel, BorderLayout.NORTH);

        // 2. Central Panel
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setBackground(UIUtils.BG_MAIN);
        centerPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        // 2A. Filter Bar Card
        JPanel filterCard = UIUtils.createCardPanel();
        filterCard.setLayout(new BorderLayout());

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilters.setOpaque(false);

        leftFilters.add(new JLabel("Emergency Service:"));
        String[] emergencyCategories = {"All Emergency Services", "Police", "Hospital", "Ambulance", "Fire Service"};
        categoryCombo = new JComboBox<>(emergencyCategories);
        categoryCombo.setFont(UIUtils.FONT_REGULAR);
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.addActionListener(e -> refreshData());
        leftFilters.add(categoryCombo);

        leftFilters.add(new JLabel("Search:"));
        searchField = new JTextField(16);
        UIUtils.styleTextField(searchField);
        searchField.setToolTipText("Search emergency contacts by name or location");
        searchField.addActionListener(e -> refreshData());
        leftFilters.add(searchField);

        JButton searchBtn = UIUtils.createStyledButton("Search", UIUtils.BLUE_PRIMARY, Color.WHITE);
        searchBtn.addActionListener(e -> refreshData());
        leftFilters.add(searchBtn);

        JButton clearBtn = UIUtils.createStyledButton("Clear", UIUtils.SLATE_SECONDARY, Color.WHITE);
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            categoryCombo.setSelectedIndex(0);
            refreshData();
        });
        leftFilters.add(clearBtn);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightActions.setOpaque(false);

        JButton addEmergencyBtn = UIUtils.createStyledButton("+ Add Emergency Contact", UIUtils.RED_DANGER, Color.WHITE);
        addEmergencyBtn.addActionListener(e -> {
            String selected = (String) categoryCombo.getSelectedItem();
            String catToPreselect = "Hospital";
            if (selected != null && !selected.contains("All")) {
                catToPreselect = selected;
            }
            mainFrame.addContactWithCategory(catToPreselect);
        });
        rightActions.add(addEmergencyBtn);

        filterCard.add(leftFilters, BorderLayout.WEST);
        filterCard.add(rightActions, BorderLayout.EAST);
        centerPanel.add(filterCard, BorderLayout.NORTH);

        // 2B. Content Switcher (Table vs Empty state)
        contentCardLayout = new CardLayout();
        tableContainerCard = new JPanel(contentCardLayout);
        tableContainerCard.setOpaque(false);

        // Subcard 1: Table Card
        JPanel tableCard = UIUtils.createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 10));

        String[] columns = {"Contact ID", "Department / Name", "Emergency Number", "Category", "Address", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        emergencyTable = new JTable(tableModel);
        UIUtils.styleTable(emergencyTable);

        emergencyTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        emergencyTable.getColumnModel().getColumn(1).setPreferredWidth(170);
        emergencyTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        emergencyTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        emergencyTable.getColumnModel().getColumn(4).setPreferredWidth(230);
        emergencyTable.getColumnModel().getColumn(5).setPreferredWidth(150);

        emergencyTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && emergencyTable.getSelectedRow() != -1) {
                    viewSelectedDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(emergencyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom status & buttons
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);

        statusLabel = new JLabel("Showing 0 emergency contacts");
        statusLabel.setFont(UIUtils.FONT_REGULAR);
        statusLabel.setForeground(UIUtils.TEXT_MUTED);
        bottomBar.add(statusLabel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton viewBtn = UIUtils.createStyledButton("View Details", UIUtils.SLATE_SECONDARY, Color.WHITE);
        viewBtn.addActionListener(e -> viewSelectedDetails());

        JButton editBtn = UIUtils.createStyledButton("Edit Contact", UIUtils.BLUE_PRIMARY, Color.WHITE);
        editBtn.addActionListener(e -> editSelectedContact());

        btnPanel.add(viewBtn);
        btnPanel.add(editBtn);

        bottomBar.add(btnPanel, BorderLayout.EAST);
        tableCard.add(bottomBar, BorderLayout.SOUTH);

        // Subcard 2: Empty State Card
        JPanel emptyCard = UIUtils.createCardPanel();
        emptyCard.setLayout(new GridBagLayout());

        JPanel emptyContent = new JPanel();
        emptyContent.setLayout(new BoxLayout(emptyContent, BoxLayout.Y_AXIS));
        emptyContent.setOpaque(false);

        JLabel emptyIcon = new JLabel("[ ! ]", SwingConstants.CENTER);
        emptyIcon.setFont(new Font("Segoe UI", Font.BOLD, 48));
        emptyIcon.setForeground(UIUtils.AMBER_WARNING);
        emptyIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel emptyTitle = new JLabel("No Emergency Contacts Found", SwingConstants.CENTER);
        emptyTitle.setFont(UIUtils.FONT_HEADER_TITLE);
        emptyTitle.setForeground(UIUtils.TEXT_DARK);
        emptyTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel emptyDesc = new JLabel("<html><center>No emergency services are registered under this filter yet.<br>Click below to add verified local police, hospital, ambulance, or fire service numbers.</center></html>", SwingConstants.CENTER);
        emptyDesc.setFont(UIUtils.FONT_REGULAR);
        emptyDesc.setForeground(UIUtils.TEXT_MUTED);
        emptyDesc.setAlignmentX(CENTER_ALIGNMENT);

        JButton emptyAddBtn = UIUtils.createStyledButton("+ Add Emergency Service Contact", UIUtils.RED_DANGER, Color.WHITE);
        emptyAddBtn.setAlignmentX(CENTER_ALIGNMENT);
        emptyAddBtn.addActionListener(e -> {
            String selected = (String) categoryCombo.getSelectedItem();
            String catToPreselect = "Hospital";
            if (selected != null && !selected.contains("All")) {
                catToPreselect = selected;
            }
            mainFrame.addContactWithCategory(catToPreselect);
        });

        emptyContent.add(emptyIcon);
        emptyContent.add(Box.createVerticalStrut(12));
        emptyContent.add(emptyTitle);
        emptyContent.add(Box.createVerticalStrut(8));
        emptyContent.add(emptyDesc);
        emptyContent.add(Box.createVerticalStrut(16));
        emptyContent.add(emptyAddBtn);

        emptyCard.add(emptyContent);

        tableContainerCard.add(tableCard, CARD_TABLE);
        tableContainerCard.add(emptyCard, CARD_EMPTY);

        centerPanel.add(tableContainerCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        refreshData();
    }

    /**
     * Refreshes the emergency contact list and switches to empty view if none exist.
     */
    public void refreshData() {
        String selected = (String) categoryCombo.getSelectedItem();
        String query = searchField.getText().trim().toLowerCase();

        List<Contact> allEmergency = contactManager.getEmergencyContacts();
        List<Contact> filtered = new ArrayList<>();

        boolean filterAll = (selected == null || selected.contains("All"));

        for (Contact c : allEmergency) {
            boolean matchesCat = filterAll || c.getCategory().equalsIgnoreCase(selected);
            boolean matchesQuery = query.isEmpty() ||
                                   c.getName().toLowerCase().contains(query) ||
                                   c.getPhoneNumber().toLowerCase().contains(query) ||
                                   c.getAddress().toLowerCase().contains(query);

            if (matchesCat && matchesQuery) {
                filtered.add(c);
            }
        }

        tableModel.setRowCount(0);
        for (Contact c : filtered) {
            tableModel.addRow(new Object[]{
                c.getContactId(),
                c.getName(),
                c.getPhoneNumber(),
                c.getCategory(),
                c.getAddress(),
                c.getEmail().isEmpty() ? "-" : c.getEmail()
            });
        }

        statusLabel.setText(String.format("Showing %d emergency contact(s)", filtered.size()));

        if (filtered.isEmpty()) {
            contentCardLayout.show(tableContainerCard, CARD_EMPTY);
        } else {
            contentCardLayout.show(tableContainerCard, CARD_TABLE);
        }
    }

    private Contact getSelectedContact() {
        int selectedRow = emergencyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an emergency contact from the table.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String contactId = (String) tableModel.getValueAt(selectedRow, 0);
        Contact contact = contactManager.findById(contactId);
        if (contact == null) {
            JOptionPane.showMessageDialog(this,
                "Selected contact could not be found.",
                "Not Found",
                JOptionPane.ERROR_MESSAGE);
        }
        return contact;
    }

    private void viewSelectedDetails() {
        Contact contact = getSelectedContact();
        if (contact != null) {
            mainFrame.showContactDetailsDialog(contact);
        }
    }

    private void editSelectedContact() {
        Contact contact = getSelectedContact();
        if (contact != null) {
            mainFrame.openEditContactScreen(contact);
        }
    }
}
