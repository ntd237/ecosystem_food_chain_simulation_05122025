package com.ecosystem.interfaces;

import com.ecosystem.model.Organism;

/**
 * Interface định nghĩa hành vi sinh sản cho các sinh vật.
 * Được implement bởi tất cả các Organism.
 * 
 * Mỗi loại sinh vật có ngưỡng năng lượng và chi phí sinh sản khác nhau.
 */
public interface Reproducible {

    /**
     * Kiểm tra xem sinh vật có đủ điều kiện để sinh sản hay không.
     * Điều kiện: năng lượng >= ngưỡng sinh sản và còn sống.
     * 
     * @return true nếu có thể sinh sản, false nếu không
     */
    boolean canReproduce();

    /**
     * Thực hiện sinh sản, tạo ra sinh vật mới cùng loại.
     * Sinh vật mẹ sẽ mất một lượng năng lượng nhất định.
     * 
     * @return Sinh vật mới được tạo ra, hoặc null nếu không thể sinh sản
     */
    Organism reproduce();

    /**
     * Lấy ngưỡng năng lượng cần thiết để sinh sản.
     * 
     * @return Ngưỡng năng lượng
     */
    double getReproductionThreshold();

    /**
     * Lấy chi phí năng lượng khi sinh sản.
     * 
     * @return Chi phí năng lượng
     */
    double getReproductionCost();
}
