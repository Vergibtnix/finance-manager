package at.vergibtnix.finance_manager.controller;

import at.vergibtnix.finance_manager.entity.Transaction;
import at.vergibtnix.finance_manager.entity.TransactionType;
import at.vergibtnix.finance_manager.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping("/transactions")
    public String listTransactions(@RequestParam(defaultValue = "0") int page,
                                   Model model) {

        int pageSize = 20; // 20 Einträge pro Seite

        Page<Transaction> transactionPage = service.findPaginated(page, pageSize);

        model.addAttribute("transactions", transactionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionPage.getTotalPages());
        model.addAttribute("page", "list");

        return "transactions-list";
    }

    @GetMapping("/transactions/new")
    public String newTransactionForm(Model model) {
        Transaction t = new Transaction();
        t.setDate(LocalDate.now());
        t.setType(TransactionType.EINNAHME);

        model.addAttribute("transaction", t);
        model.addAttribute("page", "new");

        return "transactions-form";
    }

    @PostMapping("/transactions")
    public String createTransaction(@ModelAttribute("transaction") Transaction transaction) {
        service.save(transaction);
        return "redirect:/transactions";
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        model.addAttribute("income", service.getIncomeSum());
        model.addAttribute("expense", service.getExpenseSum());
        model.addAttribute("balance", service.getBalance());
        model.addAttribute("page", "stats");

        return "statistics";
    }

    @GetMapping("/transactions/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/transactions";
    }

    @GetMapping("/transactions/edit/{id}")
    public String editTransaction(@PathVariable Long id, Model model) {
        Transaction t = service.findById(id);
        model.addAttribute("transaction", t);
        model.addAttribute("page", "edit");
        return "transactions-form";
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("page", "home");
        return "index";
    }
}
