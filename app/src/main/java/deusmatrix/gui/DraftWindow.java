package deusmatrix.gui;

import deusmatrix.models.GameField;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;

public class DraftWindow extends JDialog {
    private boolean[][][] candidates;

    private JButton[][] cells = new JButton[GameField.FIELD_SIZE][GameField.FIELD_SIZE];

    private GameField gameField;

    private int selectedRow = -1;
    private int selectedColumn = -1;

    public DraftWindow(JFrame parent, GameField gameField) {
        super(parent, "Deus Matrix — Draft", false);

        this.gameField = gameField;

        candidates = new boolean[GameField.FIELD_SIZE][GameField.FIELD_SIZE][GameField.FIELD_SIZE];

        setSize(560, 660);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        addGrid();
        addControls();
        fillCells();
    }

    private void addGrid() {
        JPanel gridPanel = new JPanel(new GridBagLayout());

        for (int row = 0; row < GameField.FIELD_SIZE; row++) {
            for (int col = 0; col < GameField.FIELD_SIZE; col++) {
                JButton cell = new JButton();
                cell.setFont(new Font("Arial", Font.PLAIN, 10));
                cell.setPreferredSize(new Dimension(52, 52));
                cell.setVerticalTextPosition(SwingConstants.CENTER);
                cell.setHorizontalTextPosition(SwingConstants.CENTER);

                drawBlockBorders(cell, row, col);

                final int r = row;
                final int c = col;
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selectCell(r, c);
                    }
                });

                cells[row][col] = cell;

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = col;
                gbc.gridy = row;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gridPanel.add(cell, gbc);
            }
        }

        add(gridPanel, BorderLayout.CENTER);
    }

    private void addControls() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));

        for (int i = 0; i < GameField.FIELD_SIZE; i++) {
            final int value = i + 1;
            JButton btn = new JButton(String.valueOf(value));
            btn.setPreferredSize(new Dimension(45, 40));
            btn.addActionListener(e -> toggleCandidate(btn, value));
            bottomPanel.add(btn);
        }

        JButton clearButton = new JButton("Clear");
        clearButton.setPreferredSize(new Dimension(80, 40));
        clearButton.addActionListener(e -> clearSelectedCell(clearButton));
        bottomPanel.add(clearButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void selectCell(int row, int col) {
        selectedRow = row;
        selectedColumn = col;
        updateHighlighting();
    }

    private void updateHighlighting() {
        for (int row = 0; row < GameField.FIELD_SIZE; row++) {
            for (int col = 0; col < GameField.FIELD_SIZE; col++) {
                cells[row][col].setSelected(row == selectedRow || col == selectedColumn);
            }
        }
    }

    private void toggleCandidate(JButton button, int value) {
        if (selectedRow == -1 || selectedColumn == -1 || !button.isEnabled()) return;

        candidates[selectedRow][selectedColumn][value - 1] = !candidates[selectedRow][selectedColumn][value - 1];

        updateCellDisplay(selectedRow, selectedColumn);
    }

    private void clearSelectedCell(JButton button) {
        if (selectedRow == -1 || selectedColumn == -1 || !button.isEnabled()) return;

        for (int i = 0; i < GameField.FIELD_SIZE; i++) {
            candidates[selectedRow][selectedColumn][i] = false;
        }
        updateCellDisplay(selectedRow, selectedColumn);
    }

    private void updateCellDisplay(int row, int col) {
        if (!cells[row][col].isEnabled()) {
            return;
        }

        StringBuilder sb = new StringBuilder("<html><div style='text-align:center;font-size:9px'>");
        for (int i = 0; i < GameField.FIELD_SIZE; i++) {
            if (candidates[row][col][i]) {
                sb.append(i + 1);
            } else {
                sb.append("&nbsp;");
            }
            if ((i + 1) % 3 == 0 && i != 8) {
                sb.append("<br>");
            } else {
                sb.append("&nbsp;");
            }
        }
        sb.append("</div></html>");
        cells[row][col].setText(sb.toString());
    }

    public void onNumberPlaced(int placedRow, int placedCol, int placedValue) {
        for (int c = 0; c < GameField.FIELD_SIZE; c++) {
            if (candidates[placedRow][c][placedValue - 1]) {
                candidates[placedRow][c][placedValue - 1] = false;
                updateCellDisplay(placedRow, c);
            }
        }
        for (int r = 0; r < GameField.FIELD_SIZE; r++) {
            if (candidates[r][placedCol][placedValue - 1]) {
                candidates[r][placedCol][placedValue - 1] = false;
                updateCellDisplay(r, placedCol);
            }
        }
        int blockRow = (placedRow / GameField.BLOCKS_IN_LINE_COUNT) * GameField.NUMS_IN_BLOCK_COUNT;
        int blockCol = (placedCol / GameField.BLOCKS_IN_LINE_COUNT) * GameField.NUMS_IN_BLOCK_COUNT;
        for (int r = blockRow; r < blockRow + GameField.NUMS_IN_BLOCK_COUNT; r++) {
            for (int c = blockCol; c < blockCol + GameField.NUMS_IN_BLOCK_COUNT; c++) {
                if (candidates[r][c][placedValue - 1]) {
                    candidates[r][c][placedValue - 1] = false;
                    updateCellDisplay(r, c);
                }
            }
        }
    }

    public void lockCell(int row, int col) {
        for (int i = 0; i < GameField.FIELD_SIZE; i++) {
            candidates[row][col][i] = false;
        }
        cells[row][col].setText("");
        cells[row][col].setEnabled(false);
    }

    public void unlockCell(int row, int col) {
        cells[row][col].setEnabled(true);
    }

    private void drawBlockBorders(JButton cell, int row, int column) {
        Color thinColor = Color.LIGHT_GRAY;
        Color thickColor = Color.BLACK;
        int thinWidth = 1;
        int thickWidth = 3;

        int top = thinWidth, left = thinWidth, bottom = thinWidth, right = thinWidth;

        if ((column + 1) % GameField.BLOCKS_IN_LINE_COUNT == 0) right = thickWidth;
        if ((row + 1) % GameField.BLOCKS_IN_LINE_COUNT == 0) bottom = thickWidth;
        if ((column + 1) % GameField.BLOCKS_IN_LINE_COUNT == 1) left = thickWidth;
        if ((row + 1) % GameField.BLOCKS_IN_LINE_COUNT == 1) top = thickWidth;
        if (column == 0) left = thickWidth;
        if (row == 0) top = thickWidth;

        Border outer = BorderFactory.createMatteBorder(top, left, bottom, right, thickColor);
        Border inner = BorderFactory.createLineBorder(thinColor, thinWidth);
        cell.setBorder(BorderFactory.createCompoundBorder(outer, inner));
    }

    private void fillCells() {
        for (int row = 0; row < GameField.FIELD_SIZE; row++) {
            for (int col = 0; col < GameField.FIELD_SIZE; col++) {
                if (this.gameField.getCellValue(row, col) != GameField.FIELD_EMPTY_VALUE) {
                    String valueRepresentation = Integer.toString(this.gameField.getCellValue(row, col));

                    cells[row][col].setText("<html><div style='text-align:center;font-size:20px;font-weight:bold'>"
                            + valueRepresentation
                            + "</div></html>");
                    cells[row][col].setEnabled(false);
                }
            }
        }
    }
}
