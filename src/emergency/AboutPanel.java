package emergency;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

/**
 * About screen presenting project metadata, core Object-Oriented Programming
 * concepts demonstrated, objectives, and runtime environment details.
 */
public class AboutPanel extends JPanel {

    private final MainFrame mainFrame;
    private final FileHandler fileHandler;

    public AboutPanel(MainFrame mainFrame, FileHandler fileHandler) {
        this.mainFrame = mainFrame;
        this.fileHandler = fileHandler;

        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_MAIN);

        initUI();
    }

    private void initUI() {
        // 1. Header
        JPanel headerPanel = UIUtils.createHeaderPanel(
            "About Emergency Contact Assistant",
            "College Java Micro Project - Object Oriented Programming (CS201 / Sem 3)"
        );
        add(headerPanel, BorderLayout.NORTH);

        // 2. Main content inside scroll pane
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UIUtils.BG_MAIN);
        content.setBorder(new EmptyBorder(20, 30, 20, 30));

        // 2A. Project Summary Card
        JPanel summaryCard = UIUtils.createCardPanel();
        summaryCard.setLayout(new BorderLayout(0, 10));

        JLabel titleLbl = new JLabel("Emergency Contact Assistant");
        titleLbl.setFont(UIUtils.FONT_HEADER_TITLE);
        titleLbl.setForeground(UIUtils.BLUE_PRIMARY);

        JLabel descLbl = new JLabel("<html><b>Emergency Contact Assistant</b> is a standalone, resilient desktop utility designed for managing both critical emergency service contacts (Police, Hospital, Ambulance, Fire Service) and personal contacts (Family, Friends) in one consolidated dashboard.<br><br>" +
            "<b>Project Objective:</b> Provide an intuitive, offline, and reliable desktop contact management solution with local persistent file storage, robust input validation, quick search, and emergency categorization without requiring complex external database engines or cloud dependencies.</html>");
        descLbl.setFont(UIUtils.FONT_REGULAR);
        descLbl.setForeground(UIUtils.TEXT_DARK);

        summaryCard.add(titleLbl, BorderLayout.NORTH);
        summaryCard.add(descLbl, BorderLayout.CENTER);
        content.add(summaryCard);
        content.add(Box.createVerticalStrut(16));

        // 2B. Technical Specifications & Core Concepts (2-column layout)
        JPanel gridPanel = new JPanel(new java.awt.GridLayout(1, 2, 16, 0));
        gridPanel.setOpaque(false);

        // Left Card: Project Metadata
        JPanel metaCard = UIUtils.createCardPanel();
        metaCard.setLayout(new BoxLayout(metaCard, BoxLayout.Y_AXIS));

        JLabel metaTitle = new JLabel("Project Metadata & Tech Stack");
        metaTitle.setFont(UIUtils.FONT_SECTION_TITLE);
        metaTitle.setForeground(UIUtils.TEXT_DARK);
        metaCard.add(metaTitle);
        metaCard.add(Box.createVerticalStrut(12));

        metaCard.add(createMetaRow("Project Title:", "Emergency Contact Assistant"));
        metaCard.add(createMetaRow("Project Type:", "Java Mini Project (Sem 3)"));
        metaCard.add(createMetaRow("Programming Language:", "Java (JDK 17+)"));
        metaCard.add(createMetaRow("GUI Framework:", "Java Swing (JFrame, JPanel, JTable)"));
        metaCard.add(createMetaRow("Layout Managers:", "BorderLayout, GridBagLayout, FlowLayout"));
        metaCard.add(createMetaRow("Data Storage:", "Local File Handling (data/contacts.txt)"));
        metaCard.add(createMetaRow("Data Structures:", "ArrayList<Contact>, Collections"));
        metaCard.add(createMetaRow("IDE Compatibility:", "Eclipse IDE & Standard Java SDK"));

        gridPanel.add(metaCard);

        // Right Card: Core OOP Concepts Demonstrated
        JPanel oopCard = UIUtils.createCardPanel();
        oopCard.setLayout(new BoxLayout(oopCard, BoxLayout.Y_AXIS));

        JLabel oopTitle = new JLabel("Core Concepts Demonstrated");
        oopTitle.setFont(UIUtils.FONT_SECTION_TITLE);
        oopTitle.setForeground(UIUtils.TEXT_DARK);
        oopCard.add(oopTitle);
        oopCard.add(Box.createVerticalStrut(12));

        oopCard.add(createConceptBullet("Classes & Objects:", "Structured Contact and Manager entities modeling real-world domain records."));
        oopCard.add(createConceptBullet("Encapsulation:", "Private attributes in Contact with validated getters and setters."));
        oopCard.add(createConceptBullet("Separation of Concerns:", "Business logic (ContactManager) strictly decoupled from UI views."));
        oopCard.add(createConceptBullet("Dynamic Collections:", "ArrayList dynamic resizing, indexing, sorting, and linear filtering."));
        oopCard.add(createConceptBullet("Robust File I/O:", "BufferedReader / BufferedWriter with UTF-8 encoding and atomic file replacement."));
        oopCard.add(createConceptBullet("Exception Handling:", "Try-catch blocks protecting against corrupted records and file write failures."));
        oopCard.add(createConceptBullet("Event Handling:", "Java Swing ActionListeners, MouseAdapters, and Event Dispatch Thread (EDT)."));

        gridPanel.add(oopCard);
        content.add(gridPanel);
        content.add(Box.createVerticalStrut(16));

        // 2C. Storage & Runtime Environment Card
        JPanel envCard = UIUtils.createCardPanel();
        envCard.setLayout(new BoxLayout(envCard, BoxLayout.Y_AXIS));

        JLabel envTitle = new JLabel("Runtime & Environment Status");
        envTitle.setFont(UIUtils.FONT_SECTION_TITLE);
        envTitle.setForeground(UIUtils.TEXT_DARK);
        envCard.add(envTitle);
        envCard.add(Box.createVerticalStrut(8));

        envCard.add(createMetaRow("Java Version:", System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")"));
        envCard.add(createMetaRow("Operating System:", System.getProperty("os.name") + " " + System.getProperty("os.arch")));
        envCard.add(createMetaRow("Storage File Location:", fileHandler.getAbsoluteFilePath()));
        envCard.add(createMetaRow("Safety Disclaimer:", "This application manages contact info locally and does not automatically dial phone numbers."));

        content.add(envCard);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createMetaRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(3, 0, 3, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_BOLD);
        lbl.setForeground(UIUtils.TEXT_DARK);
        lbl.setPreferredSize(new Dimension(170, 20));

        JLabel val = new JLabel(value);
        val.setFont(UIUtils.FONT_REGULAR);
        val.setForeground(UIUtils.TEXT_MUTED);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }

    private JPanel createConceptBullet(String title, String explanation) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(3, 0, 3, 0));

        JLabel bullet = new JLabel("* " + title);
        bullet.setFont(UIUtils.FONT_BOLD);
        bullet.setForeground(UIUtils.BLUE_PRIMARY);
        bullet.setPreferredSize(new Dimension(180, 20));

        JLabel exp = new JLabel("<html>" + explanation + "</html>");
        exp.setFont(UIUtils.FONT_REGULAR);
        exp.setForeground(UIUtils.TEXT_MUTED);

        row.add(bullet, BorderLayout.WEST);
        row.add(exp, BorderLayout.CENTER);
        return row;
    }
}

