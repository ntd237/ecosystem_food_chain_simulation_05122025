package com.ecosystem.simulation;

/**
 * Enum định nghĩa các trạng thái của simulation.
 */
public enum SimulationState {

    /**
     * Simulation chưa bắt đầu hoặc đã reset.
     */
    STOPPED,

    /**
     * Simulation đang chạy.
     */
    RUNNING,

    /**
     * Simulation tạm dừng.
     */
    PAUSED,

    /**
     * Simulation kết thúc (hệ sinh thái chết hoặc đạt max generations).
     */
    FINISHED
}
