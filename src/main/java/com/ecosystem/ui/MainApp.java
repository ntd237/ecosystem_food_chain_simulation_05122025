package com.ecosystem.ui;

import com.ecosystem.model.EcosystemConfig;
import com.ecosystem.model.EcosystemStats;
import com.ecosystem.simulation.SimulationEngine;
import com.ecosystem.simulation.SimulationListener;
import com.ecosystem.simulation.SimulationState;
import com.ecosystem.utils.ConfigLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Main JavaFX Application cho Ecosystem Simulation.
 * 
 * Giao diện gồm:
 * - Main Menu: Chọn kịch bản, Help, Quit
 * - Simulation Screen: Grid view, Chart, Controls
 */
public class MainApp extends Application implements SimulationListener {

    private Stage primaryStage;
    private Scene mainMenuScene;
    private Scene simulationScene;

    // Simulation components
    private SimulationEngine engine;
    private GridView gridView;
    private ChartView chartView;

    // UI components
    private Label statsLabel;
    private Label stateLabel;
    private Button playPauseButton;
    private Slider speedSlider;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.engine = new SimulationEngine();
        engine.addListener(this);

        // Tạo các scene
        mainMenuScene = createMainMenuScene();
        simulationScene = createSimulationScene();

        // Hiển thị main menu
        primaryStage.setTitle("🌍 Ecosystem Food Chain Simulation");
        primaryStage.setScene(mainMenuScene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();

        // Xử lý đóng cửa sổ
        primaryStage.setOnCloseRequest(event -> {
            engine.stop();
            Platform.exit();
        });
    }

    /**
     * Tạo Main Menu scene.
     */
    private Scene createMainMenuScene() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");

        // Title
        Label title = new Label("🌍 Ecosystem Food Chain");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Mô phỏng chuỗi thức ăn trong hệ sinh thái");
        subtitle.setFont(Font.font("Arial", 18));
        subtitle.setTextFill(Color.LIGHTGRAY);

        // Scenario buttons
        VBox scenarioBox = new VBox(15);
        scenarioBox.setAlignment(Pos.CENTER);

        Label chooseLabel = new Label("Chọn kịch bản mô phỏng:");
        chooseLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        chooseLabel.setTextFill(Color.WHITE);

        Button balancedBtn = createMenuButton("⚖️ Hệ sinh thái cân bằng",
                "Producer: 100 | Herbivore: 30 | Carnivore: 10");
        balancedBtn.setOnAction(e -> startSimulation("balanced"));

        Button overpopBtn = createMenuButton("📈 Quá tải Herbivore",
                "Producer: 50 | Herbivore: 80 | Carnivore: 5");
        overpopBtn.setOnAction(e -> startSimulation("overpopulation"));

        Button extinctBtn = createMenuButton("💀 Nguy cơ tuyệt chủng",
                "Producer: 30 | Herbivore: 15 | Carnivore: 25");
        extinctBtn.setOnAction(e -> startSimulation("extinction"));

        scenarioBox.getChildren().addAll(chooseLabel, balancedBtn, overpopBtn, extinctBtn);

        // Help and Quit buttons
        HBox bottomBox = new HBox(20);
        bottomBox.setAlignment(Pos.CENTER);

        Button helpBtn = new Button("❓ Hướng dẫn");
        helpBtn.setStyle(getSecondaryButtonStyle());
        helpBtn.setOnAction(e -> showHelpDialog());

        Button quitBtn = new Button("🚪 Thoát");
        quitBtn.setStyle(getSecondaryButtonStyle());
        quitBtn.setOnAction(e -> confirmQuit());

        bottomBox.getChildren().addAll(helpBtn, quitBtn);

        // Legend
        HBox legend = createLegend();

        root.getChildren().addAll(title, subtitle, scenarioBox, legend, bottomBox);

        return new Scene(root, 900, 700);
    }

    /**
     * Tạo Simulation scene.
     */
    private Scene createSimulationScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        // Top: Stats bar
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Center: Grid view
        gridView = new GridView(700, 450);
        VBox centerBox = new VBox(gridView);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(10));
        root.setCenter(centerBox);

        // Right: Chart
        chartView = new ChartView();
        chartView.getChart().setPrefWidth(350);
        chartView.getChart().setPrefHeight(300);

        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));
        rightPane.getChildren().add(chartView.getChart());
        rightPane.getChildren().add(createLegend());
        root.setRight(rightPane);

        // Bottom: Controls
        HBox controls = createControls();
        root.setBottom(controls);

        return new Scene(root, 1200, 750);
    }

    /**
     * Tạo top bar với stats.
     */
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setStyle("-fx-background-color: #0f0f23;");

        stateLabel = new Label("STOPPED");
        stateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        stateLabel.setTextFill(Color.ORANGE);
        stateLabel.setMinWidth(100);

        statsLabel = new Label("Thế hệ: 0 | 🌿 0 | 🐰 0 | 🦁 0");
        statsLabel.setFont(Font.font("Arial", 14));
        statsLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("← Quay lại Menu");
        backBtn.setStyle(getSecondaryButtonStyle());
        backBtn.setOnAction(e -> backToMenu());

        topBar.getChildren().addAll(stateLabel, statsLabel, spacer, backBtn);

        return topBar;
    }

    /**
     * Tạo controls bar.
     */
    private HBox createControls() {
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(15));
        controls.setStyle("-fx-background-color: #0f0f23;");

        playPauseButton = new Button("▶ Bắt đầu");
        playPauseButton.setStyle(getPrimaryButtonStyle());
        playPauseButton.setOnAction(e -> togglePlayPause());

        Button stepButton = new Button("⏭ Bước");
        stepButton.setStyle(getSecondaryButtonStyle());
        stepButton.setOnAction(e -> engine.step());

        Button resetButton = new Button("🔄 Reset");
        resetButton.setStyle(getSecondaryButtonStyle());
        resetButton.setOnAction(e -> {
            engine.reset();
            gridView.setEcosystem(engine.getEcosystem());
            chartView.clear();
            syncSpeedFromSlider();
        });

        // Speed control
        Label speedLabel = new Label("Tốc độ:");
        speedLabel.setTextFill(Color.WHITE);

        speedSlider = new Slider(50, 500, 200);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setPrefWidth(150);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Inverse: slider cao = tick interval thấp = nhanh hơn
            int tickMs = (int) (550 - newVal.doubleValue());
            engine.setTickIntervalMs(tickMs);
        });

        controls.getChildren().addAll(playPauseButton, stepButton, resetButton,
                speedLabel, speedSlider);

        return controls;
    }

    /**
     * Tạo legend hiển thị màu sắc các loài.
     */
    private HBox createLegend() {
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10));

        legend.getChildren().addAll(
                createLegendItem("🌿 Producer", Color.GREEN),
                createLegendItem("🐰 Herbivore", Color.BLUE),
                createLegendItem("🦁 Carnivore", Color.RED));

        return legend;
    }

    private HBox createLegendItem(String text, Color color) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER);

        javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(8, color);
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);

        item.getChildren().addAll(circle, label);
        return item;
    }

    /**
     * Tạo menu button với subtitle.
     */
    private Button createMenuButton(String title, String subtitle) {
        Button button = new Button(title + "\n" + subtitle);
        button.setStyle(getPrimaryButtonStyle());
        button.setPrefWidth(350);
        button.setPrefHeight(60);
        button.setFont(Font.font("Arial", 14));
        return button;
    }

    // === Button Styles ===

    private String getPrimaryButtonStyle() {
        return "-fx-background-color: #4a69bd; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 5; " +
                "-fx-cursor: hand;";
    }

    private String getSecondaryButtonStyle() {
        return "-fx-background-color: #2d3436; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-radius: 5; " +
                "-fx-cursor: hand;";
    }

    // === Actions ===

    private void startSimulation(String scenario) {
        EcosystemConfig config = ConfigLoader.loadScenario(scenario);
        engine.initialize(config);

        gridView.setEcosystem(engine.getEcosystem());
        chartView.clear();

        primaryStage.setScene(simulationScene);
    }

    private void togglePlayPause() {
        SimulationState state = engine.getState();

        if (state == SimulationState.RUNNING) {
            engine.pause();
        } else if (state == SimulationState.PAUSED) {
            engine.resume();
        } else if (state == SimulationState.FINISHED) {
            // Khi FINISHED, cần reset về trạng thái gốc trước khi chạy lại
            engine.reset();
            gridView.setEcosystem(engine.getEcosystem());
            chartView.clear();
            syncSpeedFromSlider();
            engine.start();
        } else {
            // STOPPED: Chạy lần đầu
            syncSpeedFromSlider();
            engine.start();
        }
    }

    /**
     * Đồng bộ tốc độ từ slider hiện tại sang engine.
     * Gọi sau khi initialize/reset để đảm bảo speed không bị reset.
     */
    private void syncSpeedFromSlider() {
        int tickMs = (int) (550 - speedSlider.getValue());
        engine.setTickIntervalMs(tickMs);
    }

    private void backToMenu() {
        engine.stop();
        chartView.clear();
        primaryStage.setScene(mainMenuScene);
    }

    private void showHelpDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hướng dẫn");
        alert.setHeaderText("Ecosystem Food Chain Simulation");
        alert.setContentText(
                "🎯 MỤC TIÊU:\n" +
                        "Quan sát sự tương tác giữa các loài trong chuỗi thức ăn.\n\n" +
                        "📊 QUY TẮC:\n" +
                        "• Producer (🌿): Thực vật, quang hợp tạo năng lượng\n" +
                        "• Herbivore (🐰): Ăn Producer, bị Carnivore săn\n" +
                        "• Carnivore (🦁): Săn Herbivore\n\n" +
                        "⚡ NĂNG LƯỢNG:\n" +
                        "• Chỉ 10% năng lượng được chuyển giao khi ăn\n" +
                        "• Sinh vật chết khi hết năng lượng\n" +
                        "• Sinh sản khi đủ năng lượng\n\n" +
                        "🎮 ĐIỀU KHIỂN:\n" +
                        "• Bắt đầu/Tạm dừng: Điều khiển simulation\n" +
                        "• Bước: Chạy từng bước một\n" +
                        "• Reset: Khởi động lại\n" +
                        "• Thanh tốc độ: Điều chỉnh nhanh/chậm");
        alert.showAndWait();
    }

    private void confirmQuit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Bạn có chắc chắn muốn thoát?");
        alert.setContentText("Mọi tiến trình simulation sẽ bị mất.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                engine.stop();
                Platform.exit();
            }
        });
    }

    // === SimulationListener Implementation ===

    @Override
    public void onUpdate(EcosystemStats stats) {
        Platform.runLater(() -> {
            // Cập nhật stats label
            statsLabel.setText(String.format(
                    "Thế hệ: %d | 🌿 %d | 🐰 %d | 🦁 %d | Tổng năng lượng: %.0f",
                    stats.getGeneration(),
                    stats.getProducerCount(),
                    stats.getHerbivoreCount(),
                    stats.getCarnivoreCount(),
                    stats.getTotalEnergy()));

            // Cập nhật grid
            gridView.render();

            // Cập nhật chart
            chartView.update(stats);
        });
    }

    @Override
    public void onStateChanged(SimulationState newState) {
        Platform.runLater(() -> {
            stateLabel.setText(newState.toString());

            switch (newState) {
                case RUNNING -> {
                    stateLabel.setTextFill(Color.LIGHTGREEN);
                    playPauseButton.setText("⏸ Tạm dừng");
                }
                case PAUSED -> {
                    stateLabel.setTextFill(Color.YELLOW);
                    playPauseButton.setText("▶ Tiếp tục");
                }
                case STOPPED -> {
                    stateLabel.setTextFill(Color.ORANGE);
                    playPauseButton.setText("▶ Bắt đầu");
                }
                case FINISHED -> {
                    stateLabel.setTextFill(Color.RED);
                    playPauseButton.setText("🔄 Chạy lại");
                }
            }
        });
    }

    @Override
    public void onSimulationEnded(String reason, EcosystemStats stats) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Simulation kết thúc");
            alert.setHeaderText(reason);
            alert.setContentText(String.format(
                    "Thống kê cuối cùng:\n" +
                            "• Thế hệ: %d\n" +
                            "• Producer: %d\n" +
                            "• Herbivore: %d\n" +
                            "• Carnivore: %d\n" +
                            "• Tổng năng lượng: %.0f",
                    stats.getGeneration(),
                    stats.getProducerCount(),
                    stats.getHerbivoreCount(),
                    stats.getCarnivoreCount(),
                    stats.getTotalEnergy()));
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
