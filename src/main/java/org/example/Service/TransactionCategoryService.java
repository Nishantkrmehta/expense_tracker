package org.example.Service;


import org.example.Entity.TransactionCategory;
import org.example.Entity.User;
import org.example.Repository.TransactionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionCategoryService {

    @Autowired
    private TransactionCategoryRepository categoryRepository;

    public List<TransactionCategory> getCategoriesForUser(User user) {
        return categoryRepository.findByUser(user);
    }

    public TransactionCategory saveCategory(TransactionCategory category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    public TransactionCategory getById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }
}

