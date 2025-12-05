package com.ecosystem.simulation;

import com.ecosystem.model.EcosystemStats;

/**
 * Interface Observer để nhận thông báo từ SimulationEngine.
 * Triển khai Observer Pattern để cập nhật UI.
 */
public interface SimulationListener {

    /**
     * Được gọi khi simulation cập nhật (mỗi tick).
     * 
     * @param stats Thống kê hệ sinh thái hiện tại
     */
    void onUpdate(EcosystemStats stats);

    /**
     * Được gọi khi trạng thái simulation thay đổi.
     * 
     * @param newState Trạng thái mới
     */
    void onStateChanged(SimulationState newState);

    /**
     * Được gọi khi simulation kết thúc.
     * 
     * @param reason Lý do kết thúc
     * @param stats  Thống kê cuối cùng
     */
    void onSimulationEnded(String reason, EcosystemStats stats);
}
