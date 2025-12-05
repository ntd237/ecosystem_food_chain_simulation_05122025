package com.ecosystem.model;

import javafx.scene.paint.Color;

import java.util.List;

/**
 * Class đại diện cho động vật ăn cỏ (Herbivore) trong hệ sinh thái.
 * 
 * Herbivore ăn Producer (thực vật) để lấy năng lượng.
 * Chúng di chuyển tìm kiếm thức ăn và sinh sản khi đủ năng lượng.
 * 
 * Behavioral Polymorphism:
 * - update(): Di chuyển, ăn Producer, mất năng lượng, sinh sản
 * - getColor(): Trả về màu xanh dương (Blue)
 * - findFood(): Tìm Producer gần nhất trong tầm nhìn
 * - move(): Di chuyển về phía Producer hoặc ngẫu nhiên
 */
public class Herbivore extends Consumer {

    /**
     * Constructor cho Herbivore.
     * 
     * @param x      Vị trí x trên grid
     * @param y      Vị trí y trên grid
     * @param config Cấu hình hệ sinh thái
     */
    public Herbivore(int x, int y, EcosystemConfig config) {
        super("Herbivore",
                config.getHerbivoreInitialEnergy(),
                x, y,
                config.getHerbivoreHungerRate(),
                config.getHerbivoreVision(),
                config.getHerbivoreSpeed(),
                config);
        this.reproductionThreshold = config.getHerbivoreReproductionThreshold();
        this.reproductionCost = config.getHerbivoreReproductionCost();
    }

    /**
     * Constructor với năng lượng tùy chỉnh (dùng khi sinh sản).
     * 
     * @param x             Vị trí x trên grid
     * @param y             Vị trí y trên grid
     * @param initialEnergy Năng lượng ban đầu
     * @param config        Cấu hình hệ sinh thái
     */
    public Herbivore(int x, int y, double initialEnergy, EcosystemConfig config) {
        super("Herbivore",
                initialEnergy,
                x, y,
                config.getHerbivoreHungerRate(),
                config.getHerbivoreVision(),
                config.getHerbivoreSpeed(),
                config);
        this.reproductionThreshold = config.getHerbivoreReproductionThreshold();
        this.reproductionCost = config.getHerbivoreReproductionCost();
    }

    /**
     * Cập nhật trạng thái Herbivore mỗi tick.
     * - Mất năng lượng do hunger
     * - Tìm và di chuyển về phía Producer
     * - Ăn Producer nếu ở cùng vị trí
     * - Sinh sản nếu đủ năng lượng
     * 
     * @param ecosystem Hệ sinh thái để tương tác
     */
    @Override
    public void update(Ecosystem ecosystem) {
        if (!alive)
            return;

        // Mất năng lượng do hunger
        loseEnergy();
        if (!alive)
            return;

        // Tìm thức ăn (Producer) gần nhất
        Organism food = findFood(ecosystem);

        if (food != null) {
            int distance = manhattanDistance(x, y, food.getX(), food.getY());

            if (distance <= 1) {
                // Ở ngay cạnh thức ăn -> ăn
                eat(food);
                ecosystem.removeOrganism(food);
            } else {
                // Di chuyển về phía thức ăn
                move(ecosystem);
            }
        } else {
            // Không tìm thấy thức ăn -> di chuyển ngẫu nhiên
            moveRandomly(ecosystem);
        }

        // Tăng tuổi
        incrementAge();

        // Kiểm tra sinh sản
        if (canReproduce()) {
            Organism offspring = reproduce();
            if (offspring != null) {
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
     * Di chuyển Herbivore trong hệ sinh thái.
     * Ưu tiên di chuyển về phía Producer gần nhất.
     * 
     * @param ecosystem Hệ sinh thái
     */
    @Override
    public void move(Ecosystem ecosystem) {
        Organism food = findFood(ecosystem);

        if (food != null) {
            // Di chuyển về phía thức ăn
            moveTowards(food.getX(), food.getY(), ecosystem);
        } else {
            // Di chuyển ngẫu nhiên
            moveRandomly(ecosystem);
        }
    }

    /**
     * Tìm Producer gần nhất trong tầm nhìn.
     * 
     * @param ecosystem Hệ sinh thái để tìm kiếm
     * @return Producer gần nhất, hoặc null nếu không tìm thấy
     */
    @Override
    public Organism findFood(Ecosystem ecosystem) {
        Producer nearestFood = null;
        int minDistance = Integer.MAX_VALUE;

        List<Producer> producers = ecosystem.getProducers();

        for (Producer producer : producers) {
            if (producer.isAlive() && producer.isEdible()) {
                int distance = manhattanDistance(x, y, producer.getX(), producer.getY());

                if (distance <= visionRange && distance < minDistance) {
                    minDistance = distance;
                    nearestFood = producer;
                }
            }
        }

        return nearestFood;
    }

    /**
     * Kiểm tra xem có thể ăn sinh vật trong ô đích không.
     * Herbivore chỉ ăn Producer.
     * 
     * @param cell Ô đích
     * @return true nếu ô chứa Producer
     */
    @Override
    protected boolean canEatOccupant(Cell cell) {
        return cell.hasProducer();
    }

    /**
     * Trả về màu xanh dương đại diện cho Herbivore.
     * Độ đậm tỷ lệ với năng lượng.
     * 
     * @return Màu xanh dương
     */
    @Override
    public Color getColor() {
        // Tính độ đậm dựa trên năng lượng (0.3 - 1.0)
        double maxEnergy = reproductionThreshold * 1.5;
        double intensity = 0.3 + (Math.min(energy, maxEnergy) / maxEnergy) * 0.7;
        return Color.color(0, 0, intensity);
    }

    /**
     * Trả về ký tự đại diện cho Herbivore.
     * 
     * @return Ký tự "🐰" hoặc "H"
     */
    @Override
    public String getSymbol() {
        return "🐰";
    }

    /**
     * Thực hiện sinh sản tạo Herbivore mới.
     * 
     * @return Herbivore mới, hoặc null nếu không thể sinh sản
     */
    @Override
    public Organism reproduce() {
        if (!canReproduce()) {
            return null;
        }

        // Trừ chi phí sinh sản
        reduceEnergy(reproductionCost);

        // Tạo Herbivore con với năng lượng = chi phí sinh sản / 2
        return new Herbivore(x, y, reproductionCost / 2, config);
    }
}
