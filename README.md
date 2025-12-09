# 🌍 Ecosystem Food Chain Simulation

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.org)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue.svg)](https://openjfx.io)
[![Maven](https://img.shields.io/badge/Maven-3.9+-red.svg)](https://maven.apache.org)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

## 📋 Mô tả

Dự án mô phỏng **chuỗi thức ăn trong hệ sinh thái** áp dụng các nguyên tắc **Lập trình Hướng đối tượng (OOP)**. 

Hệ sinh thái bao gồm 3 cấp độ dinh dưỡng:
- **🌿 Producer (Sinh vật sản xuất)**: Thực vật quang hợp tạo năng lượng
- **🐰 Herbivore (Động vật ăn cỏ)**: Ăn thực vật để lấy năng lượng
- **🦁 Carnivore (Động vật ăn thịt)**: Săn động vật ăn cỏ

## 🎯 Tính năng

- **Mô phỏng thời gian thực**: Grid-based simulation với các sinh vật di chuyển và tương tác
- **Quy tắc 10%**: Chỉ 10% năng lượng được chuyển giao giữa các cấp độ dinh dưỡng
- **3 Kịch bản mô phỏng**:
  - ⚖️ Hệ sinh thái cân bằng
  - 📈 Quá tải Herbivore  
  - 💀 Nguy cơ tuyệt chủng
- **Biểu đồ dân số**: Theo dõi số lượng các loài theo thời gian
- **Điều khiển tốc độ**: Điều chỉnh nhanh/chậm simulation

## 🏗️ Kiến trúc OOP

### Class Diagram

```
Organism (abstract)
├── Producer
└── Consumer (abstract)
    ├── Herbivore
    └── Carnivore

Interfaces:
├── Movable
├── Consumable
└── Reproducible
```

### Behavioral Polymorphism

| Method | Producer | Herbivore | Carnivore |
|--------|----------|-----------|-----------|
| `update()` | Quang hợp | Di chuyển, ăn cây | Săn mồi |
| `move()` | Không | Tìm cây | Săn Herbivore |
| `getColor()` | Xanh lá | Xanh dương | Đỏ |
| `reproduce()` | Sinh sản vô tính | Sinh sản | Sinh sản |

## 📁 Cấu trúc dự án

```
ecosystem_food_chain_simulation_05122025/
├── pom.xml                                 # Maven configuration
├── README.md                               # Documentation
│
└── src/
    ├── main/
    │   ├── java/com/ecosystem/
    │   │   ├── Main.java                   # Entry point
    │   │   ├── model/                      # Domain models
    │   │   │   ├── Organism.java           # Abstract base class
    │   │   │   ├── Producer.java           # Thực vật
    │   │   │   ├── Consumer.java           # Abstract consumer
    │   │   │   ├── Herbivore.java          # Động vật ăn cỏ
    │   │   │   ├── Carnivore.java          # Động vật ăn thịt
    │   │   │   ├── Cell.java               # Ô trong grid
    │   │   │   ├── Ecosystem.java          # Quản lý hệ sinh thái
    │   │   │   ├── EcosystemConfig.java    # Cấu hình
    │   │   │   └── EcosystemStats.java     # Thống kê
    │   │   ├── interfaces/
    │   │   │   ├── Movable.java
    │   │   │   ├── Consumable.java
    │   │   │   └── Reproducible.java
    │   │   ├── simulation/
    │   │   │   ├── SimulationEngine.java   # Game loop
    │   │   │   ├── SimulationState.java
    │   │   │   └── SimulationListener.java
    │   │   ├── ui/
    │   │   │   ├── MainApp.java            # JavaFX Application
    │   │   │   ├── GridView.java           # Grid rendering
    │   │   │   └── ChartView.java          # Population chart
    │   │   └── utils/
    │   │       └── ConfigLoader.java       # YAML loader
    │   └── resources/
    │       └── config/
    │           └── ecosystem.yaml          # Configuration
    │
    └── test/java/com/ecosystem/
        └── model/
            └── OrganismTest.java           # Unit tests
```

## 💻 Yêu cầu hệ thống

- **Java**: 17+
- **Maven**: 3.9+
- **JavaFX**: 21 (tự động download qua Maven)

## 🚀 Cài đặt và Chạy

### Cách 1: Sử dụng Maven

```bash
# Clone repository
git clone https://github.com/ntd237/ecosystem_food_chain_simulation_05122025.git
cd ecosystem_food_chain_simulation_05122025

# Compile và chạy
mvn clean javafx:run
```

### Cách 2: Build JAR

```bash
# Build
mvn clean package

# Chạy
java -jar target/ecosystem-simulation-1.0.0.jar
```

## 📊 Nguyên lý hoạt động

### Chuyển đổi năng lượng (10% Rule)

```
☀️ Ánh sáng → 🌿 Producer (quang hợp)
    ↓ 10% năng lượng
🐰 Herbivore (ăn cỏ)
    ↓ 10% năng lượng  
🦁 Carnivore (săn mồi)
```

### Vòng đời sinh vật

1. **Producer**: Quang hợp → Tăng năng lượng → Sinh sản khi đủ ngưỡng
2. **Herbivore**: Di chuyển → Tìm cây → Ăn → Mất năng lượng → Sinh sản/Chết
3. **Carnivore**: Di chuyển → Săn mồi → Ăn → Mất năng lượng → Sinh sản/Chết

## 🎮 Hướng dẫn sử dụng

1. **Chọn kịch bản**: Tại Main Menu, chọn một trong 3 kịch bản
2. **Bắt đầu**: Nhấn nút "▶ Bắt đầu" để chạy simulation
3. **Điều khiển**:
   - ⏸ Tạm dừng: Dừng simulation
   - ⏭ Bước: Chạy từng bước
   - 🔄 Reset: Khởi động lại
   - Thanh tốc độ: Điều chỉnh nhanh/chậm
4. **Quan sát**: Theo dõi grid và biểu đồ dân số
