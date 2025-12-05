package com.ecosystem.model;

/**
 * Class chứa thống kê về hệ sinh thái.
 * Được sử dụng để hiển thị thông tin và vẽ biểu đồ.
 */
public class EcosystemStats {

    private final int generation;
    private final int producerCount;
    private final int herbivoreCount;
    private final int carnivoreCount;
    private final double totalEnergy;
    private final double averageProducerEnergy;
    private final double averageHerbivoreEnergy;
    private final double averageCarnivoreEnergy;

    /**
     * Constructor tạo snapshot thống kê.
     * 
     * @param generation         Số thế hệ hiện tại
     * @param producerCount      Số lượng Producer
     * @param herbivoreCount     Số lượng Herbivore
     * @param carnivoreCount     Số lượng Carnivore
     * @param totalEnergy        Tổng năng lượng trong hệ thống
     * @param avgProducerEnergy  Năng lượng trung bình của Producer
     * @param avgHerbivoreEnergy Năng lượng trung bình của Herbivore
     * @param avgCarnivoreEnergy Năng lượng trung bình của Carnivore
     */
    public EcosystemStats(int generation, int producerCount, int herbivoreCount,
            int carnivoreCount, double totalEnergy,
            double avgProducerEnergy, double avgHerbivoreEnergy,
            double avgCarnivoreEnergy) {
        this.generation = generation;
        this.producerCount = producerCount;
        this.herbivoreCount = herbivoreCount;
        this.carnivoreCount = carnivoreCount;
        this.totalEnergy = totalEnergy;
        this.averageProducerEnergy = avgProducerEnergy;
        this.averageHerbivoreEnergy = avgHerbivoreEnergy;
        this.averageCarnivoreEnergy = avgCarnivoreEnergy;
    }

    /**
     * Lấy tổng số sinh vật trong hệ thống.
     * 
     * @return Tổng số sinh vật
     */
    public int getTotalOrganisms() {
        return producerCount + herbivoreCount + carnivoreCount;
    }

    /**
     * Kiểm tra xem hệ sinh thái có còn tồn tại không.
     * Hệ sinh thái chết khi không còn Herbivore hoặc Carnivore.
     * 
     * @return true nếu hệ sinh thái còn hoạt động
     */
    public boolean isEcosystemAlive() {
        return herbivoreCount > 0 || carnivoreCount > 0;
    }

    /**
     * Kiểm tra xem hệ sinh thái có cân bằng không.
     * Cân bằng khi tỷ lệ các loài hợp lý.
     * 
     * @return true nếu hệ sinh thái cân bằng
     */
    public boolean isBalanced() {
        if (herbivoreCount == 0)
            return false;
        double carnivoreRatio = (double) carnivoreCount / herbivoreCount;
        double herbivoreProducerRatio = producerCount > 0 ? (double) herbivoreCount / producerCount : 0;

        // Cân bằng khi: carnivore/herbivore < 0.5 và herbivore/producer < 0.5
        return carnivoreRatio < 0.5 && herbivoreProducerRatio < 0.5;
    }

    // === Getters ===

    public int getGeneration() {
        return generation;
    }

    public int getProducerCount() {
        return producerCount;
    }

    public int getHerbivoreCount() {
        return herbivoreCount;
    }

    public int getCarnivoreCount() {
        return carnivoreCount;
    }

    public double getTotalEnergy() {
        return totalEnergy;
    }

    public double getAverageProducerEnergy() {
        return averageProducerEnergy;
    }

    public double getAverageHerbivoreEnergy() {
        return averageHerbivoreEnergy;
    }

    public double getAverageCarnivoreEnergy() {
        return averageCarnivoreEnergy;
    }

    @Override
    public String toString() {
        return String.format(
                "Gen %d | Producers: %d (avg: %.1f) | Herbivores: %d (avg: %.1f) | Carnivores: %d (avg: %.1f) | Total Energy: %.1f",
                generation, producerCount, averageProducerEnergy,
                herbivoreCount, averageHerbivoreEnergy,
                carnivoreCount, averageCarnivoreEnergy,
                totalEnergy);
    }
}
