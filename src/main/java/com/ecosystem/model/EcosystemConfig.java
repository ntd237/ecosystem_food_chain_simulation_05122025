package com.ecosystem.model;

/**
 * Class chứa cấu hình cho hệ sinh thái.
 * Tất cả các tham số được load từ file YAML, không hardcode.
 * 
 * Áp dụng Encapsulation: gom nhóm các tham số liên quan.
 */
public class EcosystemConfig {

    // === Grid Configuration ===
    private int gridWidth = 50;
    private int gridHeight = 30;

    // === Energy Configuration ===
    private double energyTransferRate = 0.10; // 10% rule
    private double producerPhotosynthesis = 5.0;
    private double producerInitialEnergy = 30.0;
    private double producerMaxEnergy = 100.0;
    private double herbivoreInitialEnergy = 50.0;
    private double herbivoreHungerRate = 2.0;
    private double carnivoreInitialEnergy = 80.0;
    private double carnivoreHungerRate = 3.0;

    // === Movement Configuration ===
    private int herbivoreVision = 5;
    private int carnivoreVision = 7;
    private int herbivoreSpeed = 1;
    private int carnivoreSpeed = 2;

    // === Reproduction Configuration ===
    private double producerReproductionThreshold = 80.0;
    private double producerReproductionCost = 40.0;
    private double herbivoreReproductionThreshold = 100.0;
    private double herbivoreReproductionCost = 50.0;
    private double carnivoreReproductionThreshold = 150.0;
    private double carnivoreReproductionCost = 75.0;
    private double producerSpawnRate = 0.02;

    // === Simulation Configuration ===
    private int tickIntervalMs = 200;
    private int maxGenerations = 10000;

    // === Initial Population ===
    private int initialProducers = 100;
    private int initialHerbivores = 30;
    private int initialCarnivores = 10;

    /**
     * Constructor mặc định với giá trị default.
     */
    public EcosystemConfig() {
        // Sử dụng giá trị default
    }

    // === Builder Pattern cho việc tạo config ===

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class để tạo EcosystemConfig theo từng bước.
     */
    public static class Builder {
        private final EcosystemConfig config = new EcosystemConfig();

        public Builder gridSize(int width, int height) {
            config.gridWidth = width;
            config.gridHeight = height;
            return this;
        }

        public Builder energyTransferRate(double rate) {
            config.energyTransferRate = rate;
            return this;
        }

        public Builder producerEnergy(double initial, double max, double photosynthesis) {
            config.producerInitialEnergy = initial;
            config.producerMaxEnergy = max;
            config.producerPhotosynthesis = photosynthesis;
            return this;
        }

        public Builder herbivoreEnergy(double initial, double hungerRate) {
            config.herbivoreInitialEnergy = initial;
            config.herbivoreHungerRate = hungerRate;
            return this;
        }

        public Builder carnivoreEnergy(double initial, double hungerRate) {
            config.carnivoreInitialEnergy = initial;
            config.carnivoreHungerRate = hungerRate;
            return this;
        }

        public Builder herbivoreMovement(int vision, int speed) {
            config.herbivoreVision = vision;
            config.herbivoreSpeed = speed;
            return this;
        }

        public Builder carnivoreMovement(int vision, int speed) {
            config.carnivoreVision = vision;
            config.carnivoreSpeed = speed;
            return this;
        }

        public Builder producerReproduction(double threshold, double cost) {
            config.producerReproductionThreshold = threshold;
            config.producerReproductionCost = cost;
            return this;
        }

        public Builder herbivoreReproduction(double threshold, double cost) {
            config.herbivoreReproductionThreshold = threshold;
            config.herbivoreReproductionCost = cost;
            return this;
        }

        public Builder carnivoreReproduction(double threshold, double cost) {
            config.carnivoreReproductionThreshold = threshold;
            config.carnivoreReproductionCost = cost;
            return this;
        }

        public Builder producerSpawnRate(double rate) {
            config.producerSpawnRate = rate;
            return this;
        }

        public Builder simulation(int tickMs, int maxGen) {
            config.tickIntervalMs = tickMs;
            config.maxGenerations = maxGen;
            return this;
        }

        public Builder initialPopulation(int producers, int herbivores, int carnivores) {
            config.initialProducers = producers;
            config.initialHerbivores = herbivores;
            config.initialCarnivores = carnivores;
            return this;
        }

        public EcosystemConfig build() {
            return config;
        }
    }

    // === Getters ===

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public double getEnergyTransferRate() {
        return energyTransferRate;
    }

    public double getProducerPhotosynthesis() {
        return producerPhotosynthesis;
    }

    public double getProducerInitialEnergy() {
        return producerInitialEnergy;
    }

    public double getProducerMaxEnergy() {
        return producerMaxEnergy;
    }

    public double getHerbivoreInitialEnergy() {
        return herbivoreInitialEnergy;
    }

    public double getHerbivoreHungerRate() {
        return herbivoreHungerRate;
    }

    public double getCarnivoreInitialEnergy() {
        return carnivoreInitialEnergy;
    }

    public double getCarnivoreHungerRate() {
        return carnivoreHungerRate;
    }

    public int getHerbivoreVision() {
        return herbivoreVision;
    }

    public int getCarnivoreVision() {
        return carnivoreVision;
    }

    public int getHerbivoreSpeed() {
        return herbivoreSpeed;
    }

    public int getCarnivoreSpeed() {
        return carnivoreSpeed;
    }

    public double getProducerReproductionThreshold() {
        return producerReproductionThreshold;
    }

    public double getProducerReproductionCost() {
        return producerReproductionCost;
    }

    public double getHerbivoreReproductionThreshold() {
        return herbivoreReproductionThreshold;
    }

    public double getHerbivoreReproductionCost() {
        return herbivoreReproductionCost;
    }

    public double getCarnivoreReproductionThreshold() {
        return carnivoreReproductionThreshold;
    }

    public double getCarnivoreReproductionCost() {
        return carnivoreReproductionCost;
    }

    public double getProducerSpawnRate() {
        return producerSpawnRate;
    }

    public int getTickIntervalMs() {
        return tickIntervalMs;
    }

    public int getMaxGenerations() {
        return maxGenerations;
    }

    public int getInitialProducers() {
        return initialProducers;
    }

    public int getInitialHerbivores() {
        return initialHerbivores;
    }

    public int getInitialCarnivores() {
        return initialCarnivores;
    }

    // === Setters cho YAML loading ===

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public void setInitialProducers(int initialProducers) {
        this.initialProducers = initialProducers;
    }

    public void setInitialHerbivores(int initialHerbivores) {
        this.initialHerbivores = initialHerbivores;
    }

    public void setInitialCarnivores(int initialCarnivores) {
        this.initialCarnivores = initialCarnivores;
    }
}
