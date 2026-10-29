# Emergency Contact Assistant

A modern, standalone Java desktop application built using **Java Swing** for managing emergency services and personal contact directories. Developed as a **Semester 3 Object-Oriented Programming (Java) College Micro Project**.

---

## 1. Abstract

During critical emergencies—such as medical crises, accidents, fires, or security incidents—immediate access to verified local contact numbers is essential. Relying on disorganized phone contact lists or web searches during network outages can cause life-threatening delays. **Emergency Contact Assistant** is an offline, lightweight desktop application developed in pure Java. It allows users to register, categorize, search, filter, update, and persist emergency contacts (Police, Hospital, Ambulance, Fire Service) as well as personal emergency contacts (Family, Friends) without requiring external database servers or internet access.

---

## 2. Problem Statement

Standard smartphone contacts mix ordinary acquaintances, commercial callers, and emergency responders in a single long list. In high-stress situations:
1. Locating the nearest hospital casualty ward, police station helpline, or fire dispatch is time-consuming.
2. Incomplete or unverified phone numbers lead to failed calls.
3. Network outages or web search issues make finding local emergency helplines unreliable.
4. Existing heavy database-driven software is unsuitable for simple, instant offline desktop usage.

**Emergency Contact Assistant** solves this by offering a dedicated desktop dashboard with categorized emergency service filtering, instant search, input validation, and local persistent storage.

---

## 3. Objectives

* Build a native, professional desktop application using **Java Swing** adhering to strict Object-Oriented Programming (OOP) principles.
* Provide an intuitive dashboard with dynamic real-time counts for total contacts, emergency services, and personal contacts.
* Implement structured categorized directories for **Police**, **Hospital**, **Ambulance**, and **Fire Service**.
* Enable quick full-text search across names, phone numbers, and locations.
* Implement robust client-side validation for phone numbers (handling international prefixes and formatting characters), email addresses, and mandatory fields.
* Ensure data persistence across application restarts using local file I/O (`BufferedReader`, `BufferedWriter`, atomic file writes) in `data/contacts.txt`.
* Ensure 100% compatibility with **Eclipse IDE** without any external libraries or dependencies.

---

## 4. Key Features

| Feature | Description |
| :--- | :--- |
| **Clean Dashboard** | Displays real-time metrics, recent contacts table, and quick search bar. |
| **All Contacts Table** | Interactive `JTable` with alternating row colors, category filter dropdown, and real-time search. |
| **Add / Edit Contact** | Unified, reusable input form with automatic sequential ID generation (`CNT-1001`, `CNT-1002`). |
| **Input Validation** | Non-empty name checks, multi-format phone regex with digit counting (7–15 digits), and optional email syntax validation. |
| **Dedicated Emergency Directory** | Filtered view specifically for **Police**, **Hospital**, **Ambulance**, and **Fire Service** with empty-state guidance. |
| **View Details Dialog** | Multi-line formatted card inspection for complete addresses, department names, and special emergency instructions. |
| **Safe Deletion** | Confirmation prompts preventing accidental contact removal. |
| **Persistent File Storage** | Saves records to `data/contacts.txt` with delimiter-safe escape formatting and atomic temporary file swapping. |
| **Sample Data Loader** | Dedicated button to instantly load verified test contacts (Rahul, Arun, Priya) for classroom demonstrations. |
| **Modern Swing UI** | Dark navy sidebar, card-based dashboard, Segoe UI typography, and responsive layouts. |

---

## 5. Object-Oriented Programming Concepts Used

* **Encapsulation:** The [`Contact`](src/emergency/Contact.java) class keeps all attributes (`contactId`, `name`, `phoneNumber`, `category`, `address`, `email`, `notes`) private and provides validated public accessors and mutators.
* **Separation of Concerns & Modularity:** Business logic is isolated in [`ContactManager`](src/emergency/ContactManager.java), persistence in [`FileHandler`](src/emergency/FileHandler.java), and graphical views in dedicated Swing panels (`DashboardPanel`, `ContactListPanel`, etc.).
* **Dynamic Collections:** Employs `ArrayList<Contact>` from Java Collections Framework for dynamic memory storage, linear search filtering, and safe defensive copying.
* **Polymorphism & Inheritance:** Extends `JFrame` and `JPanel`, overrides `paintComponent` for custom rounded buttons, and implements `ActionListener` and `MouseAdapter`.
* **Exception Handling:** Robust `try-catch-finally` and try-with-resources blocks protect against file I/O errors, malformed data rows, and duplicate IDs without application crashes.
* **Event Dispatch Thread (EDT):** Uses `SwingUtilities.invokeLater()` to conform to Swing single-thread concurrency rules.

---

## 6. Technology Requirements

* **Language:** Java Standard Edition (JDK 17 or later; tested with JDK 21).
* **GUI Library:** Java Swing & AWT (standard library; no JavaFX or external UI skins required).
* **Persistence:** `java.io` (`BufferedReader`, `BufferedWriter`, `FileInputStream`, `FileOutputStream`, `StandardCharsets.UTF_8`).
* **IDE:** Eclipse IDE for Java Developers (2022-09 or newer recommended), IntelliJ IDEA, or CLI.
* **Operating System:** Cross-platform (Windows 10/11, macOS, Linux).
* **Dependencies:** None (Zero external JARs, no Maven/Gradle required).

---

## 7. Project Folder Structure

```text
Project/
├── .classpath                          # Eclipse build path configuration
├── .gitignore                          # Git ignore rules for build artifacts
├── .project                            # Eclipse project descriptor
├── README.md                           # Comprehensive documentation & guide
├── PROJECT_REPORT.md                   # Full academic project report
├── run.bat                             # One-click Windows launch script
├── test.bat                            # One-click Windows test runner script
├── data/
│   └── contacts.txt                    # Local persistent data file (UTF-8)
├── bin/                                # Compiled Java bytecode (.class files)
└── src/
    └── emergency/
        ├── Main.java                   # Main entry point (EDT launcher)
        ├── MainFrame.java              # Main application window & coordinator
        ├── Contact.java                # Contact model entity (Encapsulation)
        ├── ContactManager.java         # Business logic & collection manager
        ├── FileHandler.java            # File I/O handler with safe escaping
        ├── UIUtils.java                # UI palette, typography & styling helpers
        ├── DashboardPanel.java         # Dashboard statistics & recent items
        ├── ContactFormPanel.java       # Reusable Add & Edit form panel
        ├── ContactListPanel.java       # Table directory, search & actions
        ├── EmergencyDirectoryPanel.java# Dedicated emergency services view
        ├── AboutPanel.java             # About project & runtime specs
        ├── ContactManagerTest.java     # Automated unit & integration tests
        └── GUISmokeTest.java           # Automated GUI smoke test suite
```

---

## 8. Instructions for Running in Eclipse IDE

1. **Open Eclipse IDE.**
2. Select **File → Import...** from the top menu.
3. Expand **General**, select **Existing Projects into Workspace**, and click **Next**.
4. In **Select root directory**, click **Browse...** and select this project directory:
   `e:\SEM 3\OBJECT ORIENTED PROGARMING\Micro Project\Project`
5. Ensure `EmergencyContactAssistant` is checked in the Projects list and click **Finish**.
6. In Eclipse's **Package Explorer**, expand:
   `EmergencyContactAssistant → src → emergency`.
7. Right-click **`Main.java`**, select **Run As → Java Application**.
8. The graphical application will launch immediately.

---

## 9. Running from the Command Line (CLI)

### On Windows:

Double-click `run.bat` or run:

```powershell
# 1. Compile all Java sources into bin/
javac -d bin -sourcepath src src/emergency/*.java

# 2. Run the application
java -cp bin emergency.Main
```

### To run the automated test suite:

Double-click `test.bat` or run:

```powershell
java -cp bin emergency.ContactManagerTest
java -cp bin emergency.GUISmokeTest
```

---

## 10. Data Storage Location & Format

* **File Location:** `data/contacts.txt`
* **Encoding:** UTF-8
* **Storage Protocol:** Block-delimited format with escape sequencing:
  ```text
  RECORD_START
  id=CNT-1001
  name=Rahul
  phone=9876543210
  category=Hospital
  address=Coimbatore Medical College Hospital
  email=rahul@cmch.org
  notes=24/7 Casualty Ward
  RECORD_END
  ```
  * Special characters (`\`, `\n`, `\r`, `\t`) are escaped (`\\`, `\n`) to ensure commas, quotes, and multi-line notes do not corrupt the records.
  * Writes are atomic using a temporary swap file (`contacts.txt.tmp`) to guarantee data safety during interruptions.

---

## 11. Sample Testing Procedure

You can load verified test data directly using the **"Load Sample Data"** button on the Dashboard, or manually test using:

| ID | Name | Phone Number | Category | Address | Email / Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `CNT-1001` | Rahul | `9876543210` | Hospital | Coimbatore | rahul@cmch.org |
| `CNT-1002` | Arun | `9876501234` | Family | Coimbatore | Brother |
| `CNT-1003` | Priya | `9876512345` | Police | Coimbatore | Station Helpline |

### Step-by-Step Test Procedure:
1. **Add Contact:** Click `Add Contact` on the sidebar. Enter name "Rahul", phone "9876543210", category "Hospital", address "Coimbatore". Click `Save Contact`. Verify the success popup.
2. **Input Validation:** Try submitting with an empty name or phone "123". Notice warning popups highlighting invalid inputs.
3. **Search & Filter:** Go to `All Contacts`. Type "Rahul" in the search box and press Enter. Verify only Rahul appears. Clear search and filter by "Police".
4. **Edit Contact:** Select a contact row, click `Edit Contact`, update phone number or notes, and click `Update Contact`.
5. **Delete Contact:** Select a row and click `Delete Contact`. Confirm the prompt. Verify the contact is removed.
6. **Persistence Test:** Close the application. Re-open via `run.bat` or Eclipse. Verify all contacts and IDs are preserved intact.

---

## 12. Known Limitations

* Application runs as a standalone desktop program; it does not connect to external telephony hardware or VoIP APIs to trigger real phone calls.
* Single-user local storage without concurrent multi-user database network synchronization.
* Contact images and avatars are not stored to preserve zero-dependency simplicity.

---

## 13. Future Enhancements

* Export contacts to standard `.vcf` (vCard) and PDF formats.
* Integration with SMS/Email notification gateways for sending one-click emergency SOS alerts with geolocation coordinates.
* Dark / Light mode toggle switch.
* Contact grouping and custom tag support.

