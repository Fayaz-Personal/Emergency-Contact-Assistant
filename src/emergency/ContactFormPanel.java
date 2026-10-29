package emergency;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Reusable form panel for both adding new contacts and editing existing contacts.
 * Enforces strict input validation for names, phone numbers, emails, and categories.
 */
public class ContactFormPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ContactManager contactManager;

    // Mode tracking
    private boolean isEditMode = false;
    private String originalContactId = "";

    // Form Components
    private JLabel headerTitleLabel;
    private JLabel headerSubtitleLabel;

    private JTextField idField;
    private JTextField nameField;
    private JTextField phoneField;
    private JComboBox<String> categoryCombo;
    private JTextArea addressArea;
    private JTextField emailField;
    private JTextArea notesArea;

    private JButton saveButton;
    private JButton clearButton;
    private JButton cancelButton;

    // Validation patterns
    // Phone allows leading +, spaces, dashes, parentheses, 7-20 chars total
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s\\-()]{7,20}$");
    // Standard email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public ContactFormPanel(MainFrame mainFrame, ContactManager contactManager) {
        this.mainFrame = mainFrame;
        this.contactManager = contactManager;

        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_MAIN);

        initUI();
    }

    private void initUI() {
        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIUtils.CARD_BG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER_COLOR),
            new EmptyBorder(18, 24, 18, 24)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        headerTitleLabel = new JLabel("Add New Emergency Contact");
        headerTitleLabel.setFont(UIUtils.FONT_HEADER_TITLE);
        headerTitleLabel.setForeground(UIUtils.TEXT_DARK);

        headerSubtitleLabel = new JLabel("Enter details below. Contact ID will be generated automatically.");
        headerSubtitleLabel.setFont(UIUtils.FONT_HEADER_SUB);
        headerSubtitleLabel.setForeground(UIUtils.TEXT_MUTED);

        textPanel.add(headerTitleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(headerSubtitleLabel);

        headerPanel.add(textPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Central Form Card
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        centerWrapper.setOpaque(false);

        JPanel formCard = UIUtils.createCardPanel();
        formCard.setPreferredSize(new Dimension(720, 520));
        formCard.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Row 0: Contact ID (Read-only)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        JLabel idLabel = new JLabel("Contact ID:");
        idLabel.setFont(UIUtils.FONT_BOLD);
        formCard.add(idLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        idField = new JTextField();
        idField.setEditable(false);
        idField.setBackground(new Color(241, 245, 249));
        idField.setFont(UIUtils.FONT_BOLD);
        idField.setForeground(UIUtils.TEXT_MUTED);
        UIUtils.styleTextField(idField);
        formCard.add(idField, gbc);

        // Row 1: Full Name *
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        JLabel nameLabel = new JLabel("Full Name *:");
        nameLabel.setFont(UIUtils.FONT_BOLD);
        formCard.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        nameField = new JTextField();
        UIUtils.styleTextField(nameField);
        nameField.setToolTipText("Enter full person or emergency department name");
        formCard.add(nameField, gbc);

        // Row 2: Phone Number *
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        JLabel phoneLabel = new JLabel("Phone Number *:");
        phoneLabel.setFont(UIUtils.FONT_BOLD);
        formCard.add(phoneLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        phoneField = new JTextField();
        UIUtils.styleTextField(phoneField);
        phoneField.setToolTipText("Supports leading +, spaces, dashes e.g. +91 98765 43210");
        formCard.add(phoneField, gbc);

        // Row 3: Category *
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        JLabel catLabel = new JLabel("Category *:");
        catLabel.setFont(UIUtils.FONT_BOLD);
        formCard.add(catLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        categoryCombo = new JComboBox<>(Contact.CATEGORIES);
        categoryCombo.setFont(UIUtils.FONT_REGULAR);
        categoryCombo.setBackground(Color.WHITE);
        formCard.add(categoryCombo, gbc);

        // Row 4: Address *
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        JLabel addrLabel = new JLabel("Address *:");
        addrLabel.setFont(UIUtils.FONT_BOLD);
        formCard.add(addrLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        addressArea = new JTextArea(3, 20);
        addressArea.setFont(UIUtils.FONT_REGULAR);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addrScroll = new JScrollPane(addressArea);
        addrScroll.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        formCard.add(addrScroll, gbc);

        // Row 5: Email (Optional)
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        JLabel emailLabel = new JLabel("Email (Optional):");
        emailLabel.setFont(UIUtils.FONT_REGULAR);
        formCard.add(emailLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        emailField = new JTextField();
        UIUtils.styleTextField(emailField);
        emailField.setToolTipText("Valid email address e.g. contact@example.com");
        formCard.add(emailField, gbc);

        // Row 6: Notes (Optional)
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        JLabel notesLabel = new JLabel("Notes (Optional):");
        notesLabel.setFont(UIUtils.FONT_REGULAR);
        formCard.add(notesLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        notesArea = new JTextArea(3, 20);
        notesArea.setFont(UIUtils.FONT_REGULAR);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        formCard.add(notesScroll, gbc);

        // Row 7: Buttons panel
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 12, 8, 12);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);

        cancelButton = UIUtils.createStyledButton("Cancel", UIUtils.SLATE_SECONDARY, Color.WHITE);
        cancelButton.addActionListener(e -> cancelAction());

        clearButton = UIUtils.createStyledButton("Clear Form", new Color(148, 163, 184), UIUtils.TEXT_DARK);
        clearButton.addActionListener(e -> clearForm());

        saveButton = UIUtils.createStyledButton("Save Contact", UIUtils.BLUE_PRIMARY, Color.WHITE);
        saveButton.addActionListener(e -> submitForm());

        buttonPanel.add(cancelButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);

        formCard.add(buttonPanel, gbc);

        centerWrapper.add(formCard);
        JScrollPane scrollForm = new JScrollPane(centerWrapper);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        add(scrollForm, BorderLayout.CENTER);

        // Prepare initial Add mode
        setAddMode();
    }

    /**
     * Configures the form for adding a new contact.
     */
    public void setAddMode() {
        isEditMode = false;
        originalContactId = "";
        headerTitleLabel.setText("Add New Emergency Contact");
        headerSubtitleLabel.setText("Fill in the fields below. A unique Contact ID is generated automatically.");
        saveButton.setText("Save Contact");
        clearButton.setEnabled(true);
        clearButton.setVisible(true);

        clearForm();
        idField.setText(contactManager.generateNextId());
    }

    /**
     * Configures the form for adding a contact with a specific category pre-selected.
     *
     * @param category Pre-selected category.
     */
    public void setAddModeWithCategory(String category) {
        setAddMode();
        if (category != null) {
            categoryCombo.setSelectedItem(category);
        }
    }

    /**
     * Configures the form for editing an existing contact.
     *
     * @param contact Contact to be edited.
     */
    public void setEditMode(Contact contact) {
        if (contact == null) return;
        isEditMode = true;
        originalContactId = contact.getContactId();

        headerTitleLabel.setText("Edit Contact (" + contact.getContactId() + ")");
        headerSubtitleLabel.setText("Update contact information. ID is preserved.");
        saveButton.setText("Update Contact");
        clearButton.setEnabled(false);
        clearButton.setVisible(false);

        idField.setText(contact.getContactId());
        nameField.setText(contact.getName());
        phoneField.setText(contact.getPhoneNumber());
        categoryCombo.setSelectedItem(contact.getCategory());
        addressArea.setText(contact.getAddress());
        emailField.setText(contact.getEmail());
        notesArea.setText(contact.getNotes());
    }

    /**
     * Clears all editable fields in the form.
     */
    public void clearForm() {
        if (!isEditMode) {
            idField.setText(contactManager.generateNextId());
        }
        nameField.setText("");
        phoneField.setText("");
        categoryCombo.setSelectedIndex(0);
        addressArea.setText("");
        emailField.setText("");
        notesArea.setText("");
        nameField.requestFocus();
    }

    private void cancelAction() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel? Any unsaved input will be discarded.",
            "Confirm Cancel",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            clearForm();
            mainFrame.showScreen(MainFrame.SCREEN_ALL_CONTACTS);
        }
    }

    /**
     * Validates input fields and saves or updates the contact.
     */
    private void submitForm() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String address = addressArea.getText().trim();
        String email = emailField.getText().trim();
        String notes = notesArea.getText().trim();

        // 1. Validation: Name
        if (name.isEmpty()) {
            showValidationError("Full Name cannot be empty.", nameField);
            return;
        }
        if (name.length() < 2) {
            showValidationError("Full Name must be at least 2 characters.", nameField);
            return;
        }

        // 2. Validation: Phone
        if (phone.isEmpty()) {
            showValidationError("Phone Number cannot be empty.", phoneField);
            return;
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            showValidationError("Invalid phone number format.\nUse digits with optional '+' prefix, spaces, dashes or parentheses.\nExample: +91 98765 43210 or 9876543210", phoneField);
            return;
        }
        // Count actual numeric digits to ensure plausible number (7 to 15 digits)
        long digitCount = phone.chars().filter(Character::isDigit).count();
        if (digitCount < 7 || digitCount > 15) {
            showValidationError("Phone number must contain between 7 and 15 digits.", phoneField);
            return;
        }

        // 3. Validation: Category
        if (category == null || category.trim().isEmpty()) {
            showValidationError("Please select a valid category.", categoryCombo);
            return;
        }

        // 4. Validation: Address
        if (address.isEmpty()) {
            showValidationError("Address cannot be empty.", addressArea);
            return;
        }

        // 5. Validation: Email (Optional, but must be valid if supplied)
        if (!email.isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                showValidationError("Invalid email address format.\nExample: contact@example.com", emailField);
                return;
            }
        }

        // Build contact object
        Contact contact = new Contact(id, name, phone, category, address, email, notes);

        if (isEditMode) {
            // Update existing contact
            boolean success = mainFrame.handleUpdateContact(originalContactId, contact);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Contact '" + name + "' (" + id + ") was updated successfully!",
                    "Contact Updated",
                    JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showScreen(MainFrame.SCREEN_ALL_CONTACTS);
            }
        } else {
            // Add new contact
            boolean success = mainFrame.handleAddContact(contact);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Contact '" + name + "' (" + id + ") was saved successfully!",
                    "Contact Saved",
                    JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                mainFrame.showScreen(MainFrame.SCREEN_ALL_CONTACTS);
            }
        }
    }

    private void showValidationError(String message, javax.swing.JComponent targetComponent) {
        JOptionPane.showMessageDialog(this,
            message,
            "Input Validation Error",
            JOptionPane.WARNING_MESSAGE);
        if (targetComponent != null) {
            targetComponent.requestFocus();
        }
    }
}

