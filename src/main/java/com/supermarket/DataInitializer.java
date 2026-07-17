package com.supermarket;

import com.supermarket.model.Product;
import com.supermarket.model.User;
import com.supermarket.repository.ProductRepository;
import com.supermarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) {
        // Create admin if not exists
        if (!userRepository.existsByEmail("admin@freshmart.com")) {
            userRepository.save(new User("Admin", "admin@freshmart.com", "admin123", "ADMIN"));
        }

        // Seed products
        if (productRepository.count() == 0) {
            productRepository.save(new Product("Whole Milk (1L)", "Fresh full-fat milk", 12.50, 100, "Dairy", ""));
            productRepository.save(new Product("Free Range Eggs (12)", "Farm fresh eggs", 28.00, 80, "Dairy", ""));
            productRepository.save(new Product("White Bread", "Soft sliced white bread", 15.00, 60, "Bakery", ""));
            productRepository.save(new Product("Brown Bread", "Whole wheat bread", 18.00, 50, "Bakery", ""));
            productRepository.save(new Product("Bananas (kg)", "Fresh yellow bananas", 10.00, 200, "Fruits", ""));
            productRepository.save(new Product("Apples (kg)", "Crisp red apples", 22.00, 150, "Fruits", ""));
            productRepository.save(new Product("Tomatoes (kg)", "Ripe red tomatoes", 14.00, 120, "Vegetables", ""));
            productRepository.save(new Product("Cucumber", "Fresh green cucumber", 8.00, 90, "Vegetables", ""));
            productRepository.save(new Product("Chicken Breast (kg)", "Boneless skinless chicken", 55.00, 70, "Meat", ""));
            productRepository.save(new Product("Orange Juice (1L)", "100% natural orange juice", 25.00, 85, "Beverages", ""));
            productRepository.save(new Product("Mineral Water (1.5L)", "Pure mineral water", 7.00, 300, "Beverages", ""));
            productRepository.save(new Product("Cheddar Cheese (200g)", "Aged cheddar cheese", 35.00, 60, "Dairy", ""));
        }
    }
}
