package com.ecosystem.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests cho các model classes.
 */
class OrganismTest {

    private EcosystemConfig config;

    @BeforeEach
    void setUp() {
        config = EcosystemConfig.builder()
                .gridSize(50, 30)
                .producerEnergy(30, 100, 5)
                .herbivoreEnergy(50, 2)
                .carnivoreEnergy(80, 3)
                .producerReproduction(80, 40)
                .herbivoreReproduction(100, 50)
                .carnivoreReproduction(150, 75)
                .build();
    }

    @Nested
    @DisplayName("Producer Tests")
    class ProducerTests {

        @Test
        @DisplayName("Producer should photosynthesize and gain energy")
        void testPhotosynthesis() {
            Producer producer = new Producer(5, 5, config);
            double initialEnergy = producer.getEnergy();

            producer.photosynthesize();

            assertTrue(producer.getEnergy() > initialEnergy,
                    "Energy should increase after photosynthesis");
        }

        @Test
        @DisplayName("Producer energy should not exceed max")
        void testMaxEnergy() {
            Producer producer = new Producer(5, 5, config);

            // Quang hợp nhiều lần
            for (int i = 0; i < 100; i++) {
                producer.photosynthesize();
            }

            assertTrue(producer.getEnergy() <= producer.getMaxEnergy(),
                    "Energy should not exceed max energy");
        }

        @Test
        @DisplayName("Producer should be able to reproduce when energy threshold reached")
        void testReproduction() {
            Producer producer = new Producer(5, 5, config);
            producer.setEnergy(100); // Đủ ngưỡng sinh sản

            assertTrue(producer.canReproduce(),
                    "Producer should be able to reproduce with sufficient energy");

            Organism offspring = producer.reproduce();
            assertNotNull(offspring, "Offspring should not be null");
            assertTrue(offspring instanceof Producer, "Offspring should be Producer");
        }

        @Test
        @DisplayName("Producer should die when consumed")
        void testBeConsumed() {
            Producer producer = new Producer(5, 5, config);

            producer.beConsumed();

            assertFalse(producer.isAlive(), "Producer should be dead after consumed");
            assertEquals(0, producer.getEnergy(), "Energy should be 0 after consumed");
        }
    }

    @Nested
    @DisplayName("Herbivore Tests")
    class HerbivoreTests {

        @Test
        @DisplayName("Herbivore should lose energy over time")
        void testHunger() {
            Herbivore herbivore = new Herbivore(5, 5, config);
            double initialEnergy = herbivore.getEnergy();

            herbivore.loseEnergy();

            assertTrue(herbivore.getEnergy() < initialEnergy,
                    "Energy should decrease due to hunger");
        }

        @Test
        @DisplayName("Herbivore should die when energy depleted")
        void testStarvation() {
            Herbivore herbivore = new Herbivore(5, 5, config);
            herbivore.setEnergy(1);

            herbivore.loseEnergy();
            herbivore.loseEnergy();

            assertFalse(herbivore.isAlive(), "Herbivore should die when energy depleted");
        }

        @Test
        @DisplayName("Herbivore should gain energy when eating")
        void testEating() {
            Herbivore herbivore = new Herbivore(5, 5, config);
            Producer producer = new Producer(5, 6, config);
            producer.setEnergy(100);

            double initialEnergy = herbivore.getEnergy();
            herbivore.eat(producer);

            assertTrue(herbivore.getEnergy() > initialEnergy,
                    "Energy should increase after eating");
            assertFalse(producer.isAlive(), "Producer should be dead after eaten");
        }

        @Test
        @DisplayName("Energy transfer should follow 10% rule")
        void testEnergyTransfer() {
            Herbivore herbivore = new Herbivore(5, 5, config);
            Producer producer = new Producer(5, 6, config);
            producer.setEnergy(100);

            double initialEnergy = herbivore.getEnergy();
            double expectedGain = producer.getEnergyValue(); // 10% of 100 = 10

            herbivore.eat(producer);

            assertEquals(initialEnergy + expectedGain, herbivore.getEnergy(), 0.01,
                    "Energy gain should follow 10% rule");
        }
    }

    @Nested
    @DisplayName("Carnivore Tests")
    class CarnivoreTests {

        @Test
        @DisplayName("Carnivore should have higher hunger rate than Herbivore")
        void testHungerRate() {
            Herbivore herbivore = new Herbivore(5, 5, config);
            Carnivore carnivore = new Carnivore(10, 10, config);

            assertTrue(carnivore.getHungerRate() > herbivore.getHungerRate(),
                    "Carnivore should have higher hunger rate");
        }

        @Test
        @DisplayName("Carnivore should have faster speed than Herbivore")
        void testSpeed() {
            Herbivore herbivore = new Herbivore(5, 5, config);
            Carnivore carnivore = new Carnivore(10, 10, config);

            assertTrue(carnivore.getSpeed() >= herbivore.getSpeed(),
                    "Carnivore should be faster or equal to Herbivore");
        }

        @Test
        @DisplayName("Carnivore should gain energy when eating Herbivore")
        void testHunting() {
            Carnivore carnivore = new Carnivore(5, 5, config);
            Herbivore herbivore = new Herbivore(5, 6, config);
            herbivore.setEnergy(100);

            double initialEnergy = carnivore.getEnergy();
            carnivore.eat(herbivore);

            assertTrue(carnivore.getEnergy() > initialEnergy,
                    "Energy should increase after hunting");
        }
    }

    @Nested
    @DisplayName("Ecosystem Tests")
    class EcosystemTests {

        private Ecosystem ecosystem;

        @BeforeEach
        void setUp() {
            ecosystem = new Ecosystem(config);
        }

        @Test
        @DisplayName("Ecosystem should initialize with correct dimensions")
        void testDimensions() {
            assertEquals(50, ecosystem.getWidth(), "Width should match config");
            assertEquals(30, ecosystem.getHeight(), "Height should match config");
        }

        @Test
        @DisplayName("Ecosystem should add organisms correctly")
        void testAddOrganism() {
            Producer producer = new Producer(5, 5, config);

            boolean added = ecosystem.addOrganism(producer);

            assertTrue(added, "Organism should be added successfully");
            assertEquals(1, ecosystem.getProducers().size(), "Producer count should be 1");
        }

        @Test
        @DisplayName("Ecosystem should not add organism to occupied cell")
        void testAddToOccupiedCell() {
            Producer producer1 = new Producer(5, 5, config);
            Producer producer2 = new Producer(5, 5, config);

            ecosystem.addOrganism(producer1);
            boolean added = ecosystem.addOrganism(producer2);

            assertFalse(added, "Should not add to occupied cell");
        }

        @Test
        @DisplayName("Ecosystem should remove dead organisms")
        void testRemoveOrganism() {
            Producer producer = new Producer(5, 5, config);
            ecosystem.addOrganism(producer);

            ecosystem.removeOrganism(producer);

            assertEquals(0, ecosystem.getProducers().size(), "Producer should be removed");
            assertTrue(ecosystem.getCell(5, 5).isEmpty(), "Cell should be empty");
        }

        @Test
        @DisplayName("Ecosystem statistics should be accurate")
        void testStatistics() {
            ecosystem.addOrganism(new Producer(1, 1, config));
            ecosystem.addOrganism(new Producer(2, 2, config));
            ecosystem.addOrganism(new Herbivore(3, 3, config));
            ecosystem.addOrganism(new Carnivore(4, 4, config));

            EcosystemStats stats = ecosystem.getStatistics();

            assertEquals(2, stats.getProducerCount(), "Producer count should be 2");
            assertEquals(1, stats.getHerbivoreCount(), "Herbivore count should be 1");
            assertEquals(1, stats.getCarnivoreCount(), "Carnivore count should be 1");
        }
    }
}
