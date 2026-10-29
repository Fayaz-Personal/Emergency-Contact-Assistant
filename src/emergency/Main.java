package emergency;

import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application entry point for Emergency Contact Assistant.
 * Initializes persistent FileHandler, loads existing contacts into ContactManager,
 * configures the Swing Look & Feel, and launches MainFrame on the Event Dispatch Thread (EDT).
 */
public class Main {

    public static void main(String[] args) {
        // Configure System Look & Feel for native window styling and font anti-aliasing
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Notice: Could not set System Look and Feel. Defaulting to standard Swing UI.");
        }

        // Initialize file handler
        FileHandler fileHandler = new FileHandler("data/contacts.txt");

        // Load saved contacts safely
        List<Contact> initialContacts = null;
        try {
            initialContacts = fileHandler.loadContacts();
            System.out.println("[Main] Successfully loaded " + initialContacts.size() + " contact(s) from " + fileHandler.getFilePath());
        } catch (IOException e) {
            System.err.println("[Main] Warning: Could not read existing contacts file: " + e.getMessage());
            // Safe fallback: start with empty list without wiping file
            initialContacts = null;
        }

        final List<Contact> loadedContacts = initialContacts;

        // Launch GUI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                ContactManager contactManager = new ContactManager();
                if (loadedContacts != null) {
                    contactManager.setContacts(loadedContacts);
                }

                MainFrame mainFrame = new MainFrame(contactManager, fileHandler);
                mainFrame.setVisible(true);

                if (loadedContacts == null && new java.io.File("data/contacts.txt").exists()) {
                    JOptionPane.showMessageDialog(mainFrame,
                        "Warning: Could not read existing contacts from 'data/contacts.txt'.\n" +
                        "An empty list was initialized to prevent data loss.",
                        "Storage Warning",
                        JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "An unexpected error occurred while starting the application:\n" + ex.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

