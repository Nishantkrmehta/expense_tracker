package org.example.controller;

import org.example.Entity.Transaction;
import org.example.Entity.User;
import org.example.Service.TransactionService;
import org.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public List<Transaction> getTransactions(@PathVariable String username) {
        User user = userService.findByUsername(username).orElse(null);
        return (user != null) ? transactionService.getTransactionsForUser(user) : List.of();
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionService.saveTransaction(transaction);
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }
}

