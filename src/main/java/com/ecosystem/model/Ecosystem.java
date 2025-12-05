package com.ecosystem.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class đại diện cho toàn bộ hệ sinh thái.
 * Quản lý grid, các sinh vật, và xử lý cập nhật trạng thái.
 * 
 * Áp dụng Encapsulation: quản lý trạng thái nội bộ của hệ sinh thái.
 */
public class Ecosystem {

    private final Random random = new Random();

    // Grid chứa các ô
    private final Cell[][] grid;
    private final int width;
    private final int height;

    // Danh sách sinh vật theo loại (thread-safe)
    private final List<Producer> producers;
    private final List<Herbivore> herbivores;
    private final List<Carnivore> carnivores;

    // Cấu hình
    private final EcosystemConfig config;

    // Số thế hệ (tick)
    private int generation;

    /**
     * Constructor tạo hệ sinh thái mới.
     * 
     * @param config Cấu hình hệ sinh thái
     */
    public Ecosystem(EcosystemConfig config) {
        this.config = config;
        this.width = config.getGridWidth();
        this.height = config.getGridHeight();
        this.generation = 0;

        // Khởi tạo grid
        this.grid = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Cell(x, y);
            }
        }

        // Khởi tạo danh sách sinh vật (thread-safe)
        this.producers = new CopyOnWriteArrayList<>();
        this.herbivores = new CopyOnWriteArrayList<>();
        this.carnivores = new CopyOnWriteArrayList<>();
    }

    /**
     * Khởi tạo hệ sinh thái với số lượng sinh vật ban đầu.
     */
    public void initialize() {
        // Xóa tất cả sinh vật hiện có
        clear();

        // Spawn Producers
        for (int i = 0; i < config.getInitialProducers(); i++) {
            spawnRandomOrganism(OrganismType.PRODUCER);
        }

        // Spawn Herbivores
        for (int i = 0; i < config.getInitialHerbivores(); i++) {
            spawnRandomOrganism(OrganismType.HERBIVORE);
        }

        // Spawn Carnivores
        for (int i = 0; i < config.getInitialCarnivores(); i++) {
            spawnRandomOrganism(OrganismType.CARNIVORE);
        }

        generation = 0;
    }

    /**
     * Spawn một sinh vật ở vị trí ngẫu nhiên.
     * 
     * @param type Loại sinh vật
     * @return true nếu spawn thành công
     */
    public boolean spawnRandomOrganism(OrganismType type) {
        // Tìm ô trống ngẫu nhiên
        List<Cell> emptyCells = getEmptyCells();
        if (emptyCells.isEmpty()) {
            return false;
        }

        Cell cell = emptyCells.get(random.nextInt(emptyCells.size()));
        Organism organism = createOrganism(type, cell.getX(), cell.getY());

        return addOrganism(organism);
    }

    /**
     * Tạo sinh vật mới theo loại.
     * 
     * @param type Loại sinh vật
     * @param x    Vị trí x
     * @param y    Vị trí y
     * @return Sinh vật mới
     */
    private Organism createOrganism(OrganismType type, int x, int y) {
        return switch (type) {
            case PRODUCER -> new Producer(x, y, config);
            case HERBIVORE -> new Herbivore(x, y, config);
            case CARNIVORE -> new Carnivore(x, y, config);
        };
    }

    /**
     * Thêm sinh vật vào hệ sinh thái.
     * 
     * @param organism Sinh vật cần thêm
     * @return true nếu thêm thành công
     */
    public boolean addOrganism(Organism organism) {
        if (organism == null)
            return false;

        int x = organism.getX();
        int y = organism.getY();

        // Kiểm tra giới hạn
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        Cell cell = grid[x][y];

        // Kiểm tra ô trống
        if (!cell.isEmpty()) {
            return false;
        }

        // Đặt sinh vật vào ô
        cell.setOccupant(organism);

        // Thêm vào danh sách tương ứng
        if (organism instanceof Producer p) {
            producers.add(p);
        } else if (organism instanceof Herbivore h) {
            herbivores.add(h);
        } else if (organism instanceof Carnivore c) {
            carnivores.add(c);
        }

        return true;
    }

    /**
     * Loại bỏ sinh vật khỏi hệ sinh thái.
     * 
     * @param organism Sinh vật cần loại bỏ
     */
    public void removeOrganism(Organism organism) {
        if (organism == null)
            return;

        int x = organism.getX();
        int y = organism.getY();

        if (x >= 0 && x < width && y >= 0 && y < height) {
            Cell cell = grid[x][y];
            if (cell.getOccupant() == organism) {
                cell.clear();
            }
        }

        // Loại bỏ khỏi danh sách
        if (organism instanceof Producer p) {
            producers.remove(p);
        } else if (organism instanceof Herbivore h) {
            herbivores.remove(h);
        } else if (organism instanceof Carnivore c) {
            carnivores.remove(c);
        }
    }

    /**
     * Cập nhật trạng thái hệ sinh thái (1 tick).
     */
    public void update() {
        generation++;

        // Spawn Producer mới (quá trình tự nhiên)
        if (random.nextDouble() < config.getProducerSpawnRate()) {
            spawnRandomOrganism(OrganismType.PRODUCER);
        }

        // Tạo danh sách shuffle để cập nhật ngẫu nhiên
        List<Organism> allOrganisms = new ArrayList<>();
        allOrganisms.addAll(producers);
        allOrganisms.addAll(herbivores);
        allOrganisms.addAll(carnivores);
        Collections.shuffle(allOrganisms);

        // Cập nhật từng sinh vật
        for (Organism organism : allOrganisms) {
            if (organism.isAlive()) {
                organism.update(this);
            }
        }

        // Loại bỏ sinh vật đã chết
        cleanupDeadOrganisms();
    }

    /**
     * Dọn dẹp sinh vật đã chết.
     */
    private void cleanupDeadOrganisms() {
        // Loại bỏ Producer chết
        Iterator<Producer> producerIterator = producers.iterator();
        while (producerIterator.hasNext()) {
            Producer p = producerIterator.next();
            if (!p.isAlive()) {
                Cell cell = grid[p.getX()][p.getY()];
                if (cell.getOccupant() == p) {
                    cell.clear();
                }
                producerIterator.remove();
            }
        }

        // Loại bỏ Herbivore chết
        Iterator<Herbivore> herbivoreIterator = herbivores.iterator();
        while (herbivoreIterator.hasNext()) {
            Herbivore h = herbivoreIterator.next();
            if (!h.isAlive()) {
                Cell cell = grid[h.getX()][h.getY()];
                if (cell.getOccupant() == h) {
                    cell.clear();
                }
                herbivoreIterator.remove();
            }
        }

        // Loại bỏ Carnivore chết
        Iterator<Carnivore> carnivoreIterator = carnivores.iterator();
        while (carnivoreIterator.hasNext()) {
            Carnivore c = carnivoreIterator.next();
            if (!c.isAlive()) {
                Cell cell = grid[c.getX()][c.getY()];
                if (cell.getOccupant() == c) {
                    cell.clear();
                }
                carnivoreIterator.remove();
            }
        }
    }

    /**
     * Lấy danh sách ô trống.
     * 
     * @return Danh sách các ô trống
     */
    public List<Cell> getEmptyCells() {
        List<Cell> emptyCells = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y].isEmpty()) {
                    emptyCells.add(grid[x][y]);
                }
            }
        }
        return emptyCells;
    }

    /**
     * Lấy danh sách ô trống lân cận một vị trí.
     * 
     * @param centerX Vị trí x trung tâm
     * @param centerY Vị trí y trung tâm
     * @return Danh sách ô trống lân cận
     */
    public List<Cell> getEmptyNeighbors(int centerX, int centerY) {
        List<Cell> neighbors = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0)
                    continue;

                int nx = centerX + dx;
                int ny = centerY + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (grid[nx][ny].isEmpty()) {
                        neighbors.add(grid[nx][ny]);
                    }
                }
            }
        }

        return neighbors;
    }

    /**
     * Lấy thống kê hệ sinh thái.
     * 
     * @return Đối tượng EcosystemStats
     */
    public EcosystemStats getStatistics() {
        double totalEnergy = 0;
        double producerEnergy = 0;
        double herbivoreEnergy = 0;
        double carnivoreEnergy = 0;

        for (Producer p : producers) {
            if (p.isAlive()) {
                producerEnergy += p.getEnergy();
            }
        }

        for (Herbivore h : herbivores) {
            if (h.isAlive()) {
                herbivoreEnergy += h.getEnergy();
            }
        }

        for (Carnivore c : carnivores) {
            if (c.isAlive()) {
                carnivoreEnergy += c.getEnergy();
            }
        }

        totalEnergy = producerEnergy + herbivoreEnergy + carnivoreEnergy;

        double avgProducerEnergy = producers.isEmpty() ? 0 : producerEnergy / producers.size();
        double avgHerbivoreEnergy = herbivores.isEmpty() ? 0 : herbivoreEnergy / herbivores.size();
        double avgCarnivoreEnergy = carnivores.isEmpty() ? 0 : carnivoreEnergy / carnivores.size();

        return new EcosystemStats(
                generation,
                producers.size(),
                herbivores.size(),
                carnivores.size(),
                totalEnergy,
                avgProducerEnergy,
                avgHerbivoreEnergy,
                avgCarnivoreEnergy);
    }

    /**
     * Xóa tất cả sinh vật khỏi hệ sinh thái.
     */
    public void clear() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y].clear();
            }
        }
        producers.clear();
        herbivores.clear();
        carnivores.clear();
        generation = 0;
    }

    // === Getters ===

    public Cell getCell(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[x][y];
        }
        return null;
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Producer> getProducers() {
        return new ArrayList<>(producers);
    }

    public List<Herbivore> getHerbivores() {
        return new ArrayList<>(herbivores);
    }

    public List<Carnivore> getCarnivores() {
        return new ArrayList<>(carnivores);
    }

    public int getGeneration() {
        return generation;
    }

    public EcosystemConfig getConfig() {
        return config;
    }

    /**
     * Enum định nghĩa các loại sinh vật.
     */
    public enum OrganismType {
        PRODUCER,
        HERBIVORE,
        CARNIVORE
    }
}
