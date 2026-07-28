package at.vergibtnix.finance_manager.repository;

import at.vergibtnix.finance_manager.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // ---- Gesamtstatistik ----
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'EINNAHME'")
    double sumIncome();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'AUSGABE'")
    double sumExpense();

    // ---- Zeitraumstatistik ----
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.type = 'EINNAHME' AND t.date BETWEEN :start AND :end")
    double sumIncomeByPeriod(LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.type = 'AUSGABE' AND t.date BETWEEN :start AND :end")
    double sumExpenseByPeriod(LocalDate start, LocalDate end);

    // ---- Monatsstatistik ----
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.type = 'EINNAHME' AND YEAR(t.date) = :year AND MONTH(t.date) = :month")
    double sumIncomeByMonth(int year, int month);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.type = 'AUSGABE' AND YEAR(t.date) = :year AND MONTH(t.date) = :month")
    double sumExpenseByMonth(int year, int month);
}
