package org.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.Entity.TransactionCategory;
import org.example.Entity.Transaction;
import org.example.Entity.User;
import org.example.Service.TransactionCategoryService;
import org.example.Service.TransactionService;
import org.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionCategoryService catService;

    @Autowired
    private TransactionService txService;

    @GetMapping("/dashboard")
    public String showDashboard(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        User user = userService.findByUsername(auth.getName()).orElseThrow();

        List<TransactionCategory> categories = catService.getCategoriesForUser(user);

        List<Transaction> allTxns = txService.getTransactionsForUser(user);

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allTxns.size());
        Page<Transaction> txPage = new PageImpl<>(allTxns.subList(start, end), pageable, allTxns.size());

        double totalIncome = allTxns.stream()
                .filter(t -> t.getType() == Transaction.Type.INCOME).mapToDouble(Transaction::getAmount).sum();

        double totalExpense = allTxns.stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE).mapToDouble(Transaction::getAmount).sum();

        model.addAttribute("username", user.getUsername());
        model.addAttribute("categories", categories);
        model.addAttribute("transactions", txPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", txPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);

        return "dashboard";
    }

    @PostMapping("/transaction/add")
    public String addTransaction(Authentication auth,
                                 @RequestParam String title,
                                 @RequestParam double amount,
                                 @RequestParam Transaction.Type type,
                                 @RequestParam Long categoryId) {

        User user = userService.findByUsername(auth.getName()).orElseThrow();
        TransactionCategory category = catService.getById(categoryId);

        Transaction tx = new Transaction();  // set manually
        tx.setDescription(title);
        tx.setAmount(amount);
        tx.setType(type);
        tx.setCategory(category);
        tx.setUser(user);

        txService.saveTransaction(tx);
        return "redirect:/dashboard";
    }

    @GetMapping("/transaction/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        txService.deleteTransaction(id);
        return "redirect:/dashboard";
    }

    @PostMapping("/category/add")
    public String addCategory(Authentication auth, @RequestParam String name) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        catService.saveCategory(new TransactionCategory(name, user));
        return "redirect:/dashboard";
    }

    @GetMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        catService.deleteCategory(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/transaction/edit/{id}")
    public String showEditTransaction(@PathVariable Long id, Model model) {
        Transaction tx = txService.getById(id);
        model.addAttribute("transaction", tx);
        model.addAttribute("categories", catService.getCategoriesForUser(tx.getUser()));
        return "edit-transaction";
    }

    @PostMapping("/transaction/edit")
    public String editTransaction(@ModelAttribute Transaction transaction) {
        txService.saveTransaction(transaction);
        return "redirect:/dashboard";
    }

    @GetMapping("/category/edit/{id}")
    public String showEditCategory(@PathVariable Long id, Model model) {
        TransactionCategory cat = catService.getById(id);
        model.addAttribute("category", cat);
        return "edit-category";
    }

    @PostMapping("/category/edit")
    public String editCategory(@ModelAttribute TransactionCategory category) {
        catService.saveCategory(category);
        return "redirect:/dashboard";
    }

    @GetMapping("/export/csv")
    public void exportCsv(Authentication auth, HttpServletResponse response) throws IOException {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        List<Transaction> transactions = txService.getTransactionsForUser(user);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.csv");

        PrintWriter writer = response.getWriter();
        writer.println("Title,Category,Type,Amount");

        for (Transaction tx : transactions) {
            writer.printf("%s,%s,%s,%.2f%n",
                    tx.getDescription().replace(",", " "),
                    tx.getCategory().getName(),
                    tx.getType(),
                    tx.getAmount()
            );
        }

        writer.flush();
    }
}
