package com.ecosystem.model;

import com.ecosystem.interfaces.Movable;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;

/**
 * Abstract class đại diện cho sinh vật tiêu thụ (Consumer) trong hệ sinh thái.
 * 
 * Consumer ăn các sinh vật khác để lấy năng lượng.
 * Chúng di chuyển và mất năng lượng theo thời gian.
 * 
 * Áp dụng Inheritance: Herbivore và Carnivore kế thừa từ class này.
 */
public abstract class Consumer extends Organism implements Movable {

    protected static final Random RANDOM = new Random();

    // Tốc độ mất năng lượng mỗi tick
    protected final double hungerRate;

    // Tầm nhìn để tìm thức ăn (số ô)
    protected final int visionRange;

    // Tốc độ di chuyển (số ô mỗi tick)
    protected final int speed;

    // Configuration reference
    protected final EcosystemConfig config;

    /**
     * Constructor cho Consumer.
     * 
     * @param name        Tên loại sinh vật
     * @param energy      Năng lượng ban đầu
     * @param x           Vị trí x trên grid
     * @param y           Vị trí y trên grid
     * @param hungerRate  Tốc độ mất năng lượng
     * @param visionRange Tầm nhìn
     * @param speed       Tốc độ di chuyển
     * @param config      Cấu hình hệ sinh thái
     */
    protected Consumer(String name, double energy, int x, int y,
            double hungerRate, int visionRange, int speed,
            EcosystemConfig config) {
        super(name, energy, x, y);
        this.hungerRate = hungerRate;
        this.visionRange = visionRange;
        this.speed = speed;
        this.config = config;
    }

    /**
     * Tìm thức ăn trong tầm nhìn.
     * Herbivore tìm Producer, Carnivore tìm Herbivore.
     * 
     * @param ecosystem Hệ sinh thái để tìm kiếm
     * @return Sinh vật thức ăn gần nhất, hoặc null nếu không tìm thấy
     */
    public abstract Organism findFood(Ecosystem ecosystem);

    /**
     * Ăn một sinh vật, nhận năng lượng theo 10% rule.
     * 
     * @param food Sinh vật bị ăn
     */
    public void eat(Organism food) {
        if (food != null && food.isAlive() && food.isEdible()) {
            // Nhận 10% năng lượng của con mồi
            double gainedEnergy = food.getEnergyValue();
            addEnergy(gainedEnergy);

            // Con mồi bị tiêu thụ (chết)
            food.beConsumed();
        }
    }

    /**
     * Mất năng lượng do hoạt động sống (hunger).
     * Nếu năng lượng <= 0, sinh vật chết.
     */
    public void loseEnergy() {
        reduceEnergy(hungerRate);
    }

    /**
     * Di chuyển về phía mục tiêu.
     * 
     * @param targetX   Vị trí x đích
     * @param targetY   Vị trí y đích
     * @param ecosystem Hệ sinh thái
     * @return true nếu di chuyển thành công
     */
    protected boolean moveTowards(int targetX, int targetY, Ecosystem ecosystem) {
        int dx = Integer.compare(targetX, x);
        int dy = Integer.compare(targetY, y);

        int newX = x + dx;
        int newY = y + dy;

        // Kiểm tra giới hạn grid
        if (newX >= 0 && newX < ecosystem.getWidth() &&
                newY >= 0 && newY < ecosystem.getHeight()) {

            Cell currentCell = ecosystem.getCell(x, y);
            Cell targetCell = ecosystem.getCell(newX, newY);

            // Chỉ di chuyển nếu ô đích trống hoặc có thức ăn
            if (targetCell.isEmpty() || canEatOccupant(targetCell)) {
                currentCell.clear();
                setPosition(newX, newY);
                targetCell.setOccupant(this);
                return true;
            }
        }
        return false;
    }

    /**
     * Di chuyển ngẫu nhiên.
     * 
     * @param ecosystem Hệ sinh thái
     * @return true nếu di chuyển thành công
     */
    protected boolean moveRandomly(Ecosystem ecosystem) {
        // 8 hướng có thể di chuyển
        int[] dx = { -1, 0, 1, -1, 1, -1, 0, 1 };
        int[] dy = { -1, -1, -1, 0, 0, 1, 1, 1 };

        // Shuffle hướng để chọn ngẫu nhiên
        List<Integer> indices = new java.util.ArrayList<>();
        for (int i = 0; i < 8; i++)
            indices.add(i);
        java.util.Collections.shuffle(indices);

        for (int idx : indices) {
            int newX = x + dx[idx];
            int newY = y + dy[idx];

            if (newX >= 0 && newX < ecosystem.getWidth() &&
                    newY >= 0 && newY < ecosystem.getHeight()) {

                Cell targetCell = ecosystem.getCell(newX, newY);
                if (targetCell.isEmpty()) {
                    Cell currentCell = ecosystem.getCell(x, y);
                    currentCell.clear();
                    setPosition(newX, newY);
                    targetCell.setOccupant(this);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Kiểm tra xem có thể ăn sinh vật trong ô đích không.
     * 
     * @param cell Ô đích
     * @return true nếu có thể ăn
     */
    protected abstract boolean canEatOccupant(Cell cell);

    /**
     * Tính khoảng cách Manhattan giữa hai điểm.
     * 
     * @param x1 Tọa độ x điểm 1
     * @param y1 Tọa độ y điểm 1
     * @param x2 Tọa độ x điểm 2
     * @param y2 Tọa độ y điểm 2
     * @return Khoảng cách Manhattan
     */
    protected int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    // === Movable Interface Implementation ===

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public int getVisionRange() {
        return visionRange;
    }

    // === Getters ===

    public double getHungerRate() {
        return hungerRate;
    }
}
