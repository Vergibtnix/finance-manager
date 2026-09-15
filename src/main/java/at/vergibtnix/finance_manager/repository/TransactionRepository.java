package at.vergibtnix.finance_manager.repository;

import at.vergibtnix.finance_manager.entity.Transaction;
import at.vergibtnix.finance_manager.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    // ---- Gesamtstatistik ----
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE (:type IS NULL OR t.type = :type)")
    BigDecimal sumByType(@Param("type") TransactionType type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE (:type IS NULL OR t.type = :type) AND t.date BETWEEN :start AND :end")
    BigDecimal sumByTypeAndPeriod(@Param("type") TransactionType type,
                                  @Param("start") LocalDate start,
                                  @Param("end") LocalDate end);

    @Query("SELECT DISTINCT t.category FROM Transaction t ORDER BY t.category ASC")
    List<String> findDistinctCategories();
}
