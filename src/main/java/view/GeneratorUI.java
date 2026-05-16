package view;

import entities.Board;
import interface_adapters.IObtainable;
import utilities.BoardGenerator;
import utilities.GoalParser;

import javax.swing.*;
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
            new SpinnerNumberModel(4, 1, 8, 1)
    );

    private final JCheckBox optionOne = new JCheckBox("Increase major ability chance");
    private final JCheckBox optionTwo = new JCheckBox("Force geo limitations");
    private final JCheckBox optionThree = new JCheckBox("Force \"fun\" middle square");
    private final JCheckBox optionFour = new JCheckBox("Prevent lines requiring multiple saves");

    private final JTextArea resultArea = new JTextArea();

    public GeneratorUI() {
        super("Galaxy Board Generator");

        URL iconStream = getClass()
                .getClassLoader()
                .getResource("galaxy_icon.png");
        ImageIcon icon = new ImageIcon(iconStream);
        this.setIconImage(icon.getImage());

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        optionThree.setEnabled(false);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Settings"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Seed:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(seedField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Player Count:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(playerCountSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        inputPanel.add(optionOne, gbc);

        gbc.gridy = 3;
        inputPanel.add(optionTwo, gbc);

        gbc.gridy = 4;
        inputPanel.add(optionThree, gbc);

        gbc.gridy = 5;
        inputPanel.add(optionFour, gbc);

        JButton generateButton = new JButton("Generate");
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(generateButton, gbc);

        JTextArea credits = new JTextArea("Special thanks to choombert and python for help with generation logic, and wunder for rigorous testing :)");
        credits.setEditable(false);
        credits.setLineWrap(true);
        credits.setColumns(15);
        credits.setEnabled(false);
        credits.setOpaque(false);
        credits.setDisabledTextColor(Color.BLACK);
        credits.setWrapStyleWord(true);
        credits.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        gbc.gridy = 15;
        inputPanel.add(credits, gbc);

        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setBorder(BorderFactory.createTitledBorder("Result"));

        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.WEST);
        add(resultScrollPane, BorderLayout.CENTER);

        generateButton.addActionListener(e -> generateResult());
        resultArea.setCaretColor(Color.WHITE);
        resultArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resultArea.addMouseListener(new CopyListener());
    }

    private void generateResult() {
        int seed = 0;

        try{
            seed = Integer.parseInt(seedField.getText());
        } catch (NumberFormatException e) {
            seed = seedField.getText().hashCode();
        }

        int playerCount = (int) playerCountSpinner.getValue();

        if (playerCount == 1 || playerCount == 2 || playerCount == 4) {
            playerCountSpinner.setBackground(Color.GRAY);
        } else {
            playerCountSpinner.setBackground(Color.RED);
        }
        boolean majorChance = optionOne.isSelected();
        boolean geoLimitation = optionTwo.isSelected();
        boolean distributeMajors = optionThree.isSelected();
        boolean preventMultipleSaves = optionFour.isSelected();

        if (geoLimitation) {
            BoardGenerator.GEO_LIMIT_CHANCE = 1;
        } else {
            BoardGenerator.GEO_LIMIT_CHANCE = -1;
        }

        BoardGenerator.INCREASE_MAJOR_CHANCE = majorChance;
        BoardGenerator.PREVENT_MULTIPLE_SAVES = preventMultipleSaves;

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream("hollow_knight_goals.json");

        if (inputStream == null) {
            throw new RuntimeException("Resource not found");
        }

        ArrayList<IObtainable> goals = GoalParser.parseGoals(inputStream);
        Board board = BoardGenerator.generateBoardRobin(goals, seed);

        resultArea.setText( board.generateBoardJSON() );
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