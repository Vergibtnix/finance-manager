package at.vergibtnix.finance_manager.controller;

import at.vergibtnix.finance_manager.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MonthlyStatisticsController {

    private final TransactionService service;

    public MonthlyStatisticsController(TransactionService service) {
        this.service = service;
    }

    @GetMapping("/statistics/monthly")
    public String monthlyStatistics(@RequestParam(required = false) Integer year,
                                    @RequestParam(required = false) Integer month,
                                    Model model) {

        // Standard: aktueller Monat
        java.time.YearMonth now = java.time.YearMonth.now();

        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();

        double income = service.getMonthlyIncome(year, month);
        double expense = service.getMonthlyExpense(year, month);
        double balance = service.getMonthlyBalance(year, month);

        model.addAttribute("income", income);
        model.addAttribute("expense", expense);
        model.addAttribute("balance", balance);

        model.addAttribute("year", year);
        model.addAttribute("month", month);

        model.addAttribute("page", "monthly-stats");

        return "monthly-statistics";
    }
}
