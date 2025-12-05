package com.ecosystem.model;

import javafx.scene.paint.Color;

import java.util.List;

/**
 * Class đại diện cho động vật ăn thịt (Carnivore) trong hệ sinh thái.
 * 
 * Carnivore săn Herbivore để lấy năng lượng.
 * Chúng di chuyển nhanh hơn và có tầm nhìn xa hơn Herbivore.
 * 
 * Behavioral Polymorphism:
 * - update(): Di chuyển, săn Herbivore, mất năng lượng, sinh sản
 * - getColor(): Trả về màu đỏ (Red)
 * - findFood(): Tìm Herbivore gần nhất trong tầm nhìn
 * - move(): Di chuyển về phía Herbivore hoặc ngẫu nhiên
 */
public class Carnivore extends Consumer {

    // Tỷ lệ săn mồi thành công
    private final double huntSuccessRate;

    /**
     * Constructor cho Carnivore.
     * 
     * @param x      Vị trí x trên grid
     * @param y      Vị trí y trên grid
     * @param config Cấu hình hệ sinh thái
     */
    public Carnivore(int x, int y, EcosystemConfig config) {
        super("Carnivore",
                config.getCarnivoreInitialEnergy(),
                x, y,
                config.getCarnivoreHungerRate(),
                config.getCarnivoreVision(),
                config.getCarnivoreSpeed(),
                config);
        this.reproductionThreshold = config.getCarnivoreReproductionThreshold();
        this.reproductionCost = config.getCarnivoreReproductionCost();
        this.huntSuccessRate = 0.8; // 80% tỷ lệ săn thành công
    }

    /**
     * Constructor với năng lượng tùy chỉnh (dùng khi sinh sản).
     * 
     * @param x             Vị trí x trên grid
     * @param y             Vị trí y trên grid
     * @param initialEnergy Năng lượng ban đầu
     * @param config        Cấu hình hệ sinh thái
     */
    public Carnivore(int x, int y, double initialEnergy, EcosystemConfig config) {
        super("Carnivore",
                initialEnergy,
                x, y,
                config.getCarnivoreHungerRate(),
                config.getCarnivoreVision(),
                config.getCarnivoreSpeed(),
                config);
        this.reproductionThreshold = config.getCarnivoreReproductionThreshold();
        this.reproductionCost = config.getCarnivoreReproductionCost();
        this.huntSuccessRate = 0.8;
    }

    /**
     * Cập nhật trạng thái Carnivore mỗi tick.
     * - Mất năng lượng do hunger (nhanh hơn Herbivore)
     * - Tìm và di chuyển về phía Herbivore
     * - Săn Herbivore nếu ở cùng vị trí
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

        // Tìm con mồi (Herbivore) gần nhất
        Organism prey = findFood(ecosystem);

        if (prey != null) {
            int distance = manhattanDistance(x, y, prey.getX(), prey.getY());

            if (distance <= 1) {
                // Ở ngay cạnh con mồi -> săn
                hunt((Herbivore) prey, ecosystem);
            } else {
                // Di chuyển về phía con mồi
                move(ecosystem);
            }
        } else {
            // Không tìm thấy con mồi -> di chuyển ngẫu nhiên
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
     * Săn một Herbivore.
     * Tỷ lệ thành công dựa trên huntSuccessRate.
     * 
     * @param prey      Con mồi Herbivore
     * @param ecosystem Hệ sinh thái
     * @return true nếu săn thành công
     */
    public boolean hunt(Herbivore prey, Ecosystem ecosystem) {
        if (prey == null || !prey.isAlive()) {
            return false;
        }

        // Kiểm tra tỷ lệ thành công
        if (RANDOM.nextDouble() <= huntSuccessRate) {
            // Săn thành công
            eat(prey);
            ecosystem.removeOrganism(prey);
            return true;
        }

        // Săn thất bại - vẫn mất một ít năng lượng
        reduceEnergy(hungerRate * 0.5);
        return false;
    }

    /**
     * Di chuyển Carnivore trong hệ sinh thái.
     * Ưu tiên di chuyển về phía Herbivore gần nhất.
     * Carnivore di chuyển nhanh hơn, có thể di chuyển nhiều bước mỗi tick.
     * 
     * @param ecosystem Hệ sinh thái
     */
    @Override
    public void move(Ecosystem ecosystem) {
        // Carnivore có thể di chuyển nhiều bước mỗi tick
        for (int step = 0; step < speed; step++) {
            Organism prey = findFood(ecosystem);

            if (prey != null) {
                int distance = manhattanDistance(x, y, prey.getX(), prey.getY());

                if (distance <= 1) {
                    // Đã ở gần con mồi, dừng di chuyển
                    break;
                }

                // Di chuyển về phía con mồi
                moveTowards(prey.getX(), prey.getY(), ecosystem);
            } else {
                // Di chuyển ngẫu nhiên
                moveRandomly(ecosystem);
            }
        }
    }

    /**
     * Tìm Herbivore gần nhất trong tầm nhìn.
     * 
     * @param ecosystem Hệ sinh thái để tìm kiếm
     * @return Herbivore gần nhất, hoặc null nếu không tìm thấy
     */
    @Override
    public Organism findFood(Ecosystem ecosystem) {
        Herbivore nearestPrey = null;
        int minDistance = Integer.MAX_VALUE;

        List<Herbivore> herbivores = ecosystem.getHerbivores();

        for (Herbivore herbivore : herbivores) {
            if (herbivore.isAlive() && herbivore.isEdible()) {
                int distance = manhattanDistance(x, y, herbivore.getX(), herbivore.getY());

                if (distance <= visionRange && distance < minDistance) {
                    minDistance = distance;
                    nearestPrey = herbivore;
                }
            }
        }

        return nearestPrey;
    }

    /**
     * Kiểm tra xem có thể ăn sinh vật trong ô đích không.
     * Carnivore chỉ ăn Herbivore.
     * 
     * @param cell Ô đích
     * @return true nếu ô chứa Herbivore
     */
    @Override
    protected boolean canEatOccupant(Cell cell) {
        return cell.hasHerbivore();
    }

    /**
     * Trả về màu đỏ đại diện cho Carnivore.
     * Độ đậm tỷ lệ với năng lượng.
     * 
     * @return Màu đỏ
     */
    @Override
    public Color getColor() {
        // Tính độ đậm dựa trên năng lượng (0.3 - 1.0)
        double maxEnergy = reproductionThreshold * 1.5;
        double intensity = 0.3 + (Math.min(energy, maxEnergy) / maxEnergy) * 0.7;
        return Color.color(intensity, 0, 0);
    }

    /**
     * Trả về ký tự đại diện cho Carnivore.
     * 
     * @return Ký tự "🦁" hoặc "C"
     */
    @Override
    public String getSymbol() {
        return "🦁";
    }

    /**
     * Thực hiện sinh sản tạo Carnivore mới.
     * 
     * @return Carnivore mới, hoặc null nếu không thể sinh sản
     */
    @Override
    public Organism reproduce() {
        if (!canReproduce()) {
            return null;
        }

        // Trừ chi phí sinh sản
        reduceEnergy(reproductionCost);

        // Tạo Carnivore con với năng lượng = chi phí sinh sản / 2
        return new Carnivore(x, y, reproductionCost / 2, config);
    }

    // === Getters ===

    public double getHuntSuccessRate() {
        return huntSuccessRate;
    }
}
