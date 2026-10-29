package emergency;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

/**
 * Headless-compatible GUI Smoke Test verifying that MainFrame, DashboardPanel,
 * ContactListPanel, ContactFormPanel, EmergencyDirectoryPanel, and AboutPanel
 * initialize, bind listeners, load sample data, and dispose cleanly without throwing exceptions.
 */
public class GUISmokeTest {

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        System.out.println("Starting GUI Smoke Test...");

        // Ensure clean test file
        String testDataFile = "data/smoke_test_contacts.txt";
        new File(testDataFile).delete();

        FileHandler fileHandler = new FileHandler(testDataFile);
        ContactManager contactManager = new ContactManager();

        SwingUtilities.invokeAndWait(() -> {
            try {
                System.out.println("1. Initializing MainFrame...");
                MainFrame frame = new MainFrame(contactManager, fileHandler);

                System.out.println("2. Testing screen switching...");
                frame.showScreen(MainFrame.SCREEN_DASHBOARD);
                frame.showScreen(MainFrame.SCREEN_ALL_CONTACTS);
                frame.showScreen(MainFrame.SCREEN_ADD_CONTACT);
                frame.showScreen(MainFrame.SCREEN_EMERGENCY_DIRECTORY);
                frame.showScreen(MainFrame.SCREEN_ABOUT);

                System.out.println("3. Testing adding contact via MainFrame...");
                Contact testContact = new Contact("CNT-9001", "Smoke Test Police", "100", "Police", "City Center", "police@city.gov", "Test notes");
                boolean added = frame.handleAddContact(testContact);
                if (!added) {
                    throw new RuntimeException("Failed to add contact in GUI test");
                }

                System.out.println("4. Testing update contact via MainFrame...");
                Contact updatedContact = new Contact("CNT-9001", "Smoke Test Police Updated", "101", "Police", "City Center Updated", "police@city.gov", "Test notes updated");
                boolean updated = frame.handleUpdateContact("CNT-9001", updatedContact);
                if (!updated) {
                    throw new RuntimeException("Failed to update contact in GUI test");
                }

                System.out.println("5. Testing delete contact via MainFrame...");
                boolean deleted = frame.handleDeleteContact("CNT-9001");
                if (!deleted) {
                    throw new RuntimeException("Failed to delete contact in GUI test");
                }

                System.out.println("6. Disposing MainFrame cleanly...");
                frame.dispose();

                System.out.println("GUI Smoke Test PASSED successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            } finally {
                new File(testDataFile).delete();
                new File(testDataFile + ".tmp").delete();
            }
        });

        System.exit(0);
    }
}

