package com.supermarket.service;

import com.supermarket.model.*;
import com.supermarket.repository.OrderRepository;
import com.supermarket.repository.OrderItemRepository;
import com.supermarket.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Order placeOrder(User user, Map<Long, CartItem> cart, String address) {
        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(address);

        double total = 0;
        order = orderRepository.save(order); // save first to get ID

        List<OrderItem> items = new ArrayList<>();
        for (CartItem cartItem : cart.values()) {
            Optional<Product> productOpt = productRepository.findById(cartItem.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                OrderItem item = new OrderItem(order, product, cartItem.getQuantity());
                orderItemRepository.save(item);
                items.add(item);
                total += item.getSubtotal();

                // reduce stock
                product.setStock(product.getStock() - cartItem.getQuantity());
                productRepository.save(product);
            }
        }

        order.setItems(items);
        order.setTotalPrice(total);
        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public void updateStatus(Long orderId, String status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }
}
