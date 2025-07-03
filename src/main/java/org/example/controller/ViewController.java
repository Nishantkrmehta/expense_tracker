package org.example.controller;

import org.example.Entity.User;
import org.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ViewController {

    @Autowired
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public ViewController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    // Landing: redirect to login
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // Show login form; handle login error via Spring Security
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
        }
        return "login";
    }

    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password.");
        }
        return "login";
    }

    // Display signup form
    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    // Handle signup submission
    @PostMapping("/signup")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("errorMessage", "Username already exists!");
            return "signup"; // return same page with error
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        return "redirect:/login";
    }



    // Optional: Logout redirect handler
    @GetMapping("/logout-success")
    public String logoutPage(Model model) {
        model.addAttribute("logoutMessage", "You've been logged out.");
        return "login";
    }
}
