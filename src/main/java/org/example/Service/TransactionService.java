package org.example.Service;


import org.example.Entity.Transaction;
import org.example.Entity.User;
import org.example.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getTransactionsForUser(User user) {
        return transactionRepository.findByUser(user, Sort.by(Sort.Direction.DESC, "id"));
    }

    public Transaction saveTransaction(Transaction transaction) {
        Transaction saved = transactionRepository.save(transaction);
        System.out.println("✅ Saved transaction ID: " + saved.getId());
        return saved;
    }

    public void deleteTransaction(Long transactionId) {
        transactionRepository.deleteById(transactionId);
    }

    public Transaction getById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public Page<Transaction> getTransactionsForUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return transactionRepository.findByUser(user, pageable);
    }
}

