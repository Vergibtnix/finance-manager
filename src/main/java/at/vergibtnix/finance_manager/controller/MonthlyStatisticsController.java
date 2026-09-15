package at.vergibtnix.finance_manager.controller;

import at.vergibtnix.finance_manager.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

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
        YearMonth now = YearMonth.now();

        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();

        YearMonth selectedMonth = YearMonth.of(year, month);

        var income = service.getMonthlyIncome(selectedMonth);
        var expense = service.getMonthlyExpense(selectedMonth);
        var balance = service.getMonthlyBalance(selectedMonth);

        model.addAttribute("income", income);
        model.addAttribute("expense", expense);
        model.addAttribute("balance", balance);
        model.addAttribute("periodLabel", selectedMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN)
                + " " + selectedMonth.getYear());

        model.addAttribute("year", year);
        model.addAttribute("month", month);

        model.addAttribute("page", "monthly-stats");

        return "monthly-statistics";
    }
}
