package deusmatrix.gui;

import deusmatrix.controllers.GameOperationsController;
import deusmatrix.controllers.SudokuGameController;
import deusmatrix.dao.StatisticsDAO;
import deusmatrix.dao.UsersDAO;
import deusmatrix.models.GameDifficult;
import deusmatrix.models.GameField;
import deusmatrix.models.GameFieldSolver;
import deusmatrix.models.User;
import deusmatrix.utils.HibernateConfiguration;
import deusmatrix.utils.Logger;
import deusmatrix.utils.SupportFunctions;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;

public class GameWindow extends JFrame {
    private User user;
    private GameDifficult difficult;
    private SudokuGameController sudokuGameController;
    private GameOperationsController gameOperationsController;

    private GameField solvedGameField;

    private JLabel timerLabel;
    private JLabel attemptsLabel;
    private JLabel difficultyLabel;
    private JPanel sudokuPanel;
    private JButton[] numberButtons;
    private JButton hintButton;

    private JButton[][] cells = new JButton[GameField.FIELD_SIZE][GameField.FIELD_SIZE];

    private int selectedRow = -1;
    private int selectedColumn = -1;

    public GameWindow(User user, GameDifficult difficult) {
        this.user = user;
        this.difficult = difficult;

        initController();
        initUI();
    }

    private void initController() {
        UsersDAO usersDAO = new UsersDAO(HibernateConfiguration.getEntityManagerFactory());
        StatisticsDAO statisticsDAO = new StatisticsDAO(HibernateConfiguration.getEntityManagerFactory());

        gameOperationsController = new GameOperationsController(usersDAO, statisticsDAO);

        sudokuGameController = new SudokuGameController();
    }

    private void initUI() {
        Logger.getInstance().info("initializing Sudoku UI");

        setTitle("Deus Matrix — Sudoku");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new BorderLayout());

        addTopPanel();
        addCentralPanel();
        addBottomPanel();

        pack();
    }

    private void addTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(timerLabel, BorderLayout.WEST);

        difficultyLabel = new JLabel("Difficulty: ");
        difficultyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(difficultyLabel, BorderLayout.CENTER);

        attemptsLabel = new JLabel("Attempts: 0 / 3");
        attemptsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(attemptsLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void addCentralPanel() {
        sudokuPanel = new JPanel();
        sudokuPanel.setLayout(new GridBagLayout());

        addCentralPanelColumnsLabels();
        addCentralPanelRowsLabels();
        addCentralPanelColumnsRightBorder();
        addGameField();
    }

    private void addCentralPanelColumnsLabels() {
        for (int column = 0; column < GameField.FIELD_SIZE; column++) {
            JLabel columnLabel = new JLabel(String.valueOf(column + 1));
            columnLabel.setHorizontalAlignment(SwingConstants.CENTER);
            columnLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            addGridComponent(sudokuPanel, columnLabel, column + 1, 0);
        }
    }

    private void addCentralPanelRowsLabels() {
        for (int row = 0; row < GameField.FIELD_SIZE; row++) {
            JLabel rowLabel = new JLabel(String.valueOf(row + 1));
            rowLabel.setHorizontalAlignment(SwingConstants.CENTER);
            rowLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            addGridComponent(sudokuPanel, rowLabel, 0, row + 1);
        }
    }

    private void addCentralPanelColumnsRightBorder() {
        for (int row = 0; row < GameField.FIELD_SIZE; row++) {
            JLabel borderLabel = new JLabel(" ");
            borderLabel.setHorizontalAlignment(SwingConstants.CENTER);
            borderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            addGridComponent(sudokuPanel, borderLabel, GameField.FIELD_SIZE + 1, row + 1);
        }
    }

    private void addGameField() {
        for (int row = 0; row < GameField.FIELD_SIZE; row++) {
            for (int column = 0; column < GameField.FIELD_SIZE; column++) {
                JButton cell = new JButton(" ");
                cell.setFont(new Font("Arial", Font.BOLD, 20));
                cell.setFocusPainted(true);
                cell.setContentAreaFilled(true);
                cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                drawBlockBorders(cell, row, column);

                final int r = row;
                final int c = column;

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selectCell(r, c);
                    }
                });

                cells[row][column] = cell;
                addGridComponent(sudokuPanel, cell, column + 1, row + 1);
            }
        }

        add(sudokuPanel, BorderLayout.CENTER);
    }

    private void addBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        numberButtons = new JButton[GameField.FIELD_SIZE];
        for (int i = 0; i < GameField.FIELD_SIZE; i++) {
            final int value = i + 1;
            numberButtons[i] = new JButton(String.valueOf(value));
            numberButtons[i].addActionListener(e -> onNumberButtonClick(value));
            bottomPanel.add(numberButtons[i]);
        }

        hintButton = new JButton("Hint");
        hintButton.addActionListener(e -> showHint());
        bottomPanel.add(hintButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addGridComponent(JPanel panel, Component component, int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = x;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(2, 2, 2, 2);

        panel.add(component, gbc);
    }

    private void drawBlockBorders(JButton cell, int row, int column) {
        Color thinColor = Color.LIGHT_GRAY;
        Color thickColor = Color.BLACK;
        int thinWidth = 1;
        int thickWidth = 6;

        Border innerBorder = BorderFactory.createLineBorder(thinColor, thinWidth);

        int topInset = thinWidth;
        int leftInset = thinWidth;
        int bottomInset = thinWidth;
        int rightInset = thinWidth;

        boolean isBlockRightEdge = (column + 1) % GameField.BLOCKS_IN_LINE_COUNT == 0;
        boolean isBlockBottomEdge = (row + 1) % GameField.BLOCKS_IN_LINE_COUNT == 0;
        boolean isBlockLeftEdge = (column + 1) % GameField.BLOCKS_IN_LINE_COUNT == 1;
        boolean isBlockTopEdge = (row + 1) % GameField.BLOCKS_IN_LINE_COUNT == 1;

        if (isBlockRightEdge) {
            rightInset = thickWidth;
        }

        if (isBlockBottomEdge) {
            bottomInset = thickWidth;
        }

        if (isBlockLeftEdge) {
            leftInset = thickWidth;
        }

        if (isBlockTopEdge) {
            topInset = thickWidth;
        }

        if (column == 0) {
            leftInset = thickWidth;
        }

        if (row == 0) {
            topInset = thickWidth;
        }

        Border outerBorder = BorderFactory.createMatteBorder(topInset, leftInset, bottomInset, rightInset, thickColor);

        cell.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
    }

    private void selectCell(int row, int column) {
        selectedRow = row;
        selectedColumn = column;

        updateCellHighlighting();
    }

    private void updateCellHighlighting() {
        for (int row = 0; row < GameField.FIELD_SIZE; row++) {
            for (int column = 0; column < GameField.FIELD_SIZE; column++) {
                if (row == selectedRow || column == selectedColumn) {
                    cells[row][column].setSelected(true);
                } else {
                    cells[row][column].setSelected(false);
                }
            }
        }
    }

    public void setCellValue(int row, int column, int value) {
        if (row >= 0 && row < GameField.FIELD_SIZE && column >= 0 && column < GameField.FIELD_SIZE) {
            cells[row][column].setText(value > 0 ? String.valueOf(value) : " ");
        }
    }

    private void onNumberButtonClick(int value) {
        if (selectedRow != -1 && selectedColumn != -1) {
            setCellValue(selectedRow, selectedColumn, value);
            incrementAttempts();
        } else {
            SupportFunctions.showMessage("Select field cell!");
        }
    }

    private void incrementAttempts() {
        // TODO

        String currentText = attemptsLabel.getText();
        int attempts = Integer.parseInt(currentText.split(": ")[1].split(" / ")[0]);
        attempts++;
        attemptsLabel.setText("Attempts: " + attempts + " / 3");
    }

    private void showHint() {
        // TODO
    }

    private void setDifficulty(String difficulty) {
        difficultyLabel.setText("Difficulty: " + difficulty);
    }

    private void startTimer() {
        Timer timer = new Timer(1000, new ActionListener() {
            private int seconds = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                int minutes = seconds / 60;
                int secs = seconds % 60;

                timerLabel.setText("Time: " + String.format("%02d", minutes) + ":" + String.format("%02d", secs));
            }
        });

        timer.start();
    }

    public void create() {
        Logger.getInstance().info("launch");

        setDifficulty(this.difficult.toString());

        startTimer();

        SwingUtilities.invokeLater(() -> setVisible(true));

        GameField gameField = fillGameField();
        this.solvedGameField = solveField(gameField);
    }

    private GameField fillGameField() {
        GameField gameField = sudokuGameController.createGameField(difficult);

        for (int row = 0; row < GameField.FIELD_SIZE; row++) {
            for (int column = 0; column < GameField.FIELD_SIZE; column++) {
                int cellValue = gameField.getCellValue(row, column);

                if (cellValue != GameField.FIELD_EMPTY_VALUE) {
                    setCellValue(row, column, cellValue);
                }
            }
        }

        return gameField;
    }

    private GameField solveField(GameField gameField) {
        GameField solvedField = null;

        try {
            solvedField = gameField.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        GameFieldSolver solver = sudokuGameController.createGameFieldSolver(solvedField);
        solver.solve();

        return solvedField;
    }
}
