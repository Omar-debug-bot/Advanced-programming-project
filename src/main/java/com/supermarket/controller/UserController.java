package com.supermarket.controller;

import com.supermarket.model.CartItem;
import com.supermarket.model.Product;
import com.supermarket.model.User;
import com.supermarket.service.OrderService;
import com.supermarket.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    private User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @SuppressWarnings("unchecked")
    private Map<Long, CartItem> getCart(HttpSession session) {
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model,
                       @RequestParam(required = false) String search,
                       @RequestParam(required = false) String category) {
        if (getUser(session) == null) return "redirect:/login";

        List<Product> products;
        if (search != null && !search.isBlank()) {
            products = productService.searchProducts(search);
        } else if (category != null && !category.isBlank()) {
            products = productService.getByCategory(category);
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("user", getUser(session));
        model.addAttribute("cartSize", getCart(session).size());
        return "user/home";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session) {
        if (getUser(session) == null) return "redirect:/login";

        productService.findById(productId).ifPresent(product -> {
            Map<Long, CartItem> cart = getCart(session);
            if (cart.containsKey(productId)) {
                cart.get(productId).setQuantity(cart.get(productId).getQuantity() + quantity);
            } else {
                cart.put(productId, new CartItem(productId, product.getName(), product.getPrice(), quantity));
            }
        });
        return "redirect:/user/cart";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        if (getUser(session) == null) return "redirect:/login";

        Map<Long, CartItem> cart = getCart(session);
        double total = cart.values().stream().mapToDouble(CartItem::getSubtotal).sum();

        model.addAttribute("cart", cart.values());
        model.addAttribute("total", total);
        model.addAttribute("user", getUser(session));
        return "user/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId, HttpSession session) {
        getCart(session).remove(productId);
        return "redirect:/user/cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        if (getUser(session) == null) return "redirect:/login";
        Map<Long, CartItem> cart = getCart(session);
        if (cart.isEmpty()) return "redirect:/user/cart";

        double total = cart.values().stream().mapToDouble(CartItem::getSubtotal).sum();
        model.addAttribute("total", total);
        model.addAttribute("user", getUser(session));
        return "user/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String address, HttpSession session) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";

        Map<Long, CartItem> cart = getCart(session);
        if (cart.isEmpty()) return "redirect:/user/cart";

        orderService.placeOrder(user, cart, address);
        session.removeAttribute("cart");
        return "redirect:/user/orders?success";
    }

    @GetMapping("/orders")
    public String myOrders(HttpSession session, Model model) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("orders", orderService.getUserOrders(user));
        model.addAttribute("user", user);
        return "user/orders";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User updated, HttpSession session) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";

        user.setName(updated.getName());
        user.setPhone(updated.getPhone());
        user.setAddress(updated.getAddress());
        // only update password if provided
        if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
            user.setPassword(updated.getPassword());
        }
        session.setAttribute("user", user);
        return "redirect:/user/profile?updated";
    }
}
