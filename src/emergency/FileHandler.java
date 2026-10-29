package emergency;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles persistent storage of emergency contacts to and from a local file.
 * Uses UTF-8 encoded text format with safe field escaping to prevent delimiter collisions
 * and preserve multi-line notes or addresses.
 * Implements atomic saving to guard against data corruption.
 */
public class FileHandler {

    private static final String DEFAULT_FILE_PATH = "data/contacts.txt";
    private static final String RECORD_START = "RECORD_START";
    private static final String RECORD_END = "RECORD_END";

    private final String filePath;

    /**
     * Constructs a FileHandler pointing to the default data path: "data/contacts.txt".
     */
    public FileHandler() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Constructs a FileHandler with a specified file path.
     *
     * @param filePath Path to the data file.
     */
    public FileHandler(String filePath) {
        this.filePath = (filePath != null && !filePath.trim().isEmpty()) ? filePath.trim() : DEFAULT_FILE_PATH;
    }

    /**
     * Returns the configured file path.
     *
     * @return File path string.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Returns the absolute file path on disk.
     *
     * @return Absolute path string.
     */
    public String getAbsoluteFilePath() {
        return new File(filePath).getAbsolutePath();
    }

    /**
     * Loads contacts from the local data file.
     * If the file does not exist or the directory is missing, returns an empty list gracefully.
     * If individual records are malformed, logs the error and continues parsing remaining records.
     *
     * @return List of parsed Contact objects.
     * @throws IOException If a fatal read error occurs.
     */
    public List<Contact> loadContacts() throws IOException {
        List<Contact> contacts = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            // File does not exist yet; return empty list gracefully
            return contacts;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            boolean inRecord = false;
            Map<String, String> fieldMap = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.equalsIgnoreCase(RECORD_START)) {
                    inRecord = true;
                    fieldMap.clear();
                    continue;
                }

                if (line.equalsIgnoreCase(RECORD_END)) {
                    if (inRecord) {
                        Contact contact = parseRecordMap(fieldMap);
                        if (contact != null) {
                            contacts.add(contact);
                        }
                    }
                    inRecord = false;
                    continue;
                }

                if (inRecord) {
                    int eqIdx = line.indexOf('=');
                    if (eqIdx > 0) {
                        String key = line.substring(0, eqIdx).trim().toLowerCase();
                        String rawVal = line.substring(eqIdx + 1);
                        String unescapedVal = unescapeValue(rawVal);
                        fieldMap.put(key, unescapedVal);
                    }
                }
            }
        }

        return contacts;
    }

    /**
     * Saves a list of contacts to the local data file.
     * Uses atomic write (writes to a temporary file first, then replaces the target)
     * to protect against accidental file corruption or truncation.
     *
     * @param contacts List of contacts to save.
     * @throws IOException If saving fails.
     */
    public synchronized void saveContacts(List<Contact> contacts) throws IOException {
        if (contacts == null) {
            throw new IllegalArgumentException("Cannot save a null contacts list.");
        }

        File targetFile = new File(filePath);
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created && !parentDir.exists()) {
                throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }

        // Write to temporary file first
        File tempFile = new File(filePath + ".tmp");

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {

            writer.write("# Emergency Contact Assistant Data File");
            writer.newLine();
            writer.write("# Stored at: " + java.time.LocalDateTime.now());
            writer.newLine();
            writer.write("# Total Records: " + contacts.size());
            writer.newLine();
            writer.newLine();

            for (Contact contact : contacts) {
                if (contact == null) continue;

                writer.write(RECORD_START);
                writer.newLine();
                writer.write("id=" + escapeValue(contact.getContactId()));
                writer.newLine();
                writer.write("name=" + escapeValue(contact.getName()));
                writer.newLine();
                writer.write("phone=" + escapeValue(contact.getPhoneNumber()));
                writer.newLine();
                writer.write("category=" + escapeValue(contact.getCategory()));
                writer.newLine();
                writer.write("address=" + escapeValue(contact.getAddress()));
                writer.newLine();
                writer.write("email=" + escapeValue(contact.getEmail()));
                writer.newLine();
                writer.write("notes=" + escapeValue(contact.getNotes()));
                writer.newLine();
                writer.write(RECORD_END);
                writer.newLine();
                writer.newLine();
            }
            writer.flush();
        }

        // Atomically replace target file with temporary file
        Path tempPath = tempFile.toPath();
        Path targetPath = Paths.get(filePath);
        try {
            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            // Fallback for filesystems that do not support ATOMIC_MOVE
            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Parses a map of field key-values into a validated Contact object.
     * Returns null if required fields (id, name) are missing or invalid.
     */
    private Contact parseRecordMap(Map<String, String> map) {
        String id = map.getOrDefault("id", "").trim();
        String name = map.getOrDefault("name", "").trim();
        String phone = map.getOrDefault("phone", "").trim();
        String category = map.getOrDefault("category", "Other").trim();
        String address = map.getOrDefault("address", "").trim();
        String email = map.getOrDefault("email", "").trim();
        String notes = map.getOrDefault("notes", "").trim();

        if (id.isEmpty() || name.isEmpty()) {
            System.err.println("[FileHandler] Skipping corrupted or incomplete record: id=" + id + ", name=" + name);
            return null;
        }

        return new Contact(id, name, phone, category, address, email, notes);
    }

    /**
     * Escapes special characters such as backslashes and newlines.
     */
    public static String escapeValue(String value) {
        if (value == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Unescapes encoded backslashes and newlines back to their original characters.
     */
    public static String unescapeValue(String value) {
        if (value == null) return "";
        StringBuilder sb = new StringBuilder();
        boolean inEscape = false;

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (inEscape) {
                switch (c) {
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    default:
                        sb.append('\\').append(c);
                        break;
                }
                inEscape = false;
            } else {
                if (c == '\\') {
                    inEscape = true;
                } else {
                    sb.append(c);
                }
            }
        }
        if (inEscape) {
            sb.append('\\');
        }
        return sb.toString();
    }
}

