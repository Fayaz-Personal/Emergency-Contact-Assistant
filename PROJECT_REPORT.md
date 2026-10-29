# PROJECT REPORT

## EMERGENCY CONTACT ASSISTANT
**Course:** Object-Oriented Programming using Java (Sem 3 / CS201)  
**Project Type:** College Micro Project  
**Author:** Second-Year Computer Science & Engineering Student  
**Implementation Technology:** Pure Java (JDK 17+, Java Swing, Java File I/O)  
**Execution Environment:** Eclipse IDE & Java Standard Platform  

---

### TABLE OF CONTENTS
1. [Introduction](#1-introduction)
2. [Problem Statement](#2-problem-statement)
3. [Objectives](#3-objectives)
4. [Existing System](#4-existing-system)
5. [Proposed System](#5-proposed-system)
6. [Software Requirements](#6-software-requirements)
7. [Hardware Requirements](#7-hardware-requirements)
8. [System Architecture](#8-system-architecture)
9. [Class Descriptions](#9-class-descriptions)
10. [Algorithms](#10-algorithms)
11. [Testing & Verification](#11-testing--verification)
12. [Results](#12-results)
13. [Limitations](#13-limitations)
14. [Future Enhancements](#14-future-enhancements)
15. [Conclusion](#15-conclusion)

---

### 1. Introduction

During life-threatening emergencies, such as medical incidents, fires, road accidents, or criminal threats, immediate communication with designated responders is vital. While digital smartphones are commonplace, their contact books frequently mix hundreds of casual contacts, commercial numbers, and delivery drivers with emergency helplines. Furthermore, internet searches during a crisis often return outdated, toll-free, or non-operational numbers.

**Emergency Contact Assistant** is an offline, standalone desktop application built entirely in Java using the Java Swing GUI toolkit and standard Java File I/O. It provides users with a dedicated, organized dashboard to record, maintain, search, and access critical local emergency services (Police, Hospitals, Ambulances, Fire Stations) alongside trusted personal contacts (Family, Friends).

---

### 2. Problem Statement

1. **Information Fragmentation:** Emergency contact numbers are often written on sticky notes, scattered across messaging apps, or buried inside phone address books containing hundreds of unrelated contacts.
2. **Search Inefficiency During Panics:** High-stress emergency situations degrade user motor skills and focus. Inability to instantly filter specifically for "Police" or "Ambulance" causes critical delays.
3. **Reliance on Unstable Connectivity:** Cloud-based directories or online searches fail during power outages, cellular tower congestion, or natural disasters.
4. **Data Overwrite & Delimiter Vulnerabilities:** Simple text-based contact storage programs frequently fail when addresses or notes contain commas or line breaks, leading to corrupted data files.

---

### 3. Objectives

* Design and implement a robust desktop contact management system adhering to core Object-Oriented Programming (OOP) principles in Java.
* Create a modern, intuitive, responsive user interface using **Java Swing** with a professional dark navy and blue color palette.
* Provide categorization mechanisms separating **Emergency Services** (Police, Hospital, Ambulance, Fire Service) from **Personal Contacts** (Family, Friends, Other).
* Implement real-time dashboard analytics tracking total, emergency, and personal contact metrics.
* Enforce rigorous client-side input validation on names, international phone numbers (with leading `+`, spaces, and dashes), and optional email formats.
* Build a reliable, UTF-8 encoded local file persistence mechanism (`data/contacts.txt`) using atomic file operations and safe character escaping.
* Ensure seamless cross-platform compatibility and zero-dependency compilation in **Eclipse IDE**.

---

### 4. Existing System

Traditional contact management typically relies on:
1. **Smartphone Phonebooks:** Contacts are alphabetically sorted without prominent emergency segregation. Searching "Police" may yield zero results if saved as "Inspector Raj".
2. **Paper Directories / Diaries:** Prone to physical loss, damage, and lack search and quick-filter capabilities.
3. **Enterprise CRM / Cloud Systems:** Heavyweight, require network access, account sign-ins, and ongoing subscriptions, making them unsuitable for simple local emergency access.

#### Disadvantages of Existing Systems:
* No dedicated emergency triage mode.
* High latency when finding numbers under stress.
* Requirement for persistent internet connection.
* Risk of accidental contact overwriting.

---

### 5. Proposed System

The proposed **Emergency Contact Assistant** provides:
* **Dedicated Emergency Directory:** Instant access to four major emergency responders: Police, Hospital, Ambulance, Fire Service.
* **Consolidated Dashboard:** Displays real-time statistics, quick action shortcuts, recent contacts, and global quick search.
* **Unified Form Management:** Reusable input panel for adding and editing records with automatic sequential ID assignment (`CNT-1001`, `CNT-1002`).
* **Safe Local File Persistence:** Encodes multi-line text and special characters into a clean record-based format in `data/contacts.txt` with atomic write protection.
* **Strict OOP Architecture:** Clear segregation of entities (`Contact`), logic (`ContactManager`), storage (`FileHandler`), and UI presentation (`MainFrame`, `DashboardPanel`, `ContactListPanel`, `EmergencyDirectoryPanel`).

---

### 6. Software Requirements

* **Operating System:** Windows 10/11, macOS, or Linux.
* **Programming Language:** Java Standard Edition (JDK 17 or later; tested on OpenJDK 21).
* **Development Environment:** Eclipse IDE for Java Developers (2022-09 or later).
* **GUI Toolkit:** Java Swing & Java AWT (`javax.swing.*`, `java.awt.*`).
* **Build System:** Standard Java Compiler (`javac`), optional Eclipse build tool.
* **External Dependencies:** None (Zero third-party JARs, Maven, or Gradle plugins).

---

### 7. Hardware Requirements

* **Processor:** Intel Core i3 / AMD Ryzen 3 or higher.
* **RAM:** Minimum 2 GB (4 GB recommended).
* **Disk Space:** 50 MB free space (application code and storage require < 5 MB).
* **Display Resolution:** Minimum 1024 × 768 pixels (optimized for 1200 × 750).

---

### 8. System Architecture

The application adopts a modular **Model-View-Controller (MVC)** inspired architecture:

```text
+-------------------------------------------------------------------------+
|                              PRESENTATION (VIEW)                        |
|                                                                         |
|   +-----------------------------------------------------------------+   |
|   |                           MainFrame                             |   |
|   +-----------------------------------------------------------------+   |
|        |                  |                  |                  |       |
|        v                  v                  v                  v       |
|  DashboardPanel   ContactListPanel   ContactFormPanel   EmergencyDirPanel|
+-------------------------------------------------------------------------+
                                    |
                                    v (Method Invocations & Listeners)
+-------------------------------------------------------------------------+
|                         BUSINESS LOGIC (CONTROLLER)                     |
|                                                                         |
|   +-----------------------------------------------------------------+   |
|   |                         ContactManager                          |   |
|   |   - ArrayList<Contact> contacts                                 |   |
|   |   - addContact(), searchContacts(), filterByCategory()          |   |
|   |   - updateContact(), deleteContact(), generateNextId()          |   |
|   +-----------------------------------------------------------------+   |
+-------------------------------------------------------------------------+
           |                                             |
           v (Manages Entities)                          v (Persistence)
+-----------------------+              +----------------------------------+
|      DATA MODEL       |              |           PERSISTENCE            |
|                       |              |                                  |
|    Contact.java       |              |        FileHandler.java          |
|  - contactId, name    |              |  - loadContacts()                |
|  - phone, category    |              |  - saveContacts(List<Contact>)   |
|  - address, email     |              |  - Safe Escaping & UTF-8 I/O     |
|  - notes              |              +----------------------------------+
+-----------------------+                                |
                                                         v
                                              +---------------------+
                                              |  data/contacts.txt  |
                                              +---------------------+
```

---

### 9. Class Descriptions

#### 9.1 `Contact.java`
* **Package:** `emergency`
* **Purpose:** Represents an individual contact record.
* **OOP Principle:** **Encapsulation**. All state fields (`contactId`, `name`, `phoneNumber`, `category`, `address`, `email`, `notes`) are private.
* **Key Methods:**
  * `getContactId()`, `setContactId()`: Access and mutate ID.
  * `getName()`, `setName()`: Access and mutate full name.
  * `getPhoneNumber()`, `setPhoneNumber()`: Access phone number string.
  * `isEmergencyService()`: Returns `true` if category is Police, Hospital, Ambulance, or Fire Service.
  * `getFormattedDescription()`: Generates a formatted text card for detailed inspection.
  * `toString()`, `equals()`, `hashCode()`: Overridden for clean representation and ID equality checks.

#### 9.2 `ContactManager.java`
* **Package:** `emergency`
* **Purpose:** Encapsulates collection management and business operations.
* **Data Structure:** `ArrayList<Contact>`.
* **Key Methods:**
  * `addContact(Contact contact)`: Validates ID uniqueness and adds record.
  * `getAllContacts()`: Returns a defensive clone of the contact list.
  * `searchContacts(String query)`: Executes case-insensitive multi-field search.
  * `filterByCategory(String category)`: Returns contacts matching target category.
  * `getEmergencyContacts()`: Filters strictly for emergency responders.
  * `updateContact(String id, Contact updated)`: In-place update preserving contact ID.
  * `deleteContact(String id)`: Removes contact by ID.
  * `generateNextId()`: Scans existing numeric suffixes in `CNT-XXXX` to return guaranteed unique next ID.
  * `getTotalContacts()`, `getEmergencyContactCount()`, `getPersonalContactCount()`: Dynamic counters.

#### 9.3 `FileHandler.java`
* **Package:** `emergency`
* **Purpose:** Implements persistent disk storage in `data/contacts.txt`.
* **Key Methods:**
  * `loadContacts()`: Reads file with `BufferedReader` using `StandardCharsets.UTF_8`. Ignores comments and blank lines; parses key-value records between `RECORD_START` and `RECORD_END`.
  * `saveContacts(List<Contact> contacts)`: Writes records with `BufferedWriter`. Employs atomic replacement via temporary file swap (`contacts.txt.tmp` → `contacts.txt`) to protect against file corruption.
  * `escapeValue(String)`, `unescapeValue(String)`: Safely encodes newlines (`\n`) and backslashes (`\\`) to preserve multi-line notes without delimiter collisions.

#### 9.4 `MainFrame.java`
* **Package:** `emergency`
* **Purpose:** Primary application window. Hosts the dark navy navigation sidebar, window listeners, and `CardLayout` container.
* **Coordination Role:** Mediates communication between UI panels, `ContactManager`, and `FileHandler`. Displays confirmation dialogs on deletion and application exit.

#### 9.5 `DashboardPanel.java`
* **Package:** `emergency`
* **Purpose:** User landing screen displaying visual stat cards (Total, Emergency, Personal), action buttons ("Add Contact", "View All", "Load Sample Data"), and a recent contacts `JTable`.

#### 9.6 `ContactListPanel.java`
* **Package:** `emergency`
* **Purpose:** Full directory browser featuring a responsive `JTable`, live search field, category dropdown filter, and buttons for "View Details", "Edit Contact", and "Delete Contact".

#### 9.7 `ContactFormPanel.java`
* **Package:** `emergency`
* **Purpose:** Reusable input form handling both new contact registration and updates. Performs validation against empty names, invalid phone lengths, and non-conforming email formats.

#### 9.8 `EmergencyDirectoryPanel.java`
* **Package:** `emergency`
* **Purpose:** Dedicated view filtering strictly for emergency responders (Police, Hospital, Ambulance, Fire Service) with an integrated empty-state card for intuitive user guidance.

#### 9.9 `AboutPanel.java`
* **Package:** `emergency`
* **Purpose:** Displays project documentation, academic course information, core Java concepts utilized, and runtime JVM specifications.

#### 9.10 `UIUtils.java`
* **Package:** `emergency`
* **Purpose:** Centralized visual constants, color codes, Segoe UI font definitions, custom button painters, and table styling renderers.

---

### 10. Algorithms

#### 10.1 Case-Insensitive Multi-Field Search Algorithm
```text
Algorithm: SearchContacts
Input: query string Q, List of contacts C
Output: List of matching contacts R

1. If Q is null or trimmed Q is empty:
     Return shallow copy of C
2. Let lowerQuery = lowercase(trim(Q))
3. Initialize empty list R
4. For each contact c in C:
     If lowercase(c.name) contains lowerQuery OR
        lowercase(c.phone) contains lowerQuery OR
        lowercase(c.category) contains lowerQuery OR
        lowercase(c.address) contains lowerQuery OR
        lowercase(c.contactId) contains lowerQuery:
           Add c to R
5. Return R
```
* **Time Complexity:** $O(N \times L)$, where $N$ is total contacts and $L$ is average string length. Instantaneous for local desktop collections.
* **Space Complexity:** $O(K)$, where $K$ is number of matches found.

#### 10.2 Sequential Unique ID Generation Algorithm
```text
Algorithm: GenerateNextId
Input: List of contacts C
Output: Unique string ID in format "CNT-XXXX"

1. Set maxIdNum = 1000
2. For each contact c in C:
     Let id = c.contactId
     If id starts with "CNT-" (case-insensitive):
       Extract numeric substring after index 4
       Parse into integer num
       If num > maxIdNum:
         maxIdNum = num
3. candidate = "CNT-" + formatted(maxIdNum + 1)
4. While ID candidate exists in C:
     maxIdNum = maxIdNum + 1
     candidate = "CNT-" + formatted(maxIdNum + 1)
5. Return candidate
```
* **Guarantees:** Monotonically increasing, collision-free identifiers even after records are deleted or application restarts.

#### 10.3 Atomic File Persistence Algorithm
```text
Algorithm: AtomicSaveContacts
Input: List of contacts C, target file path F

1. Ensure directory of F exists; create if absent.
2. Let tempPath = F + ".tmp"
3. Open tempPath using BufferedWriter with UTF-8 encoding:
     Write header metadata
     For each contact c in C:
       Write RECORD_START
       Write escaped key-value pairs (id, name, phone, category, address, email, notes)
       Write RECORD_END
4. Flush and close writer.
5. Move tempPath to F using StandardCopyOption.REPLACE_EXISTING and StandardCopyOption.ATOMIC_MOVE.
6. If atomic move unsupported, fallback to standard REPLACE_EXISTING.
```
* **Safety Benefit:** The original `contacts.txt` file is never opened in truncate mode, preventing zero-byte file wiping if a crash happens mid-save.

---

### 11. Testing & Verification

The project includes two automated test suites:
1. `ContactManagerTest.java`: Unit and integration testing for business logic, search, counters, and file I/O resilience.
2. `GUISmokeTest.java`: Headless/Swing EDT verification testing panel instantiation, screen navigation, and disposal.

#### 11.1 Automated Test Execution Log

```text
> java -cp bin emergency.ContactManagerTest

======================================================
   EMERGENCY CONTACT ASSISTANT - UNIT & INTEGRATION TESTS
======================================================

Test 01: Contact Entity Creation & Encapsulation              [PASSED]
Test 02: ContactManager Add Contact                           [PASSED]
Test 03: Prevent Duplicate Contact IDs                        [PASSED]
Test 04: Case-Insensitive Search by Name and Phone            [PASSED]
Test 05: Filter Contacts by Category                          [PASSED]
Test 06: Emergency vs Personal Contact Counting               [PASSED]
Test 07: Update Existing Contact                              [PASSED]
Test 08: Delete Contact                                       [PASSED]
Test 09: Sequential Unique Contact ID Generation              [PASSED]
Test 10: FileHandler UTF-8 Save, Special Characters & Reload  [PASSED]
Test 11: FileHandler Empty File Handling                      [PASSED]
Test 12: FileHandler Malformed Record Resilience              [PASSED]

======================================================
   TEST RESULTS SUMMARY: 12/12 PASSED (0 FAILED)
======================================================
```

```text
> java -cp bin emergency.GUISmokeTest

Starting GUI Smoke Test...
1. Initializing MainFrame...
2. Testing screen switching...
3. Testing adding contact via MainFrame...
4. Testing update contact via MainFrame...
5. Testing delete contact via MainFrame...
6. Disposing MainFrame cleanly...
GUI Smoke Test PASSED successfully!
```

#### 11.2 Manual Test Checklist Results

| Test Case | Procedure | Expected Outcome | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Add contact with valid fields | Success dialog shown; table updated; persisted to file | Form cleared, contact added with auto ID `CNT-1001` | **PASS** |
| **TC-02** | Submit form with blank Name | Warning dialog: "Full Name cannot be empty" | Warning displayed; focus returned to Name field | **PASS** |
| **TC-03** | Submit invalid phone "987" (< 7 digits) | Warning dialog: "Phone number must contain between 7 and 15 digits" | Input rejected; no record saved | **PASS** |
| **TC-04** | International phone `+91 98765 43210` | Accepted without converting to integer | Saved cleanly preserving leading `+` and spaces | **PASS** |
| **TC-05** | Case-insensitive search "rahul" | Contact "Rahul" matches and displays in table | Single row returned accurately | **PASS** |
| **TC-06** | Filter category "Hospital" | Only Hospital contacts shown in table | Table filtered; non-hospital contacts hidden | **PASS** |
| **TC-07** | Edit contact phone number | Existing ID preserved; new phone saved to file | Contact updated; table refreshed immediately | **PASS** |
| **TC-08** | Delete contact with confirmation | Confirmation dialog pops up; on Yes, contact deleted | Contact removed; file updated; count decremented | **PASS** |
| **TC-09** | Cancel contact deletion | Confirmation dialog; click No | Contact preserved in memory and on disk | **PASS** |
| **TC-10** | Application restart persistence | Close and reopen application | All previously saved contacts reload accurately | **PASS** |
| **TC-11** | Zero-byte or missing data file | Launch with non-existent file | Empty list initialized without crash; file created | **PASS** |
| **TC-12** | Load verified sample data button | Click "Load Sample Data" on Dashboard | 3 verified contacts (Rahul, Arun, Priya) populated | **PASS** |

---

### 12. Results

* The application compiled cleanly under JDK 17 / JDK 21 with zero compilation warnings or errors.
* The graphical user interface successfully opens centered at 1200 × 750 pixels with a modern dark navy sidebar, cards, and alternating table rows.
* Contact records persist safely in `data/contacts.txt` across complete restarts.
* All 12 automated unit tests and GUI smoke tests passed with a 100% pass rate.
* Ready for immediate import and execution in Eclipse IDE.

---

### 13. Limitations

1. **Local Desktop Scope:** Designed for a single workstation; does not synchronize with external SQL/NoSQL cloud servers.
2. **Telephony Integration:** Acts as an information management system; does not auto-dial phone numbers through cellular modems.
3. **Multimedia Storage:** Does not store profile pictures or audio recordings to maintain pure Java Standard Library simplicity.

---

### 14. Future Enhancements

* **vCard (.vcf) & PDF Export:** Export selected contacts or full emergency directories for offline printouts.
* **SMS Gateway Integration:** Implement Twilio or local GSM modem integration to broadcast emergency SOS messages with GPS coordinates.
* **Dialer Integration:** Add URI scheme handlers (`tel:`) for systems with softphone capabilities.
* **Audio Voice Assistant:** Voice-command search ("Find nearest hospital") using Java Speech APIs.

---

### 15. Conclusion

The **Emergency Contact Assistant** desktop application successfully achieves all project goals. Built strictly within the Java Standard Library, it demonstrates foundational and advanced Object-Oriented Programming concepts—such as encapsulation, inheritance, event-driven GUI programming, dynamic data structures, and defensive file I/O handling. It delivers a reliable, responsive, and aesthetically pleasing desktop utility that second-year Computer Science students can confidently demonstrate, explain, and expand.

