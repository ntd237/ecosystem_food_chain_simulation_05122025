package com.ecosystem.model;

import com.ecosystem.interfaces.Consumable;
import com.ecosystem.interfaces.Reproducible;
import javafx.scene.paint.Color;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract base class đại diện cho tất cả sinh vật trong hệ sinh thái.
 * 
 * Áp dụng các nguyên tắc OOP:
 * - Encapsulation: các thuộc tính được bảo vệ (protected/private)
 * - Abstraction: định nghĩa abstract methods cho subclass implement
 * - Inheritance: Producer và Consumer kế thừa từ class này
 */
public abstract class Organism implements Consumable, Reproducible {

    // Counter để tạo ID duy nhất cho mỗi sinh vật
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);

    // Thuộc tính cơ bản của sinh vật
    protected final int id;
    protected String name;
    protected double energy;
    protected int x;
    protected int y;
    protected boolean alive;
    protected int age;

    // Cấu hình sinh sản
    protected double reproductionThreshold;
    protected double reproductionCost;

    /**
     * Constructor cho Organism.
     * 
     * @param name   Tên loại sinh vật
     * @param energy Năng lượng ban đầu
     * @param x      Vị trí x trên grid
     * @param y      Vị trí y trên grid
     */
    protected Organism(String name, double energy, int x, int y) {
        this.id = ID_COUNTER.incrementAndGet();
        this.name = name;
        this.energy = energy;
        this.x = x;
        this.y = y;
        this.alive = true;
        this.age = 0;
    }

    // === Abstract Methods (Behavioral Polymorphism) ===

    /**
     * Cập nhật trạng thái sinh vật mỗi tick.
     * Mỗi loại sinh vật có logic update khác nhau:
     * - Producer: quang hợp tăng năng lượng
     * - Herbivore: di chuyển, ăn, mất năng lượng
     * - Carnivore: di chuyển, săn mồi, mất năng lượng
     * 
     * @param ecosystem Hệ sinh thái để tương tác
     */
    public abstract void update(Ecosystem ecosystem);

    /**
     * Lấy màu sắc đại diện của sinh vật.
     * - Producer: Xanh lá (Green)
     * - Herbivore: Xanh dương (Blue)
     * - Carnivore: Đỏ (Red)
     * 
     * @return Màu sắc JavaFX
     */
    public abstract Color getColor();

    /**
     * Lấy ký tự đại diện của sinh vật để hiển thị.
     * 
     * @return Ký tự đại diện
     */
    public abstract String getSymbol();

    // === Consumable Interface Implementation ===

    @Override
    public double getEnergyValue() {
        // Áp dụng 10% rule: chỉ 10% năng lượng được chuyển giao
        return energy * 0.10;
    }

    @Override
    public void beConsumed() {
        // Khi bị ăn, sinh vật chết
        this.alive = false;
        this.energy = 0;
    }

    @Override
    public boolean isEdible() {
        return alive && energy > 0;
    }

    // === Reproducible Interface Implementation ===

    @Override
    public boolean canReproduce() {
        return alive && energy >= reproductionThreshold;
    }

    @Override
    public double getReproductionThreshold() {
        return reproductionThreshold;
    }

    @Override
    public double getReproductionCost() {
        return reproductionCost;
    }

    // === Common Methods ===

    /**
     * Xử lý khi sinh vật chết.
     */
    public void die() {
        this.alive = false;
        this.energy = 0;
    }

    /**
     * Tăng tuổi sinh vật.
     */
    public void incrementAge() {
        this.age++;
    }

    /**
     * Thêm năng lượng cho sinh vật.
     * 
     * @param amount Lượng năng lượng thêm vào
     */
    public void addEnergy(double amount) {
        this.energy += amount;
    }

    /**
     * Giảm năng lượng sinh vật. Nếu năng lượng <= 0, sinh vật chết.
     * 
     * @param amount Lượng năng lượng mất đi
     */
    public void reduceEnergy(double amount) {
        this.energy -= amount;
        if (this.energy <= 0) {
            die();
        }
    }

    /**
     * Di chuyển sinh vật đến vị trí mới.
     * 
     * @param newX Vị trí x mới
     * @param newY Vị trí y mới
     */
    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    // === Getters ===

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d, energy=%.1f, pos=(%d,%d), alive=%s]",
                name, id, energy, x, y, alive);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Organism organism = (Organism) obj;
        return id == organism.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
