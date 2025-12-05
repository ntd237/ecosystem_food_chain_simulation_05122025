package com.ecosystem.interfaces;

import com.ecosystem.model.Ecosystem;

/**
 * Interface định nghĩa hành vi di chuyển cho các sinh vật.
 * Được implement bởi Herbivore và Carnivore.
 * 
 * Sử dụng behavioral polymorphism: mỗi loại Consumer có cách di chuyển khác nhau.
 */
public interface Movable {
    
    /**
     * Di chuyển sinh vật trong hệ sinh thái.
     * Herbivore: di chuyển ngẫu nhiên hoặc về phía Producer gần nhất.
     * Carnivore: di chuyển ngẫu nhiên hoặc săn Herbivore gần nhất.
     * 
     * @param ecosystem Hệ sinh thái chứa thông tin về grid và các sinh vật khác
     */
    void move(Ecosystem ecosystem);
    
    /**
     * Lấy tốc độ di chuyển của sinh vật (số ô mỗi tick).
     * 
     * @return Tốc độ di chuyển
     */
    int getSpeed();
    
    /**
     * Lấy tầm nhìn của sinh vật (số ô có thể phát hiện thức ăn).
     * 
     * @return Tầm nhìn
     */
    int getVisionRange();
}
