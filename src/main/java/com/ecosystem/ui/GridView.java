package com.ecosystem.ui;

import com.ecosystem.model.Cell;
import com.ecosystem.model.Ecosystem;
import com.ecosystem.model.Organism;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Custom Canvas component để render grid hệ sinh thái.
 * Hiển thị các sinh vật với màu sắc tương ứng.
 */
public class GridView extends Canvas {

    private Ecosystem ecosystem;
    private int cellSize = 15;
    private boolean showGrid = true;

    /**
     * Constructor với kích thước cố định.
     * 
     * @param width  Chiều rộng canvas
     * @param height Chiều cao canvas
     */
    public GridView(double width, double height) {
        super(width, height);
    }

    /**
     * Cập nhật ecosystem để render.
     * 
     * @param ecosystem Ecosystem mới
     */
    public void setEcosystem(Ecosystem ecosystem) {
        this.ecosystem = ecosystem;
        calculateCellSize();
        render();
    }

    /**
     * Tính toán kích thước cell dựa trên canvas và grid size.
     */
    private void calculateCellSize() {
        if (ecosystem == null)
            return;

        int gridWidth = ecosystem.getWidth();
        int gridHeight = ecosystem.getHeight();

        int cellWidth = (int) (getWidth() / gridWidth);
        int cellHeight = (int) (getHeight() / gridHeight);

        cellSize = Math.max(5, Math.min(cellWidth, cellHeight));
    }

    /**
     * Render toàn bộ grid.
     */
    public void render() {
        GraphicsContext gc = getGraphicsContext2D();

        // Clear canvas
        gc.setFill(Color.web("#1a1a2e"));
        gc.fillRect(0, 0, getWidth(), getHeight());

        if (ecosystem == null) {
            drawPlaceholder(gc);
            return;
        }

        int gridWidth = ecosystem.getWidth();
        int gridHeight = ecosystem.getHeight();

        // Tính offset để center grid
        double offsetX = (getWidth() - gridWidth * cellSize) / 2;
        double offsetY = (getHeight() - gridHeight * cellSize) / 2;

        // Vẽ background grid
        if (showGrid) {
            drawGridLines(gc, offsetX, offsetY, gridWidth, gridHeight);
        }

        // Vẽ các sinh vật
        Cell[][] grid = ecosystem.getGrid();
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                Cell cell = grid[x][y];
                Organism occupant = cell.getOccupant();

                if (occupant != null && occupant.isAlive()) {
                    drawOrganism(gc, occupant, offsetX + x * cellSize, offsetY + y * cellSize);
                }
            }
        }
    }

    /**
     * Vẽ placeholder khi chưa có ecosystem.
     */
    private void drawPlaceholder(GraphicsContext gc) {
        gc.setFill(Color.GRAY);
        gc.setFont(javafx.scene.text.Font.font(20));
        gc.fillText("Chọn kịch bản để bắt đầu mô phỏng",
                getWidth() / 2 - 150, getHeight() / 2);
    }

    /**
     * Vẽ các đường kẻ grid.
     */
    private void drawGridLines(GraphicsContext gc, double offsetX, double offsetY,
            int gridWidth, int gridHeight) {
        gc.setStroke(Color.web("#16213e"));
        gc.setLineWidth(0.5);

        // Đường dọc
        for (int x = 0; x <= gridWidth; x++) {
            gc.strokeLine(offsetX + x * cellSize, offsetY,
                    offsetX + x * cellSize, offsetY + gridHeight * cellSize);
        }

        // Đường ngang
        for (int y = 0; y <= gridHeight; y++) {
            gc.strokeLine(offsetX, offsetY + y * cellSize,
                    offsetX + gridWidth * cellSize, offsetY + y * cellSize);
        }
    }

    /**
     * Vẽ một sinh vật.
     */
    private void drawOrganism(GraphicsContext gc, Organism organism, double x, double y) {
        Color color = organism.getColor();

        // Vẽ hình tròn đại diện cho sinh vật
        double padding = cellSize * 0.1;
        double size = cellSize - padding * 2;

        gc.setFill(color);
        gc.fillOval(x + padding, y + padding, size, size);

        // Vẽ viền
        gc.setStroke(color.brighter());
        gc.setLineWidth(1);
        gc.strokeOval(x + padding, y + padding, size, size);
    }

    // === Getters và Setters ===

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = Math.max(5, Math.min(cellSize, 50));
        render();
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        render();
    }
}
