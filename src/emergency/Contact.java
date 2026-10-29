package emergency;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an individual Emergency Contact in the Emergency Contact Assistant
 * application. Demonstrates Object-Oriented Programming principles:
 * Encapsulation with private attributes and public getter/setter accessors.
 */
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;
    // Available categories
    public static final String[] CATEGORIES = {
        "Police",
        "Hospital",
        "Ambulance",
        "Fire Service",
        "Family",
        "Friends",
        "Other"
    };
    // Private attributes (Encapsulation)
    private String contactId;
    private String name;
    private String phoneNumber;
    private String category;
    private String address;
    private String email;
    private String notes;

    /**
     * Default constructor.
     */
    public Contact() {
        this("", "", "", "Other", "", "", "");
    }

    /**
     * Parameterized constructor to initialize a contact.
     *
     * @param contactId Unique identifier for the contact (e.g. CNT-1001)
     * @param name Full name of the contact or organization
     * @param phoneNumber Phone number with optional country code
     * @param category Category (e.g. Police, Hospital, Family, etc.)
     * @param address Physical address or location
     * @param email Email address (optional, can be empty)
     * @param notes Special instructions or notes (optional, can be empty)
     */
    public Contact(String contactId, String name, String phoneNumber, String category,
            String address, String email, String notes) {
        this.contactId = (contactId != null) ? contactId.trim() : "";
        this.name = (name != null) ? name.trim() : "";
        this.phoneNumber = (phoneNumber != null) ? phoneNumber.trim() : "";
        this.category = (category != null && !category.trim().isEmpty()) ? category.trim() : "Other";
        this.address = (address != null) ? address.trim() : "";
        this.email = (email != null) ? email.trim() : "";
        this.notes = (notes != null) ? notes.trim() : "";
    }

    // --- Getters and Setters (Encapsulation) ---
    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = (contactId != null) ? contactId.trim() : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name != null) ? name.trim() : "";
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = (phoneNumber != null) ? phoneNumber.trim() : "";
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = (category != null && !category.trim().isEmpty()) ? category.trim() : "Other";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = (address != null) ? address.trim() : "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = (email != null) ? email.trim() : "";
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.trim() : "";
    }

    /**
     * Checks if this contact belongs to an emergency service (Police, Hospital,
     * Ambulance, Fire Service).
     *
     * @return true if an emergency service category, false otherwise.
     */
    public boolean isEmergencyService() {
        if (category == null) {
            return false;
        }
        String cat = category.trim().toLowerCase();
        return cat.equals("police")
                || cat.equals("hospital")
                || cat.equals("ambulance")
                || cat.equals("fire service");
    }

    /**
     * Returns a structured, formatted multi-line description of the contact.
     * Useful for viewing complete contact details in dialogs and cards.
     *
     * @return A formatted multi-line String representation.
     */
    public String getFormattedDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("           CONTACT DETAILS               \n");
        sb.append("=========================================\n");
        sb.append(String.format("Contact ID  : %s\n", contactId));
        sb.append(String.format("Full Name   : %s\n", name));
        sb.append(String.format("Phone Number: %s\n", phoneNumber));
        sb.append(String.format("Category    : %s %s\n", category, isEmergencyService() ? "[EMERGENCY SERVICE]" : "[PERSONAL]"));
        sb.append(String.format("Address     : %s\n", address.isEmpty() ? "N/A" : address));
        sb.append(String.format("Email       : %s\n", email.isEmpty() ? "N/A" : email));
        sb.append(String.format("Notes       : %s\n", notes.isEmpty() ? "None" : notes));
        sb.append("=========================================");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s, %s)", contactId, name, phoneNumber, category);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Contact contact = (Contact) o;
        return Objects.equals(contactId, contact.contactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactId);
    }
}
