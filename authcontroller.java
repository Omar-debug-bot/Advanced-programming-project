package com.supermarket.controller;

import com.supermarket.model.User;
import com.supermarket.service.UserService;
import com.supermarket.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private CookieUtil cookieUtil;

    @GetMapping("/")
    public String home(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if ("ADMIN".equals(user.getRole())) return "redirect:/admin/dashboard";
        return "redirect:/user/home";
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request) {
        model.addAttribute("user", new User());
        model.addAttribute("rememberedEmail", cookieUtil.getRememberedEmail(request));
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        HttpServletResponse response,
                        Model model) {
        return userService.login(email, password).map(user -> {
            session.setAttribute("user", user);
            cookieUtil.rememberEmail(response, email);
            if ("ADMIN".equals(user.getRole())) return "redirect:/admin/dashboard";
            return "redirect:/user/home";
        }).orElseGet(() -> {
            model.addAttribute("error", "Invalid email or password");
            return "auth/login";
        });
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult result, Model model) {
        if (result.hasErrors()) return "auth/register";
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("error", "Email already registered");
            return "auth/register";
        }
        userService.register(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        cookieUtil.clearEmail(response);
        session.invalidate();
        return "redirect:/login";
    }
}
