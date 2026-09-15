package at.vergibtnix.finance_manager.service;

import at.vergibtnix.finance_manager.dto.ReportSummary;
import at.vergibtnix.finance_manager.dto.TransactionFilter;
import at.vergibtnix.finance_manager.entity.Transaction;
import at.vergibtnix.finance_manager.entity.TransactionType;
import at.vergibtnix.finance_manager.repository.TransactionRepository;
import at.vergibtnix.finance_manager.specification.TransactionSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class TransactionService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Map<String, String> CATEGORY_COLOR_SUGGESTIONS = createCategoryColorSuggestions();

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Iterable<Transaction> findAll() {
        return repository.findAll();
    }

    public void save(Transaction transaction) {
        if (!StringUtils.hasText(transaction.getCategoryColor())) {
            transaction.setCategoryColor(resolveSuggestedColor(transaction.getCategory()));
        }

        repository.save(transaction);
    }

    public BigDecimal getIncomeSum() {
        return repository.sumByType(TransactionType.EINNAHME);
    }

    public BigDecimal getExpenseSum() {
        return repository.sumByType(TransactionType.AUSGABE);
    }

    public BigDecimal getBalance() {
        return getIncomeSum().subtract(getExpenseSum());
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Transaction findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    public Page<Transaction> findPaginated(TransactionFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, buildSort(filter));
        return repository.findAll(TransactionSpecifications.withFilter(filter), pageable);
    }

    public List<String> getAvailableCategories() {
        return repository.findDistinctCategories();
    }

    public Map<String, String> getSuggestedCategoryColors() {
        return CATEGORY_COLOR_SUGGESTIONS;
    }

    public String resolveSuggestedColor(String category) {
        if (!StringUtils.hasText(category)) {
            return "#6c757d";
        }

        return CATEGORY_COLOR_SUGGESTIONS.getOrDefault(category.trim(), "#6c757d");
    }

    public BigDecimal getIncomeForPeriod(LocalDate start, LocalDate end) {
        return repository.sumByTypeAndPeriod(TransactionType.EINNAHME, start, end);
    }

    public BigDecimal getExpenseForPeriod(LocalDate start, LocalDate end) {
        return repository.sumByTypeAndPeriod(TransactionType.AUSGABE, start, end);
    }

    public BigDecimal getBalanceForPeriod(LocalDate start, LocalDate end) {
        return getIncomeForPeriod(start, end).subtract(getExpenseForPeriod(start, end));
    }

    public BigDecimal getMonthlyIncome(int year, int month) {
        return getMonthlyIncome(YearMonth.of(year, month));
    }

    public BigDecimal getMonthlyExpense(int year, int month) {
        return getMonthlyExpense(YearMonth.of(year, month));
    }

    public BigDecimal getMonthlyBalance(int year, int month) {
        return getMonthlyBalance(YearMonth.of(year, month));
    }

    public BigDecimal getMonthlyIncome(YearMonth yearMonth) {
        return getIncomeForPeriod(yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    public BigDecimal getMonthlyExpense(YearMonth yearMonth) {
        return getExpenseForPeriod(yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    public BigDecimal getMonthlyBalance(YearMonth yearMonth) {
        return getBalanceForPeriod(yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    public ReportSummary buildDailyReport() {
        LocalDate today = LocalDate.now();
        return buildReport("Täglicher Bericht", today, today, DATE_FORMATTER.format(today));
    }

    public ReportSummary buildWeeklyReport() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        return buildReport(
                "Wöchentlicher Bericht",
                startOfWeek,
                endOfWeek,
                DATE_FORMATTER.format(startOfWeek) + " - " + DATE_FORMATTER.format(endOfWeek)
        );
    }

    public ReportSummary buildMonthlyReport() {
        YearMonth currentMonth = YearMonth.now();
        return buildReport(
                "Monatlicher Bericht",
                currentMonth.atDay(1),
                currentMonth.atEndOfMonth(),
                currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN) + " " + currentMonth.getYear()
        );
    }

    private ReportSummary buildReport(String label, LocalDate start, LocalDate end, String periodText) {
        BigDecimal income = getIncomeForPeriod(start, end);
        BigDecimal expense = getExpenseForPeriod(start, end);
        return new ReportSummary(label, periodText, income, expense, income.subtract(expense));
    }

    private Sort buildSort(TransactionFilter filter) {
        String requestedField = filter.getSortField();
        String sortField = switch (requestedField == null ? "date" : requestedField) {
            case "amount" -> "amount";
            case "category" -> "category";
            default -> "date";
        };

        Sort.Direction direction = "asc".equalsIgnoreCase(filter.getSortDir())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, sortField).and(Sort.by(Sort.Direction.DESC, "id"));
    }

    private static Map<String, String> createCategoryColorSuggestions() {
        Map<String, String> categoryColors = new LinkedHashMap<>();
        categoryColors.put("Gehalt", "#198754");
        categoryColors.put("Vermietung", "#0d6efd");
        categoryColors.put("Lebensmittel", "#fd7e14");
        categoryColors.put("Miete", "#dc3545");
        categoryColors.put("KFZ-Kosten", "#6f42c1");
        categoryColors.put("Freizeit", "#d63384");
        categoryColors.put("Gesundheit", "#20c997");
        categoryColors.put("Sonstiges", "#6c757d");
        return categoryColors;
    }
}
