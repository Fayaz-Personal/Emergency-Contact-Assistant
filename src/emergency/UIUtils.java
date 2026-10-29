package emergency;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Utility class providing UI constants, color palettes, fonts, and styling helpers
 * to ensure a clean, professional, and consistent modern look across the application.
 */
public final class UIUtils {

    // Color Palette
    public static final Color NAVY_SIDEBAR    = new Color(15, 23, 42);     // #0F172A Deep Navy
    public static final Color NAVY_SIDEBAR_HOVER = new Color(30, 41, 59); // #1E293B
    public static final Color NAVY_SIDEBAR_ACTIVE = new Color(37, 99, 235); // #2563EB Primary Blue

    public static final Color BG_MAIN         = new Color(248, 250, 252);  // #F8FAFC Soft Grayish White
    public static final Color CARD_BG         = Color.WHITE;
    public static final Color BORDER_COLOR    = new Color(226, 232, 240);  // #E2E8F0 Subtle Border

    public static final Color TEXT_DARK       = new Color(15, 23, 42);     // #0F172A Main Text
    public static final Color TEXT_MUTED      = new Color(100, 116, 139);  // #64748B Secondary Text
    public static final Color TEXT_LIGHT      = new Color(241, 245, 249);  // #F1F5F9 Light Text

    public static final Color BLUE_PRIMARY    = new Color(37, 99, 235);    // #2563EB Royal Blue
    public static final Color BLUE_PRIMARY_HOVER = new Color(29, 78, 216); // #1D4ED8
    public static final Color GREEN_SUCCESS   = new Color(16, 185, 129);   // #10B981 Emerald
    public static final Color RED_DANGER      = new Color(220, 38, 38);    // #DC2626 Crimson Red
    public static final Color AMBER_WARNING   = new Color(217, 119, 6);    // #D97706 Amber
    public static final Color PURPLE_ACCENT   = new Color(124, 58, 237);   // #7C3AED Indigo/Purple
    public static final Color SLATE_SECONDARY = new Color(71, 85, 105);    // #475569 Slate

    // Fonts
    public static final Font FONT_HEADER_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER_SUB   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SECTION_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_REGULAR     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD        = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL       = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL_BOLD  = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_STAT_VAL    = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_NAV_BTN     = new Font("Segoe UI", Font.BOLD, 14);

    private UIUtils() {
        // Prevent instantiation of utility class
    }

    /**
     * Creates a styled modern button with rounded corners, padding, and hover feedback.
     */
    public static JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(FONT_BOLD);
        button.setForeground(fgColor);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        return button;
    }

    /**
     * Creates a card panel with standard white background, subtle border, and padding.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    /**
     * Styles a JTable with alternating row colors, proper row height, and a clean header.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(36);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(219, 234, 254)); // Light blue selection
        table.setSelectionForeground(TEXT_DARK);
        table.setFillsViewportHeight(true);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_DARK);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setReorderingAllowed(false);

        // Alternating row renderer with category badges
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(TEXT_DARK);
                }

                // Padding inside cell
                setBorder(new EmptyBorder(4, 10, 4, 10));

                // Bold font for Contact ID and Name
                if (column == 0 || column == 1) {
                    setFont(FONT_BOLD);
                } else {
                    setFont(FONT_REGULAR);
                }

                return c;
            }
        });
    }

    /**
     * Creates a consistent header bar for panels.
     */
    public static JPanel createHeaderPanel(String title, String subtitle) {
        JPanel header = new JPanel();
        header.setLayout(new java.awt.BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(18, 24, 18, 24)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new javax.swing.BoxLayout(textPanel, javax.swing.BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_HEADER_TITLE);
        titleLabel.setForeground(TEXT_DARK);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(FONT_HEADER_SUB);
        subLabel.setForeground(TEXT_MUTED);

        textPanel.add(titleLabel);
        textPanel.add(javax.swing.Box.createVerticalStrut(4));
        textPanel.add(subLabel);

        header.add(textPanel, java.awt.BorderLayout.CENTER);
        return header;
    }

    /**
     * Styles text input fields for a consistent modern look.
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_REGULAR);
        textField.setForeground(TEXT_DARK);
        textField.setCaretColor(TEXT_DARK);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
    }
}

