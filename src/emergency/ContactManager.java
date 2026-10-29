package emergency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the collection of contacts using an ArrayList.
 * Implements core business logic: adding, searching, filtering, updating,
 * deleting, ID generation, and category counters.
 * Separates business logic strictly from GUI rendering.
 */
public class ContactManager {

    private final List<Contact> contacts;

    /**
     * Constructs a new ContactManager with an empty contacts list.
     */
    public ContactManager() {
        this.contacts = new ArrayList<>();
    }

    /**
     * Constructs a ContactManager initialized with an existing list of contacts.
     *
     * @param initialContacts List of contacts to initialize with.
     */
    public ContactManager(List<Contact> initialContacts) {
        this.contacts = new ArrayList<>();
        if (initialContacts != null) {
            for (Contact c : initialContacts) {
                if (c != null && !containsId(c.getContactId())) {
                    this.contacts.add(c);
                }
            }
        }
    }

    /**
     * Adds a new contact to the collection.
     * Validates that the contact is not null and that its ID is unique.
     *
     * @param contact The contact to add.
     * @return true if added successfully.
     * @throws IllegalArgumentException if contact is null, has an invalid/blank ID, or ID already exists.
     */
    public synchronized boolean addContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Cannot add a null contact.");
        }
        String id = contact.getContactId();
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact ID cannot be empty.");
        }
        if (containsId(id)) {
            throw new IllegalArgumentException("Contact with ID '" + id + "' already exists.");
        }
        contacts.add(contact);
        return true;
    }

    /**
     * Returns a defensive copy of all saved contacts.
     *
     * @return List of all contacts.
     */
    public synchronized List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }

    /**
     * Searches contacts matching the search query in Name, Phone Number, Category, or Address.
     * The search is case-insensitive. If the query is empty or null, all contacts are returned.
     *
     * @param query Search query string.
     * @return List of matching contacts, or empty list if no matches found.
     */
    public synchronized List<Contact> searchContacts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllContacts();
        }
        String lowerQuery = query.trim().toLowerCase();
        List<Contact> results = new ArrayList<>();
        for (Contact c : contacts) {
            if (c.getName().toLowerCase().contains(lowerQuery) ||
                c.getPhoneNumber().toLowerCase().contains(lowerQuery) ||
                c.getCategory().toLowerCase().contains(lowerQuery) ||
                c.getAddress().toLowerCase().contains(lowerQuery) ||
                c.getContactId().toLowerCase().contains(lowerQuery)) {
                results.add(c);
            }
        }
        return results;
    }

    /**
     * Filters contacts by category.
     * If category is "All" or null/empty, returns all contacts.
     *
     * @param category The category name to filter by.
     * @return List of contacts in the specified category.
     */
    public synchronized List<Contact> filterByCategory(String category) {
        if (category == null || category.trim().isEmpty() || category.equalsIgnoreCase("All") || category.equalsIgnoreCase("All Categories")) {
            return getAllContacts();
        }
        String target = category.trim().toLowerCase();
        List<Contact> results = new ArrayList<>();
        for (Contact c : contacts) {
            if (c.getCategory().trim().toLowerCase().equalsIgnoreCase(target)) {
                results.add(c);
            }
        }
        return results;
    }

    /**
     * Filters contacts to only return emergency services (Police, Hospital, Ambulance, Fire Service).
     *
     * @return List of emergency-service contacts.
     */
    public synchronized List<Contact> getEmergencyContacts() {
        List<Contact> results = new ArrayList<>();
        for (Contact c : contacts) {
            if (c.isEmergencyService()) {
                results.add(c);
            }
        }
        return results;
    }

    /**
     * Finds a contact by its unique ID.
     *
     * @param contactId Unique ID to search for.
     * @return The Contact if found, or null if not found.
     */
    public synchronized Contact findById(String contactId) {
        if (contactId == null || contactId.trim().isEmpty()) {
            return null;
        }
        String target = contactId.trim();
        for (Contact c : contacts) {
            if (c.getContactId().equalsIgnoreCase(target)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Updates an existing contact.
     *
     * @param contactId      ID of the contact to update.
     * @param updatedContact The updated contact object.
     * @return true if updated successfully, false if contact with given ID does not exist.
     * @throws IllegalArgumentException if contactId is invalid or updatedContact is null.
     */
    public synchronized boolean updateContact(String contactId, Contact updatedContact) {
        if (contactId == null || contactId.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact ID cannot be empty for update.");
        }
        if (updatedContact == null) {
            throw new IllegalArgumentException("Updated contact cannot be null.");
        }

        for (int i = 0; i < contacts.size(); i++) {
            Contact current = contacts.get(i);
            if (current.getContactId().equalsIgnoreCase(contactId.trim())) {
                // Ensure ID is preserved
                updatedContact.setContactId(current.getContactId());
                contacts.set(i, updatedContact);
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes a contact by ID.
     *
     * @param contactId ID of the contact to delete.
     * @return true if removed, false if not found.
     */
    public synchronized boolean deleteContact(String contactId) {
        if (contactId == null || contactId.trim().isEmpty()) {
            return false;
        }
        String target = contactId.trim();
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getContactId().equalsIgnoreCase(target)) {
                contacts.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a contact ID already exists.
     *
     * @param contactId ID to check.
     * @return true if ID exists, false otherwise.
     */
    public synchronized boolean containsId(String contactId) {
        if (contactId == null || contactId.trim().isEmpty()) {
            return false;
        }
        return findById(contactId) != null;
    }

    /**
     * Returns total count of contacts.
     *
     * @return total count.
     */
    public synchronized int getTotalContacts() {
        return contacts.size();
    }

    /**
     * Returns total count of emergency-service contacts.
     *
     * @return count of emergency contacts.
     */
    public synchronized int getEmergencyContactCount() {
        int count = 0;
        for (Contact c : contacts) {
            if (c.isEmergencyService()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns total count of personal contacts (Family, Friends, Other).
     *
     * @return count of personal contacts.
     */
    public synchronized int getPersonalContactCount() {
        return getTotalContacts() - getEmergencyContactCount();
    }

    /**
     * Generates a unique, sequential Contact ID in the format "CNT-1001", "CNT-1002", etc.
     * Scans existing contact IDs to guarantee no duplicates even after deletions or restarts.
     *
     * @return A guaranteed unique Contact ID.
     */
    public synchronized String generateNextId() {
        int maxIdNum = 1000;
        for (Contact c : contacts) {
            String id = c.getContactId();
            if (id != null && id.toUpperCase().startsWith("CNT-")) {
                try {
                    int num = Integer.parseInt(id.substring(4).trim());
                    if (num > maxIdNum) {
                        maxIdNum = num;
                    }
                } catch (NumberFormatException ignored) {
                    // Custom non-numeric ID suffix, skip
                }
            }
        }
        int nextNum = maxIdNum + 1;
        String candidate = String.format("CNT-%04d", nextNum);
        // Safety check to ensure uniqueness
        while (containsId(candidate)) {
            nextNum++;
            candidate = String.format("CNT-%04d", nextNum);
        }
        return candidate;
    }

    /**
     * Returns the most recently added contacts, up to a specified limit.
     *
     * @param limit Maximum number of recent contacts to return.
     * @return List of recent contacts (newest first).
     */
    public synchronized List<Contact> getRecentContacts(int limit) {
        List<Contact> recent = new ArrayList<>();
        int count = 0;
        for (int i = contacts.size() - 1; i >= 0 && count < limit; i--) {
            recent.add(contacts.get(i));
            count++;
        }
        return recent;
    }

    /**
     * Replaces all contacts in the manager with a new list (used after loading from file).
     *
     * @param newContacts List of contacts to load.
     */
    public synchronized void setContacts(List<Contact> newContacts) {
        contacts.clear();
        if (newContacts != null) {
            for (Contact c : newContacts) {
                if (c != null && !containsId(c.getContactId())) {
                    contacts.add(c);
                }
            }
        }
    }

    /**
     * Clears all contacts from memory.
     */
    public synchronized void clear() {
        contacts.clear();
    }
}

