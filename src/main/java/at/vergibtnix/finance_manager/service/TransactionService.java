package at.vergibtnix.finance_manager.service;

import at.vergibtnix.finance_manager.entity.Transaction;
import at.vergibtnix.finance_manager.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.YearMonth;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Iterable<Transaction> findAll() {
        return repository.findAll();
    }

    public void save(Transaction t) {
        repository.save(t);
    }

    public double getIncomeSum() {
        return repository.sumIncome();
    }

    public double getExpenseSum() {
        return repository.sumExpense();
    }

    public double getBalance() {
        return getIncomeSum() - getExpenseSum();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Transaction findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    public Page<Transaction> findPaginated(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }




    // ---- Monatsstatistik ----

    public double getMonthlyIncome(int year, int month) {
        return repository.sumIncomeByMonth(year, month);
    }

    public double getMonthlyExpense(int year, int month) {
        return repository.sumExpenseByMonth(year, month);
    }

    public double getMonthlyBalance(int year, int month) {
        return getMonthlyIncome(year, month) - getMonthlyExpense(year, month);
    }

    public double getMonthlyIncome(YearMonth ym) {
        return 0;
    }

    public double getMonthlyExpense(YearMonth ym) {
        return 0;
    }

    public double getMonthlyBalance(YearMonth ym) {
        return 0;
    }

}
