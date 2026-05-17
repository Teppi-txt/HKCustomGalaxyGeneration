package view;

import entities.Board;
import entities.Objective;
import entities.SkipSettings;
import interface_adapters.IObtainable;
import utilities.BoardGenerator;
import utilities.GoalParser;
import utilities.GoalUtility;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class GeneratorUI extends JFrame {

    private final JTextField seedField = new JTextField("0", 15);

    private final JSpinner playerCountSpinner = new JSpinner(
            new SpinnerNumberModel(4, 1, 4, 3)
    );

    private final JCheckBox optionOne = new JCheckBox("Increase major ability chance");
    private final JCheckBox optionTwo = new JCheckBox("Force geo limitations");

    private final JCheckBox optionThree = new JCheckBox("Use custom center square");
    private final JComboBox<String> centerSquareDropdown = new JComboBox<>();

    private final JCheckBox optionFour = new JCheckBox("Prevent lines requiring multiple saves");

    private final JCheckBox darkroomsOn = new JCheckBox("Darkrooms");
    private final JCheckBox hardSkips = new JCheckBox("Hard Skips");
    private final JCheckBox extremeSkips = new JCheckBox("Extreme Skips");

    ArrayList<IObtainable> goals;

    private final JTextArea resultArea = new JTextArea();

    public GeneratorUI() {
        super("Galaxy Board Generator");

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream("hollow_knight_goals.json");

        if (inputStream == null) {
            throw new RuntimeException("Resource not found");
        }
        goals = GoalParser.parseGoals(inputStream, getSettings());

        refreshCenterSquareDropdown(goals);

        setIcon();
        configureWindow();

        JButton generateButton = new JButton("Generate");

        JPanel leftPanel = createLeftPanel(generateButton);
        JScrollPane resultScrollPane = createResultPanel();

        setLayout(new BorderLayout(0, 15));
        add(leftPanel, BorderLayout.WEST);
        add(resultScrollPane, BorderLayout.CENTER);

        generateButton.addActionListener(e -> generateResult());
        resultArea.addMouseListener(new CopyListener());
    }


    private void setIcon() {
        URL iconStream = getClass()
                .getClassLoader()
                .getResource("galaxy_icon.png");

        assert iconStream != null;
        ImageIcon icon = new ImageIcon(iconStream);
        this.setIconImage(icon.getImage());
    }

    private void configureWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(950, 700);
        setLocationRelativeTo(null);
    }

    private JPanel createCenterSquarePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        centerSquareDropdown.setEnabled(false);

        optionThree.addActionListener(e ->
                centerSquareDropdown.setEnabled(optionThree.isSelected())
        );

        panel.add(optionThree, BorderLayout.NORTH);
        panel.add(centerSquareDropdown, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLeftPanel(JButton generateButton) {
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBorder(new EmptyBorder(6, 6, 6, 6));

        leftPanel.add(createMenusPanel(generateButton), BorderLayout.NORTH);
        leftPanel.add(createCreditsPanel(), BorderLayout.SOUTH);

        URL grubImage = getClass()
                .getClassLoader()
                .getResource("grub.png");

        Image icon = new ImageIcon(grubImage).getImage().getScaledInstance(200, 200, Image.SCALE_FAST);
        JLabel picLabel = new JLabel(new ImageIcon(icon));
        picLabel.setPreferredSize(new Dimension(200, 200));
        picLabel.setOpaque(false);
        //leftPanel.add(picLabel);

        return leftPanel;
    }

    private JPanel createMenusPanel(JButton generateButton) {
        JPanel menusPanel = new JPanel();
        menusPanel.setLayout(new BoxLayout(menusPanel, BoxLayout.Y_AXIS));
        menusPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel settingsPanel = createSettingsPanel();
        JPanel skipSettingsPanel = createSkipSettingsPanel();
        JPanel buttonPanel = createButtonPanel(generateButton);

        settingsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        skipSettingsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        menusPanel.add(settingsPanel);
        menusPanel.add(Box.createVerticalStrut(15));
        menusPanel.add(skipSettingsPanel);
        menusPanel.add(Box.createVerticalStrut(15));
        menusPanel.add(buttonPanel);

        return menusPanel;
    }

    private JPanel createSettingsPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Settings"));

        GridBagConstraints gbc = createGbc(10, 12, 10, 12);

        addSettingRow(inputPanel, new JLabel("Seed:"), seedField, gbc, 0);
        addSettingRow(inputPanel, new JLabel("Player Count:"), playerCountSpinner, gbc, 1);

        addFullWidth(inputPanel, optionOne, gbc, 2);
        addFullWidth(inputPanel, optionTwo, gbc, 3);
        addFullWidth(inputPanel, createCenterSquarePanel(), gbc, 4);
        addFullWidth(inputPanel, optionFour, gbc, 5);

        return inputPanel;
    }

    private JPanel createSkipSettingsPanel() {
        JPanel skipSettingsPanel = new JPanel(new GridBagLayout());
        skipSettingsPanel.setBorder(BorderFactory.createTitledBorder("Skip Settings"));

        GridBagConstraints gbc = createGbc(4, 12, 4, 12);

        gbc.gridy = 0;
        gbc.gridx = 0;
        skipSettingsPanel.add(darkroomsOn, gbc);

        gbc.gridx = 1;
        skipSettingsPanel.add(hardSkips, gbc);

        gbc.gridy = 1;
        skipSettingsPanel.add(extremeSkips, gbc);

        return skipSettingsPanel;
    }

    private JPanel createButtonPanel(JButton generateButton) {
        generateButton.setPreferredSize(new Dimension(160, 40));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        buttonPanel.add(generateButton);

        return buttonPanel;
    }

    private JPanel createCreditsPanel() {
        JTextArea credits = new JTextArea(
                "Special thanks to choombert and python for help with generation logic, and wunder for testing :)"
        );

        credits.setEditable(false);
        credits.setLineWrap(true);
        credits.setWrapStyleWord(true);
        credits.setOpaque(false);
        credits.setEnabled(false);
        credits.setDisabledTextColor(Color.DARK_GRAY);
        credits.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        credits.setBorder(new EmptyBorder(10, 10, 10, 10));
        credits.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel creditsPanel = new JPanel(new BorderLayout());
        creditsPanel.setBorder(new EmptyBorder(10, 5, 5, 5));
        creditsPanel.add(credits, BorderLayout.CENTER);

        return creditsPanel;
    }

    private JScrollPane createResultPanel() {
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        resultArea.setCaretColor(Color.WHITE);
        resultArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        resultScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        new EmptyBorder(10, 10, 10, 10),
                        BorderFactory.createTitledBorder("Result")
                )
        );

        return resultScrollPane;
    }

    private GridBagConstraints createGbc(int top, int left, int bottom, int right) {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(top, left, bottom, right);
        gbc.anchor = GridBagConstraints.WEST;

        return gbc;
    }

    private void addSettingRow(
            JPanel panel,
            JComponent label,
            JComponent field,
            GridBagConstraints gbc,
            int row
    ) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private void addFullWidth(
            JPanel panel,
            JComponent component,
            GridBagConstraints gbc,
            int row
    ) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(component, gbc);
    }

    private void generateResult() {
        int seed = getSeed();
        int playerCount = (int) playerCountSpinner.getValue();
        String customCenter = null;

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream("hollow_knight_goals.json");

        if (inputStream == null) {
            throw new RuntimeException("Resource not found");
        }

        // do it again, with the new settings
        goals = GoalParser.parseGoals(inputStream, getSettings());


        updatePlayerCountColor(playerCount);
        applyGeneratorSettings();

        if (optionThree.isSelected()) {
            customCenter = (String) centerSquareDropdown.getSelectedItem();
        }

        Board board;
        if (playerCount == 1) {
            board = BoardGenerator.generateRGO(goals, seed);
        } else {
            board = BoardGenerator.generateBoardRobin(goals, seed, customCenter);
        }

        resultArea.setText(board.generateBoardJSON());
    }

    private void refreshCenterSquareDropdown(ArrayList<IObtainable> goals) {
        Object selected = centerSquareDropdown.getSelectedItem();

        centerSquareDropdown.removeAllItems();

        for (IObtainable goal : goals) {
            if (goal instanceof Objective) {
                continue;
            }
            centerSquareDropdown.addItem(goal.getName());
        }

        if (selected != null) {
            centerSquareDropdown.setSelectedItem(selected);
        }
    }

    private SkipSettings getSettings() {
        SkipSettings settings = new SkipSettings();
        settings.setDarkrooms(darkroomsOn.isSelected());

        if (hardSkips.isSelected()) {
            settings.addSetting("hard");
        }
        if (extremeSkips.isSelected()) {
            settings.addSetting("extreme");
        }
        return settings;
    }

    private int getSeed() {
        try {
            return Integer.parseInt(seedField.getText());
        } catch (NumberFormatException e) {
            return seedField.getText().hashCode();
        }
    }

    private void updatePlayerCountColor(int playerCount) {
        if (playerCount == 1 || playerCount == 2 || playerCount == 4) {
            playerCountSpinner.setBackground(Color.GRAY);
        } else {
            playerCountSpinner.setBackground(Color.RED);
        }
    }

    private void applyGeneratorSettings() {
        boolean geoLimitation = optionTwo.isSelected();

        // this is terrible i know
        BoardGenerator.GEO_LIMIT_CHANCE = geoLimitation ? 1 : -1;
        BoardGenerator.INCREASE_MAJOR_CHANCE = optionOne.isSelected();
        BoardGenerator.PREVENT_MULTIPLE_SAVES = optionFour.isSelected();
    }

    private class CopyListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            StringSelection selection = new StringSelection(resultArea.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GeneratorUI().setVisible(true));
    }
}