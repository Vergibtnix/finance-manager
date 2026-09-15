package at.vergibtnix.finance_manager;

import at.vergibtnix.finance_manager.entity.Transaction;
import at.vergibtnix.finance_manager.entity.TransactionType;
import at.vergibtnix.finance_manager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionFeatureTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
    }

    @Test
    void transactionsCanBeFilteredByCategoryAndSearch() throws Exception {
        transactionRepository.save(createTransaction(
                LocalDate.of(2026, 9, 1),
                "Gehalt",
                "#198754",
                new BigDecimal("3200.00"),
                TransactionType.EINNAHME,
                "Bonus September"
        ));
        transactionRepository.save(createTransaction(
                LocalDate.of(2026, 9, 2),
                "Miete",
                "#dc3545",
                new BigDecimal("950.00"),
                TransactionType.AUSGABE,
                "Wohnungsmiete"
        ));

        mockMvc.perform(get("/transactions")
                        .with(user("tester").roles("USER"))
                        .param("category", "Gehalt")
                        .param("search", "Bonus")
                        .param("sortField", "amount")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bonus September")))
                .andExpect(content().string(not(containsString("Wohnungsmiete"))));
    }

    @Test
    void existingTransactionCanBeUpdated() throws Exception {
        Transaction transaction = transactionRepository.save(createTransaction(
                LocalDate.of(2026, 9, 10),
                "Freizeit",
                "#d63384",
                new BigDecimal("40.00"),
                TransactionType.AUSGABE,
                "Kino"
        ));

        mockMvc.perform(post("/transactions")
                        .with(user("tester").roles("USER"))
                        .with(csrf())
                        .param("id", transaction.getId().toString())
                        .param("date", "2026-09-10")
                        .param("amount", "55.50")
                        .param("type", "AUSGABE")
                        .param("category", "Freizeit")
                        .param("categoryColor", "#1111aa")
                        .param("description", "Kino und Snacks"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions"));

        Transaction updated = transactionRepository.findById(transaction.getId())
                .orElseThrow(() -> new AssertionError("Aktualisierte Transaktion nicht gefunden."));

        assertEquals(new BigDecimal("55.50"), updated.getAmount());
        assertEquals("Kino und Snacks", updated.getDescription());
        assertEquals("#1111aa", updated.getCategoryColor());
    }

    @Test
    void statisticsPagesShowReportSections() throws Exception {
        transactionRepository.save(createTransaction(
                LocalDate.now(),
                "Vermietung",
                "#0d6efd",
                new BigDecimal("1200.00"),
                TransactionType.EINNAHME,
                "Mieteinnahme"
        ));

        mockMvc.perform(get("/statistics")
                        .with(user("tester").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Täglicher Bericht")))
                .andExpect(content().string(containsString("Wöchentlicher Bericht")))
                .andExpect(content().string(containsString("Monatlicher Bericht")));

        mockMvc.perform(get("/statistics/monthly")
                        .with(user("tester").roles("USER"))
                        .param("year", String.valueOf(LocalDate.now().getYear()))
                        .param("month", String.valueOf(LocalDate.now().getMonthValue())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Monatsstatistik")));
    }

    private Transaction createTransaction(LocalDate date,
                                          String category,
                                          String categoryColor,
                                          BigDecimal amount,
                                          TransactionType type,
                                          String description) {
        Transaction transaction = new Transaction();
        transaction.setDate(date);
        transaction.setCategory(category);
        transaction.setCategoryColor(categoryColor);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDescription(description);
        return transaction;
    }
}


