package emergency;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Implements the All Contacts screen.
 * Displays all contacts in a styled JTable, supporting real-time search,
 * category filtering, view details, edit, and delete operations.
 */
public class ContactListPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ContactManager contactManager;

    private DefaultTableModel tableModel;
    private JTable contactsTable;

    private JTextField searchField;
    private JComboBox<String> categoryFilterCombo;
    private JLabel statusLabel;

    // Currently displayed contacts in the table
    private List<Contact> displayedContacts = new ArrayList<>();

    public ContactListPanel(MainFrame mainFrame, ContactManager contactManager) {
        this.mainFrame = mainFrame;
        this.contactManager = contactManager;

        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_MAIN);

        initUI();
    }

    private void initUI() {
        // 1. Header
        JPanel headerPanel = UIUtils.createHeaderPanel(
            "All Contacts Directory",
            "Browse, search, filter, edit, and manage emergency and personal contacts"
        );
        add(headerPanel, BorderLayout.NORTH);

        // 2. Central Content Panel
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setBackground(UIUtils.BG_MAIN);
        centerPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        // 2A. Filter and Search Toolbar Card
        JPanel toolbarCard = UIUtils.createCardPanel();
        toolbarCard.setLayout(new BorderLayout());

        JPanel searchSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchSection.setOpaque(false);

        searchSection.add(new JLabel("Search:"));
        searchField = new JTextField(18);
        UIUtils.styleTextField(searchField);
        searchField.setToolTipText("Search by name, phone, or address (press Enter)");
        searchSection.add(searchField);

        JButton searchBtn = UIUtils.createStyledButton("Search", UIUtils.BLUE_PRIMARY, Color.WHITE);
        searchBtn.addActionListener(e -> applyFilters());
        searchField.addActionListener(e -> applyFilters());
        searchSection.add(searchBtn);

        JButton clearSearchBtn = UIUtils.createStyledButton("Clear", UIUtils.SLATE_SECONDARY, Color.WHITE);
        clearSearchBtn.addActionListener(e -> {
            searchField.setText("");
            categoryFilterCombo.setSelectedIndex(0);
            applyFilters();
        });
        searchSection.add(clearSearchBtn);

        // Category dropdown on right
        JPanel filterSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterSection.setOpaque(false);

        filterSection.add(new JLabel("Category:"));
        List<String> categories = new ArrayList<>();
        categories.add("All Categories");
        for (String cat : Contact.CATEGORIES) {
            categories.add(cat);
        }
        categoryFilterCombo = new JComboBox<>(categories.toArray(new String[0]));
        categoryFilterCombo.setFont(UIUtils.FONT_REGULAR);
        categoryFilterCombo.setBackground(Color.WHITE);
        categoryFilterCombo.addActionListener(e -> applyFilters());
        filterSection.add(categoryFilterCombo);

        JButton refreshBtn = UIUtils.createStyledButton("Refresh", new Color(14, 116, 144), Color.WHITE);
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            categoryFilterCombo.setSelectedIndex(0);
            refreshTable();
        });
        filterSection.add(refreshBtn);

        toolbarCard.add(searchSection, BorderLayout.WEST);
        toolbarCard.add(filterSection, BorderLayout.EAST);
        centerPanel.add(toolbarCard, BorderLayout.NORTH);

        // 2B. JTable Card
        JPanel tableCard = UIUtils.createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 10));

        String[] columns = {"Contact ID", "Name", "Phone Number", "Category", "Address", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        contactsTable = new JTable(tableModel);
        UIUtils.styleTable(contactsTable);

        // Adjust column widths
        contactsTable.getColumnModel().getColumn(0).setPreferredWidth(90);  // ID
        contactsTable.getColumnModel().getColumn(1).setPreferredWidth(160); // Name
        contactsTable.getColumnModel().getColumn(2).setPreferredWidth(130); // Phone
        contactsTable.getColumnModel().getColumn(3).setPreferredWidth(110); // Category
        contactsTable.getColumnModel().getColumn(4).setPreferredWidth(220); // Address
        contactsTable.getColumnModel().getColumn(5).setPreferredWidth(160); // Email

        // Double-click row handler
        contactsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && contactsTable.getSelectedRow() != -1) {
                    viewDetailsAction();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(contactsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // 2C. Bottom Action Buttons Bar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);

        statusLabel = new JLabel("Showing 0 contacts");
        statusLabel.setFont(UIUtils.FONT_REGULAR);
        statusLabel.setForeground(UIUtils.TEXT_MUTED);
        bottomBar.add(statusLabel, BorderLayout.WEST);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        JButton viewDetailsBtn = UIUtils.createStyledButton("View Details", UIUtils.SLATE_SECONDARY, Color.WHITE);
        viewDetailsBtn.addActionListener(e -> viewDetailsAction());

        JButton editBtn = UIUtils.createStyledButton("Edit Contact", UIUtils.BLUE_PRIMARY, Color.WHITE);
        editBtn.addActionListener(e -> editContactAction());

        JButton deleteBtn = UIUtils.createStyledButton("Delete Contact", UIUtils.RED_DANGER, Color.WHITE);
        deleteBtn.addActionListener(e -> deleteContactAction());

        actionsPanel.add(viewDetailsBtn);
        actionsPanel.add(editBtn);
        actionsPanel.add(deleteBtn);

        bottomBar.add(actionsPanel, BorderLayout.EAST);
        tableCard.add(bottomBar, BorderLayout.SOUTH);

        centerPanel.add(tableCard, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Populate table
        refreshTable();
    }

    /**
     * Refreshes the table with all contacts and updates status.
     */
    public void refreshTable() {
        applyFilters();
    }

    /**
     * Sets search query from an external trigger (like Dashboard) and refreshes table.
     *
     * @param query Search query text.
     */
    public void setSearchQuery(String query) {
        searchField.setText(query != null ? query : "");
        categoryFilterCombo.setSelectedIndex(0);
        applyFilters();
    }

    /**
     * Applies both search query and category filters together.
     */
    public void applyFilters() {
        String query = searchField.getText().trim();
        String selectedCategory = (String) categoryFilterCombo.getSelectedItem();

        // 1. Get search results
        List<Contact> searched = contactManager.searchContacts(query);

        // 2. Filter by category
        displayedContacts = new ArrayList<>();
        boolean filterAll = (selectedCategory == null ||
                             selectedCategory.equalsIgnoreCase("All") ||
                             selectedCategory.equalsIgnoreCase("All Categories"));

        for (Contact c : searched) {
            if (filterAll || c.getCategory().equalsIgnoreCase(selectedCategory)) {
                displayedContacts.add(c);
            }
        }

        // 3. Populate table
        tableModel.setRowCount(0);
        for (Contact c : displayedContacts) {
            tableModel.addRow(new Object[]{
                c.getContactId(),
                c.getName(),
                c.getPhoneNumber(),
                c.getCategory(),
                c.getAddress(),
                c.getEmail().isEmpty() ? "-" : c.getEmail()
            });
        }

        // 4. Update status label
        int total = contactManager.getTotalContacts();
        int showing = displayedContacts.size();
        statusLabel.setText(String.format("Showing %d of %d contacts", showing, total));
    }

    /**
     * Retrieves the Contact currently selected in the table.
     * Returns null and shows warning dialog if no row is selected.
     */
    private Contact getSelectedContact() {
        int selectedRow = contactsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a contact from the table first.",
                "No Contact Selected",
                JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String contactId = (String) tableModel.getValueAt(selectedRow, 0);
        Contact contact = contactManager.findById(contactId);
        if (contact == null) {
            JOptionPane.showMessageDialog(this,
                "The selected contact could not be found. It may have been modified or deleted.",
                "Contact Not Found",
                JOptionPane.ERROR_MESSAGE);
        }
        return contact;
    }

    private void viewDetailsAction() {
        Contact contact = getSelectedContact();
        if (contact != null) {
            mainFrame.showContactDetailsDialog(contact);
        }
    }

    private void editContactAction() {
        Contact contact = getSelectedContact();
        if (contact != null) {
            mainFrame.openEditContactScreen(contact);
        }
    }

    private void deleteContactAction() {
        Contact contact = getSelectedContact();
        if (contact == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this contact?\n\n" +
            "ID: " + contact.getContactId() + "\n" +
            "Name: " + contact.getName() + "\n" +
            "Phone: " + contact.getPhoneNumber() + "\n" +
            "Category: " + contact.getCategory() + "\n\n" +
            "This action cannot be undone.",
            "Confirm Delete Contact",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = mainFrame.handleDeleteContact(contact.getContactId());
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Contact '" + contact.getName() + "' (" + contact.getContactId() + ") was deleted successfully.",
                    "Contact Deleted",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
        }
    }
}

