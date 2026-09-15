package at.vergibtnix.finance_manager.dto;

import java.math.BigDecimal;

public class ReportSummary {

    private final String label;
    private final String periodText;
    private final BigDecimal income;
    private final BigDecimal expense;
    private final BigDecimal balance;

    public ReportSummary(String label, String periodText, BigDecimal income, BigDecimal expense, BigDecimal balance) {
        this.label = label;
        this.periodText = periodText;
        this.income = income;
        this.expense = expense;
        this.balance = balance;
    }

    public String getLabel() {
        return label;
    }

    public String getPeriodText() {
        return periodText;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public BigDecimal getExpense() {
        return expense;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}

