package org.example.Repository;

import org.example.Entity.Transaction;
import org.example.Entity.User;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // existing:
    List<Transaction> findByUser(User user);

    // add this:
    List<Transaction> findByUser(User user, Sort sort);

    Page<Transaction> findByUser(User user, Pageable pageable);
}

