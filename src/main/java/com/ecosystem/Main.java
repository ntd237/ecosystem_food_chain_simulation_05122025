package com.ecosystem;

import com.ecosystem.ui.MainApp;
import javafx.application.Application;

/**
 * Entry point cho ứng dụng Ecosystem Food Chain Simulation.
 * 
 * Chương trình mô phỏng hệ sinh thái với chuỗi thức ăn:
 * - Producer (🌿): Thực vật quang hợp
 * - Herbivore (🐰): Động vật ăn cỏ
 * - Carnivore (🦁): Động vật ăn thịt
 * 
 * Áp dụng các nguyên tắc OOP:
 * - Inheritance: Organism → Producer/Consumer → Herbivore/Carnivore
 * - Polymorphism: update(), move(), findFood(), getColor() được override
 * - Encapsulation: Các thuộc tính được bảo vệ với getter/setter
 * - Abstraction: Interface Movable, Consumable, Reproducible
 *
 */
public class Main {

    /**
     * Main method khởi động ứng dụng JavaFX.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   Ecosystem Food Chain Simulation");
        System.out.println("===========================================");
        System.out.println("Mo phong chuoi thuc an trong he sinh thai");
        System.out.println("Producer -> Herbivore -> Carnivore");
        System.out.println("-------------------------------------------");
        System.out.println("Dang khoi dong giao dien...");

        // Khởi động JavaFX Application
        Application.launch(MainApp.class, args);
    }
}
