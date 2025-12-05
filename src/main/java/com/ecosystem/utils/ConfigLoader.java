package com.ecosystem.utils;

import com.ecosystem.model.EcosystemConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * Utility class để load cấu hình từ file YAML.
 * Đảm bảo không hardcode các tham số trong code.
 */
public class ConfigLoader {

    private static final String DEFAULT_CONFIG_PATH = "/config/ecosystem.yaml";

    /**
     * Load cấu hình từ file YAML mặc định.
     * 
     * @return EcosystemConfig với các giá trị từ file
     */
    public static EcosystemConfig loadConfig() {
        return loadConfig(DEFAULT_CONFIG_PATH);
    }

    /**
     * Load cấu hình từ file YAML.
     * 
     * @param resourcePath Đường dẫn đến file YAML trong resources
     * @return EcosystemConfig với các giá trị từ file
     */
    public static EcosystemConfig loadConfig(String resourcePath) {
        try (InputStream inputStream = ConfigLoader.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                System.err.println("Không tìm thấy file config: " + resourcePath);
                System.err.println("Sử dụng cấu hình mặc định.");
                return new EcosystemConfig();
            }

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);

            return parseConfig(data);

        } catch (Exception e) {
            System.err.println("Lỗi khi load config: " + e.getMessage());
            return new EcosystemConfig();
        }
    }

    /**
     * Parse dữ liệu YAML thành EcosystemConfig.
     * 
     * @param data Dữ liệu YAML đã parse
     * @return EcosystemConfig
     */
    @SuppressWarnings("unchecked")
    private static EcosystemConfig parseConfig(Map<String, Object> data) {
        EcosystemConfig.Builder builder = EcosystemConfig.builder();

        Map<String, Object> ecosystem = (Map<String, Object>) data.get("ecosystem");
        if (ecosystem == null) {
            return builder.build();
        }

        // Parse grid config
        Map<String, Object> grid = (Map<String, Object>) ecosystem.get("grid");
        if (grid != null) {
            int width = getInt(grid, "width", 50);
            int height = getInt(grid, "height", 30);
            builder.gridSize(width, height);
        }

        // Parse energy config
        Map<String, Object> energy = (Map<String, Object>) ecosystem.get("energy");
        if (energy != null) {
            builder.energyTransferRate(getDouble(energy, "transfer_rate", 0.10));
            builder.producerEnergy(
                    getDouble(energy, "producer_initial", 30.0),
                    getDouble(energy, "producer_max", 100.0),
                    getDouble(energy, "producer_photosynthesis", 5.0));
            builder.herbivoreEnergy(
                    getDouble(energy, "herbivore_initial", 50.0),
                    getDouble(energy, "herbivore_hunger_rate", 2.0));
            builder.carnivoreEnergy(
                    getDouble(energy, "carnivore_initial", 80.0),
                    getDouble(energy, "carnivore_hunger_rate", 3.0));
        }

        // Parse movement config
        Map<String, Object> movement = (Map<String, Object>) ecosystem.get("movement");
        if (movement != null) {
            builder.herbivoreMovement(
                    getInt(movement, "herbivore_vision", 5),
                    getInt(movement, "herbivore_speed", 1));
            builder.carnivoreMovement(
                    getInt(movement, "carnivore_vision", 7),
                    getInt(movement, "carnivore_speed", 2));
        }

        // Parse reproduction config
        Map<String, Object> reproduction = (Map<String, Object>) ecosystem.get("reproduction");
        if (reproduction != null) {
            builder.producerReproduction(
                    getDouble(reproduction, "producer_threshold", 80.0),
                    getDouble(reproduction, "producer_cost", 40.0));
            builder.herbivoreReproduction(
                    getDouble(reproduction, "herbivore_threshold", 100.0),
                    getDouble(reproduction, "herbivore_cost", 50.0));
            builder.carnivoreReproduction(
                    getDouble(reproduction, "carnivore_threshold", 150.0),
                    getDouble(reproduction, "carnivore_cost", 75.0));
            builder.producerSpawnRate(getDouble(reproduction, "producer_spawn_rate", 0.02));
        }

        // Parse simulation config
        Map<String, Object> simulation = (Map<String, Object>) ecosystem.get("simulation");
        if (simulation != null) {
            builder.simulation(
                    getInt(simulation, "tick_interval_ms", 200),
                    getInt(simulation, "max_generations", 10000));
        }

        return builder.build();
    }

    /**
     * Load cấu hình scenario cụ thể.
     * 
     * @param scenarioName Tên scenario (balanced, overpopulation, extinction)
     * @return EcosystemConfig với initial population từ scenario
     */
    @SuppressWarnings("unchecked")
    public static EcosystemConfig loadScenario(String scenarioName) {
        try (InputStream inputStream = ConfigLoader.class.getResourceAsStream(DEFAULT_CONFIG_PATH)) {
            if (inputStream == null) {
                return new EcosystemConfig();
            }

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);

            // Load base config
            EcosystemConfig config = parseConfig(data);

            // Override with scenario values
            Map<String, Object> scenarios = (Map<String, Object>) data.get("scenarios");
            if (scenarios != null) {
                Map<String, Object> scenario = (Map<String, Object>) scenarios.get(scenarioName);
                if (scenario != null) {
                    config.setInitialProducers(getInt(scenario, "producers", 100));
                    config.setInitialHerbivores(getInt(scenario, "herbivores", 30));
                    config.setInitialCarnivores(getInt(scenario, "carnivores", 10));
                }
            }

            return config;

        } catch (Exception e) {
            System.err.println("Lỗi khi load scenario: " + e.getMessage());
            return new EcosystemConfig();
        }
    }

    // === Helper methods ===

    private static int getInt(Map<String, Object> map, String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    private static double getDouble(Map<String, Object> map, String key, double defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
}
