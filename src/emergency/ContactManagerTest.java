package emergency;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Automated test runner for Contact, ContactManager, and FileHandler.
 * Runs standard unit and integration tests without requiring any external testing libraries
 * like JUnit, making it 100% portable for college Java micro projects and Eclipse.
 */
public class ContactManagerTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("======================================================");
        System.out.println("   EMERGENCY CONTACT ASSISTANT - UNIT & INTEGRATION TESTS");
        System.out.println("======================================================\n");

        runAllTests();

        System.out.println("\n======================================================");
        System.out.printf("   TEST RESULTS SUMMARY: %d/%d PASSED (%d FAILED)\n",
                testsPassed, testsRun, testsFailed);
        System.out.println("======================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void runAllTests() {
        testContactEntity();
        testAddContact();
        testDuplicateIdPrevention();
        testSearchContacts();
        testFilterByCategory();
        testEmergencyAndPersonalCounts();
        testUpdateContact();
        testDeleteContact();
        testIdGeneration();
        testFilePersistence();
        testEmptyFileHandling();
        testMalformedFileResilience();
    }

    private static void testContactEntity() {
        startTest("Contact Entity Creation & Encapsulation");
        Contact c = new Contact("CNT-1001", "Dr. Sharma", "+91 9876543210", "Hospital", "City Hospital", "doc@hospital.org", "Available 24/7");

        assertTrue("ID match", "CNT-1001".equals(c.getContactId()));
        assertTrue("Name match", "Dr. Sharma".equals(c.getName()));
        assertTrue("Phone match", "+91 9876543210".equals(c.getPhoneNumber()));
        assertTrue("Category match", "Hospital".equals(c.getCategory()));
        assertTrue("Emergency service check", c.isEmergencyService());
        assertTrue("Description not null", c.getFormattedDescription().contains("CNT-1001"));
        endTest();
    }

    private static void testAddContact() {
        startTest("ContactManager Add Contact");
        ContactManager manager = new ContactManager();
        Contact c = new Contact("CNT-1001", "City Police Station", "100", "Police", "Downtown", "police@city.gov", "Helpline");
        boolean added = manager.addContact(c);

        assertTrue("Contact added successfully", added);
        assertTrue("Total count is 1", manager.getTotalContacts() == 1);
        assertTrue("Find by ID matches", manager.findById("CNT-1001") != null);
        endTest();
    }

    private static void testDuplicateIdPrevention() {
        startTest("Prevent Duplicate Contact IDs");
        ContactManager manager = new ContactManager();
        Contact c1 = new Contact("CNT-1001", "Ambulance Desk", "108", "Ambulance", "Central Hospital", "", "");
        manager.addContact(c1);

        boolean duplicateRejected = false;
        try {
            Contact c2 = new Contact("CNT-1001", "Different Contact", "999", "Police", "Station", "", "");
            manager.addContact(c2);
        } catch (IllegalArgumentException e) {
            duplicateRejected = true;
        }

        assertTrue("Duplicate ID threw IllegalArgumentException", duplicateRejected);
        assertTrue("Total contacts remains 1", manager.getTotalContacts() == 1);
        endTest();
    }

    private static void testSearchContacts() {
        startTest("Case-Insensitive Search by Name and Phone");
        ContactManager manager = new ContactManager();
        manager.addContact(new Contact("CNT-1001", "Rahul Sharma", "9876543210", "Hospital", "Coimbatore", "", ""));
        manager.addContact(new Contact("CNT-1002", "Arun Kumar", "9876501234", "Family", "Coimbatore", "", ""));
        manager.addContact(new Contact("CNT-1003", "Priya Nair", "9876512345", "Police", "Coimbatore", "", ""));

        // Case-insensitive name search
        List<Contact> results = manager.searchContacts("rahul");
        assertTrue("Search 'rahul' finds 1 contact", results.size() == 1 && results.get(0).getName().equals("Rahul Sharma"));

        // Search by phone digits
        results = manager.searchContacts("501234");
        assertTrue("Search '501234' finds Arun", results.size() == 1 && results.get(0).getName().equals("Arun Kumar"));

        // Search nonexistent
        results = manager.searchContacts("NonExistentQueryXYZ");
        assertTrue("Search nonexistent query returns empty list", results.isEmpty());
        endTest();
    }

    private static void testFilterByCategory() {
        startTest("Filter Contacts by Category");
        ContactManager manager = new ContactManager();
        manager.addContact(new Contact("CNT-1001", "Hospital Desk", "102", "Hospital", "Center", "", ""));
        manager.addContact(new Contact("CNT-1002", "Police Station", "100", "Police", "North", "", ""));
        manager.addContact(new Contact("CNT-1003", "Brother", "9876511111", "Family", "Home", "", ""));

        List<Contact> policeContacts = manager.filterByCategory("Police");
        assertTrue("Police filter returned 1 item", policeContacts.size() == 1 && policeContacts.get(0).getCategory().equals("Police"));

        List<Contact> allContacts = manager.filterByCategory("All Categories");
        assertTrue("All Categories returned 3 items", allContacts.size() == 3);
        endTest();
    }

    private static void testEmergencyAndPersonalCounts() {
        startTest("Emergency vs Personal Contact Counting");
        ContactManager manager = new ContactManager();
        manager.addContact(new Contact("CNT-1001", "Police", "100", "Police", "HQ", "", ""));
        manager.addContact(new Contact("CNT-1002", "Hospital", "108", "Hospital", "Ward", "", ""));
        manager.addContact(new Contact("CNT-1003", "Friend Alex", "9876522222", "Friends", "Apt 4", "", ""));

        assertTrue("Emergency count is 2", manager.getEmergencyContactCount() == 2);
        assertTrue("Personal count is 1", manager.getPersonalContactCount() == 1);
        assertTrue("Total count is 3", manager.getTotalContacts() == 3);
        endTest();
    }

    private static void testUpdateContact() {
        startTest("Update Existing Contact");
        ContactManager manager = new ContactManager();
        Contact original = new Contact("CNT-1001", "Old Name", "9876500000", "Family", "Old Address", "", "Old Notes");
        manager.addContact(original);

        Contact updated = new Contact("CNT-1001", "New Name", "9876599999", "Friends", "New Address", "new@test.com", "Updated Notes");
        boolean updatedResult = manager.updateContact("CNT-1001", updated);

        assertTrue("Update operation succeeded", updatedResult);
        Contact fetched = manager.findById("CNT-1001");
        assertTrue("Name was updated", "New Name".equals(fetched.getName()));
        assertTrue("Phone was updated", "9876599999".equals(fetched.getPhoneNumber()));
        assertTrue("Category was updated", "Friends".equals(fetched.getCategory()));
        assertTrue("Address was updated", "New Address".equals(fetched.getAddress()));
        endTest();
    }

    private static void testDeleteContact() {
        startTest("Delete Contact");
        ContactManager manager = new ContactManager();
        manager.addContact(new Contact("CNT-1001", "Delete Me", "9876500000", "Other", "Addr", "", ""));
        assertTrue("Manager has 1 contact", manager.getTotalContacts() == 1);

        boolean deleted = manager.deleteContact("CNT-1001");
        assertTrue("Delete returned true", deleted);
        assertTrue("Manager has 0 contacts", manager.getTotalContacts() == 0);
        assertTrue("Contact not found after delete", manager.findById("CNT-1001") == null);

        boolean deleteNonexistent = manager.deleteContact("CNT-9999");
        assertTrue("Delete non-existent returns false", !deleteNonexistent);
        endTest();
    }

    private static void testIdGeneration() {
        startTest("Sequential Unique Contact ID Generation");
        ContactManager manager = new ContactManager();
        String id1 = manager.generateNextId();
        assertTrue("First generated ID is CNT-1001", "CNT-1001".equals(id1));

        manager.addContact(new Contact(id1, "Person 1", "9876511111", "Family", "Addr", "", ""));
        String id2 = manager.generateNextId();
        assertTrue("Second generated ID is CNT-1002", "CNT-1002".equals(id2));

        manager.addContact(new Contact("CNT-1050", "High ID Person", "9876522222", "Other", "Addr", "", ""));
        String id3 = manager.generateNextId();
        assertTrue("Generated ID skips to next after highest ID (CNT-1051)", "CNT-1051".equals(id3));
        endTest();
    }

    private static void testFilePersistence() {
        startTest("FileHandler UTF-8 Save, Special Characters & Reload");
        String testFile = "data/test_contacts.txt";
        FileHandler fh = new FileHandler(testFile);

        ContactManager originalManager = new ContactManager();
        // Contact with special characters, commas, and multi-line notes
        Contact c1 = new Contact("CNT-1001", "Dr. A. P. J. Abdul Kalam", "+91 9876543210", "Hospital",
                "123/4, Cross Road, Coimbatore - 641001", "hospital@coimbatore.org",
                "Line 1: Special Note with commas, quotes \"test\"\nLine 2: 24-hr triage");
        originalManager.addContact(c1);

        try {
            fh.saveContacts(originalManager.getAllContacts());

            List<Contact> loaded = fh.loadContacts();
            assertTrue("Loaded exactly 1 contact", loaded.size() == 1);

            Contact loadedContact = loaded.get(0);
            assertTrue("ID matches after reload", "CNT-1001".equals(loadedContact.getContactId()));
            assertTrue("Name matches after reload", "Dr. A. P. J. Abdul Kalam".equals(loadedContact.getName()));
            assertTrue("Address matches after reload", "123/4, Cross Road, Coimbatore - 641001".equals(loadedContact.getAddress()));
            assertTrue("Multi-line note preserved after reload", loadedContact.getNotes().contains("Line 1:") && loadedContact.getNotes().contains("Line 2:"));

        } catch (IOException e) {
            assertTrue("Save/Load threw exception: " + e.getMessage(), false);
        } finally {
            // Clean up test file
            new File(testFile).delete();
            new File(testFile + ".tmp").delete();
        }
        endTest();
    }

    private static void testEmptyFileHandling() {
        startTest("FileHandler Empty File Handling");
        String testFile = "data/empty_test.txt";
        FileHandler fh = new FileHandler(testFile);

        try {
            // Test non-existent file
            new File(testFile).delete();
            List<Contact> nonExistentResult = fh.loadContacts();
            assertTrue("Non-existent file returns empty list without error", nonExistentResult.isEmpty());

            // Test 0-byte existing file
            new File(testFile).createNewFile();
            List<Contact> emptyResult = fh.loadContacts();
            assertTrue("0-byte file returns empty list without error", emptyResult.isEmpty());
        } catch (IOException e) {
            assertTrue("Empty file handling threw exception: " + e.getMessage(), false);
        } finally {
            new File(testFile).delete();
        }
        endTest();
    }

    private static void testMalformedFileResilience() {
        startTest("FileHandler Malformed Record Resilience");
        String testFile = "data/corrupt_test.txt";
        FileHandler fh = new FileHandler(testFile);

        try {
            // Write a file with 1 corrupt record (missing ID/name) and 1 valid record
            try (FileWriter writer = new FileWriter(testFile)) {
                writer.write("RECORD_START\n");
                writer.write("random_garbage=invalid\n");
                writer.write("RECORD_END\n");
                writer.write("RECORD_START\n");
                writer.write("id=CNT-2001\n");
                writer.write("name=Valid User\n");
                writer.write("phone=9876543210\n");
                writer.write("category=Family\n");
                writer.write("address=Test Town\n");
                writer.write("RECORD_END\n");
            }

            List<Contact> loaded = fh.loadContacts();
            assertTrue("Loaded 1 valid contact despite malformed preceding record", loaded.size() == 1);
            assertTrue("Valid contact ID is CNT-2001", "CNT-2001".equals(loaded.get(0).getContactId()));
            assertTrue("Valid contact Name is Valid User", "Valid User".equals(loaded.get(0).getName()));

        } catch (IOException e) {
            assertTrue("Malformed file handling threw exception: " + e.getMessage(), false);
        } finally {
            new File(testFile).delete();
        }
        endTest();
    }

    // --- Test assertion helpers ---

    private static void startTest(String testName) {
        testsRun++;
        System.out.printf("Test %02d: %-52s ", testsRun, testName);
    }

    private static void assertTrue(String message, boolean condition) {
        if (!condition) {
            System.out.println("[FAILED]");
            System.err.println("   Assertion failed: " + message);
            testsFailed++;
            throw new AssertionError(message);
        }
    }

    private static void endTest() {
        testsPassed++;
        System.out.println("[PASSED]");
    }
}

