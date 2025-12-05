package com.ecosystem.interfaces;

/**
 * Interface định nghĩa hành vi cho các sinh vật có thể bị ăn/tiêu thụ.
 * Được implement bởi tất cả các Organism (Producer, Herbivore).
 * 
 * Producer bị Herbivore ăn, Herbivore bị Carnivore ăn.
 */
public interface Consumable {

    /**
     * Lấy lượng năng lượng mà sinh vật này cung cấp khi bị ăn.
     * Theo 10% rule: chỉ 10% năng lượng được chuyển cho sinh vật ăn.
     * 
     * @return Lượng năng lượng có thể chuyển giao
     */
    double getEnergyValue();

    /**
     * Xử lý khi sinh vật bị tiêu thụ/ăn.
     * Thường dẫn đến sinh vật chết hoặc mất năng lượng.
     */
    void beConsumed();

    /**
     * Kiểm tra xem sinh vật có thể bị ăn hay không.
     * 
     * @return true nếu có thể bị ăn, false nếu không
     */
    boolean isEdible();
}
