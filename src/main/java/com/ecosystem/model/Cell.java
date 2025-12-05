package com.ecosystem.model;

/**
 * Class đại diện cho một ô trong grid của hệ sinh thái.
 * Mỗi ô có thể chứa tối đa một sinh vật.
 * 
 * Áp dụng Encapsulation: quản lý trạng thái ô và sinh vật bên trong.
 */
public class Cell {

    private final int x;
    private final int y;
    private Organism occupant;

    /**
     * Constructor tạo ô trống.
     * 
     * @param x Tọa độ x của ô
     * @param y Tọa độ y của ô
     */
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.occupant = null;
    }

    /**
     * Kiểm tra xem ô có trống không.
     * 
     * @return true nếu ô trống, false nếu có sinh vật
     */
    public boolean isEmpty() {
        return occupant == null || !occupant.isAlive();
    }

    /**
     * Đặt sinh vật vào ô.
     * 
     * @param organism Sinh vật cần đặt vào
     * @return true nếu đặt thành công, false nếu ô đã có sinh vật
     */
    public boolean setOccupant(Organism organism) {
        if (isEmpty() || organism == null) {
            this.occupant = organism;
            if (organism != null) {
                organism.setPosition(x, y);
            }
            return true;
        }
        return false;
    }

    /**
     * Lấy sinh vật đang chiếm ô.
     * 
     * @return Sinh vật trong ô, hoặc null nếu trống
     */
    public Organism getOccupant() {
        // Dọn dẹp sinh vật đã chết
        if (occupant != null && !occupant.isAlive()) {
            occupant = null;
        }
        return occupant;
    }

    /**
     * Xóa sinh vật khỏi ô.
     */
    public void clear() {
        this.occupant = null;
    }

    /**
     * Kiểm tra xem ô có chứa Producer không.
     * 
     * @return true nếu có Producer
     */
    public boolean hasProducer() {
        return occupant instanceof Producer && occupant.isAlive();
    }

    /**
     * Kiểm tra xem ô có chứa Herbivore không.
     * 
     * @return true nếu có Herbivore
     */
    public boolean hasHerbivore() {
        return occupant instanceof Herbivore && occupant.isAlive();
    }

    /**
     * Kiểm tra xem ô có chứa Carnivore không.
     * 
     * @return true nếu có Carnivore
     */
    public boolean hasCarnivore() {
        return occupant instanceof Carnivore && occupant.isAlive();
    }

    // === Getters ===

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return String.format("Cell(%d,%d): empty", x, y);
        }
        return String.format("Cell(%d,%d): %s", x, y, occupant.getName());
    }
}
