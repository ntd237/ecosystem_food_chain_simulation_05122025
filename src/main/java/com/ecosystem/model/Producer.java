package com.ecosystem.model;

import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;

/**
 * Class đại diện cho sinh vật sản xuất (thực vật) trong hệ sinh thái.
 * 
 * Producer thực hiện quang hợp để tạo năng lượng từ ánh sáng mặt trời.
 * Chúng không di chuyển và là nguồn thức ăn cho Herbivore.
 * 
 * Behavioral Polymorphism:
 * - update(): Quang hợp tăng năng lượng, có thể sinh sản
 * - getColor(): Trả về màu xanh lá (Green)
 * - reproduce(): Tạo Producer mới ở ô lân cận
 */
public class Producer extends Organism {

    private static final Random RANDOM = new Random();

    // Tốc độ quang hợp (năng lượng/tick)
    private final double photosynthesisRate;

    // Năng lượng tối đa
    private final double maxEnergy;

    /**
     * Constructor cho Producer.
     * 
     * @param x      Vị trí x trên grid
     * @param y      Vị trí y trên grid
     * @param config Cấu hình hệ sinh thái
     */
    public Producer(int x, int y, EcosystemConfig config) {
        super("Producer", config.getProducerInitialEnergy(), x, y);
        this.photosynthesisRate = config.getProducerPhotosynthesis();
        this.maxEnergy = config.getProducerMaxEnergy();
        this.reproductionThreshold = config.getProducerReproductionThreshold();
        this.reproductionCost = config.getProducerReproductionCost();
    }

    /**
     * Constructor với năng lượng tùy chỉnh (dùng khi sinh sản).
     * 
     * @param x             Vị trí x trên grid
     * @param y             Vị trí y trên grid
     * @param initialEnergy Năng lượng ban đầu
     * @param config        Cấu hình hệ sinh thái
     */
    public Producer(int x, int y, double initialEnergy, EcosystemConfig config) {
        super("Producer", initialEnergy, x, y);
        this.photosynthesisRate = config.getProducerPhotosynthesis();
        this.maxEnergy = config.getProducerMaxEnergy();
        this.reproductionThreshold = config.getProducerReproductionThreshold();
        this.reproductionCost = config.getProducerReproductionCost();
    }

    /**
     * Thực hiện quang hợp để tăng năng lượng.
     * Năng lượng không vượt quá giới hạn maxEnergy.
     */
    public void photosynthesize() {
        if (alive) {
            energy = Math.min(energy + photosynthesisRate, maxEnergy);
        }
    }

    /**
     * Cập nhật trạng thái Producer mỗi tick.
     * - Quang hợp tăng năng lượng
     * - Kiểm tra và thực hiện sinh sản nếu đủ điều kiện
     * 
     * @param ecosystem Hệ sinh thái để tương tác
     */
    @Override
    public void update(Ecosystem ecosystem) {
        if (!alive)
            return;

        // Quang hợp
        photosynthesize();

        // Tăng tuổi
        incrementAge();

        // Kiểm tra sinh sản
        if (canReproduce()) {
            Organism offspring = reproduce();
            if (offspring != null) {
                // Tìm ô trống lân cận để đặt con
                List<Cell> emptyNeighbors = ecosystem.getEmptyNeighbors(x, y);
                if (!emptyNeighbors.isEmpty()) {
                    Cell targetCell = emptyNeighbors.get(RANDOM.nextInt(emptyNeighbors.size()));
                    offspring.setPosition(targetCell.getX(), targetCell.getY());
                    ecosystem.addOrganism(offspring);
                }
            }
        }
    }

    /**
     * Trả về màu xanh lá đại diện cho Producer.
     * Độ đậm của màu tỷ lệ với năng lượng.
     * 
     * @return Màu xanh lá với độ đậm dựa trên năng lượng
     */
    @Override
    public Color getColor() {
        // Tính độ đậm dựa trên năng lượng (0.3 - 1.0)
        double intensity = 0.3 + (energy / maxEnergy) * 0.7;
        return Color.color(0, intensity, 0);
    }

    /**
     * Trả về ký tự đại diện cho Producer.
     * 
     * @return Ký tự "🌿" hoặc "P"
     */
    @Override
    public String getSymbol() {
        return "🌿";
    }

    /**
     * Thực hiện sinh sản tạo Producer mới.
     * Chi phí sinh sản được trừ từ năng lượng của cây mẹ.
     * 
     * @return Producer mới, hoặc null nếu không thể sinh sản
     */
    @Override
    public Organism reproduce() {
        if (!canReproduce()) {
            return null;
        }

        // Trừ chi phí sinh sản
        reduceEnergy(reproductionCost);

        // Tạo Producer con với năng lượng = chi phí sinh sản / 2
        // Vị trí sẽ được cập nhật sau bởi Ecosystem
        return new Producer(x, y, reproductionCost / 2,
                EcosystemConfig.builder()
                        .producerEnergy(reproductionCost / 2, maxEnergy, photosynthesisRate)
                        .producerReproduction(reproductionThreshold, reproductionCost)
                        .build());
    }

    // === Getters ===

    public double getPhotosynthesisRate() {
        return photosynthesisRate;
    }

    public double getMaxEnergy() {
        return maxEnergy;
    }
}
