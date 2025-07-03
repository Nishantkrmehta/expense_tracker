package org.example.controller;


import org.example.Entity.TransactionCategory;
import org.example.Entity.User;
import org.example.Service.TransactionCategoryService;
import org.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class TransactionCategoryController {

    @Autowired
    private TransactionCategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public List<TransactionCategory> getCategories(@PathVariable String username) {
        User user = userService.findByUsername(username).orElse(null);
        return (user != null) ? categoryService.getCategoriesForUser(user) : List.of();
    }

    @PostMapping
    public TransactionCategory createCategory(@RequestBody TransactionCategory category) {
        return categoryService.saveCategory(category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}

