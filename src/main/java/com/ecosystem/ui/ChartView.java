package com.ecosystem.ui;

import com.ecosystem.model.EcosystemStats;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.List;

/**
 * Component hiển thị biểu đồ dân số theo thời gian.
 * Sử dụng JavaFX LineChart để vẽ 3 đường cho Producer, Herbivore, Carnivore.
 */
public class ChartView {

    private final LineChart<Number, Number> chart;
    private final XYChart.Series<Number, Number> producerSeries;
    private final XYChart.Series<Number, Number> herbivoreSeries;
    private final XYChart.Series<Number, Number> carnivoreSeries;

    private static final int MAX_DATA_POINTS = 100;

    /**
     * Constructor tạo chart.
     */
    public ChartView() {
        // Tạo axes
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Thế hệ");
        xAxis.setAutoRanging(true);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượng");
        yAxis.setAutoRanging(true);

        // Tạo chart
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Dân số theo thời gian");
        chart.setCreateSymbols(false); // Không vẽ điểm, chỉ vẽ đường
        chart.setAnimated(false); // Tắt animation để cập nhật mượt hơn

        // Tạo series cho mỗi loại sinh vật
        producerSeries = new XYChart.Series<>();
        producerSeries.setName("Producer 🌿");

        herbivoreSeries = new XYChart.Series<>();
        herbivoreSeries.setName("Herbivore 🐰");

        carnivoreSeries = new XYChart.Series<>();
        carnivoreSeries.setName("Carnivore 🦁");

        chart.getData().addAll(producerSeries, herbivoreSeries, carnivoreSeries);

        // Style
        chart.setStyle("-fx-background-color: #1a1a2e;");
        chart.lookup(".chart-plot-background").setStyle("-fx-background-color: #16213e;");
    }

    /**
     * Cập nhật dữ liệu chart với stats mới.
     * 
     * @param stats Thống kê mới nhất
     */
    public void update(EcosystemStats stats) {
        int generation = stats.getGeneration();

        // Thêm điểm mới
        producerSeries.getData().add(
                new XYChart.Data<>(generation, stats.getProducerCount()));
        herbivoreSeries.getData().add(
                new XYChart.Data<>(generation, stats.getHerbivoreCount()));
        carnivoreSeries.getData().add(
                new XYChart.Data<>(generation, stats.getCarnivoreCount()));

        // Giới hạn số điểm để tránh lag
        if (producerSeries.getData().size() > MAX_DATA_POINTS) {
            producerSeries.getData().remove(0);
            herbivoreSeries.getData().remove(0);
            carnivoreSeries.getData().remove(0);
        }
    }

    /**
     * Cập nhật chart với lịch sử stats.
     * 
     * @param statsHistory Danh sách lịch sử thống kê
     */
    public void updateFromHistory(List<EcosystemStats> statsHistory) {
        clear();

        int startIndex = Math.max(0, statsHistory.size() - MAX_DATA_POINTS);

        for (int i = startIndex; i < statsHistory.size(); i++) {
            EcosystemStats stats = statsHistory.get(i);
            int generation = stats.getGeneration();

            producerSeries.getData().add(
                    new XYChart.Data<>(generation, stats.getProducerCount()));
            herbivoreSeries.getData().add(
                    new XYChart.Data<>(generation, stats.getHerbivoreCount()));
            carnivoreSeries.getData().add(
                    new XYChart.Data<>(generation, stats.getCarnivoreCount()));
        }
    }

    /**
     * Xóa tất cả dữ liệu chart.
     */
    public void clear() {
        producerSeries.getData().clear();
        herbivoreSeries.getData().clear();
        carnivoreSeries.getData().clear();
    }

    /**
     * Lấy LineChart component.
     * 
     * @return LineChart
     */
    public LineChart<Number, Number> getChart() {
        return chart;
    }
}
