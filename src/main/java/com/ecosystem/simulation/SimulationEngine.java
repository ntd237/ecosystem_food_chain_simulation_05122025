package com.ecosystem.simulation;

import com.ecosystem.model.Ecosystem;
import com.ecosystem.model.EcosystemConfig;
import com.ecosystem.model.EcosystemStats;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Engine điều khiển simulation.
 * Chạy game loop trên thread riêng, sử dụng Observer pattern để notify UI.
 */
public class SimulationEngine implements Runnable {

    // Hệ sinh thái
    private Ecosystem ecosystem;
    private EcosystemConfig config;

    // Trạng thái
    private SimulationState state;
    private final AtomicBoolean running;

    // Threading
    private Thread simulationThread;
    private int tickIntervalMs;

    // Observers
    private final List<SimulationListener> listeners;

    // Lịch sử thống kê (để vẽ biểu đồ)
    private final List<EcosystemStats> statsHistory;
    private static final int MAX_HISTORY_SIZE = 500;

    /**
     * Constructor tạo SimulationEngine.
     */
    public SimulationEngine() {
        this.running = new AtomicBoolean(false);
        this.state = SimulationState.STOPPED;
        this.listeners = new ArrayList<>();
        this.statsHistory = new ArrayList<>();
        this.tickIntervalMs = 200;
    }

    /**
     * Khởi tạo simulation với cấu hình.
     * 
     * @param config Cấu hình hệ sinh thái
     */
    public void initialize(EcosystemConfig config) {
        this.config = config;
        this.ecosystem = new Ecosystem(config);
        this.tickIntervalMs = config.getTickIntervalMs();
        ecosystem.initialize();
        statsHistory.clear();

        // Cập nhật thống kê ban đầu
        EcosystemStats initialStats = ecosystem.getStatistics();
        statsHistory.add(initialStats);
        notifyUpdate(initialStats);
    }

    /**
     * Bắt đầu simulation.
     */
    public void start() {
        if (state == SimulationState.RUNNING) {
            return;
        }

        if (ecosystem == null) {
            System.err.println("Ecosystem chưa được khởi tạo!");
            return;
        }

        running.set(true);
        state = SimulationState.RUNNING;
        notifyStateChanged(state);

        simulationThread = new Thread(this, "SimulationThread");
        simulationThread.setDaemon(true);
        simulationThread.start();
    }

    /**
     * Tạm dừng simulation.
     */
    public void pause() {
        if (state == SimulationState.RUNNING) {
            state = SimulationState.PAUSED;
            notifyStateChanged(state);
        }
    }

    /**
     * Tiếp tục simulation sau khi pause.
     */
    public void resume() {
        if (state == SimulationState.PAUSED) {
            state = SimulationState.RUNNING;
            notifyStateChanged(state);
        }
    }

    /**
     * Dừng simulation hoàn toàn.
     */
    public void stop() {
        running.set(false);
        state = SimulationState.STOPPED;
        notifyStateChanged(state);

        if (simulationThread != null) {
            simulationThread.interrupt();
            try {
                simulationThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Reset simulation về trạng thái ban đầu.
     */
    public void reset() {
        stop();
        if (config != null) {
            initialize(config);
        }
    }

    /**
     * Thực hiện một bước simulation (manual step).
     */
    public void step() {
        if (ecosystem != null && state != SimulationState.RUNNING) {
            performTick();
        }
    }

    /**
     * Game loop chính.
     */
    @Override
    public void run() {
        while (running.get()) {
            try {
                if (state == SimulationState.RUNNING) {
                    performTick();

                    // Kiểm tra điều kiện kết thúc
                    if (checkEndCondition()) {
                        break;
                    }
                }

                Thread.sleep(tickIntervalMs);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Thực hiện một tick của simulation.
     */
    private void performTick() {
        ecosystem.update();

        EcosystemStats stats = ecosystem.getStatistics();

        // Lưu vào lịch sử
        statsHistory.add(stats);
        if (statsHistory.size() > MAX_HISTORY_SIZE) {
            statsHistory.remove(0);
        }

        // Notify observers
        notifyUpdate(stats);
    }

    /**
     * Kiểm tra điều kiện kết thúc simulation.
     * 
     * @return true nếu simulation nên kết thúc
     */
    private boolean checkEndCondition() {
        EcosystemStats stats = ecosystem.getStatistics();

        // Kết thúc nếu không còn Herbivore và Carnivore
        if (stats.getHerbivoreCount() == 0 && stats.getCarnivoreCount() == 0) {
            finishSimulation("Tất cả động vật đã chết. Chỉ còn thực vật.");
            return true;
        }

        // Kết thúc nếu đạt max generations
        if (config != null && stats.getGeneration() >= config.getMaxGenerations()) {
            finishSimulation("Đạt số thế hệ tối đa: " + config.getMaxGenerations());
            return true;
        }

        return false;
    }

    /**
     * Kết thúc simulation.
     * 
     * @param reason Lý do kết thúc
     */
    private void finishSimulation(String reason) {
        running.set(false);
        state = SimulationState.FINISHED;
        notifyStateChanged(state);

        EcosystemStats finalStats = ecosystem.getStatistics();
        for (SimulationListener listener : listeners) {
            listener.onSimulationEnded(reason, finalStats);
        }
    }

    // === Observer Pattern Methods ===

    /**
     * Thêm listener.
     * 
     * @param listener Listener cần thêm
     */
    public void addListener(SimulationListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Xóa listener.
     * 
     * @param listener Listener cần xóa
     */
    public void removeListener(SimulationListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notify tất cả listeners về update.
     * 
     * @param stats Thống kê mới
     */
    private void notifyUpdate(EcosystemStats stats) {
        for (SimulationListener listener : listeners) {
            listener.onUpdate(stats);
        }
    }

    /**
     * Notify tất cả listeners về thay đổi trạng thái.
     * 
     * @param newState Trạng thái mới
     */
    private void notifyStateChanged(SimulationState newState) {
        for (SimulationListener listener : listeners) {
            listener.onStateChanged(newState);
        }
    }

    // === Getters và Setters ===

    public Ecosystem getEcosystem() {
        return ecosystem;
    }

    public SimulationState getState() {
        return state;
    }

    public int getTickIntervalMs() {
        return tickIntervalMs;
    }

    /**
     * Điều chỉnh tốc độ simulation.
     * 
     * @param intervalMs Thời gian giữa các tick (ms)
     */
    public void setTickIntervalMs(int intervalMs) {
        this.tickIntervalMs = Math.max(50, Math.min(intervalMs, 2000));
    }

    /**
     * Tăng tốc độ simulation.
     */
    public void speedUp() {
        setTickIntervalMs(tickIntervalMs / 2);
    }

    /**
     * Giảm tốc độ simulation.
     */
    public void slowDown() {
        setTickIntervalMs(tickIntervalMs * 2);
    }

    public List<EcosystemStats> getStatsHistory() {
        return new ArrayList<>(statsHistory);
    }

    public EcosystemConfig getConfig() {
        return config;
    }
}
