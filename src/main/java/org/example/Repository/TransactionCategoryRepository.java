package org.example.Repository;


import org.example.Entity.TransactionCategory;
import org.example.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Long> {
    List<TransactionCategory> findByUser(User user);
}

