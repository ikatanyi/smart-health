package io.smarthealth.accounting.accounts.domain.specification;

import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.infrastructure.lang.DateRange;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class JournalSpecification {

    public JournalSpecification() {
        super();
    }

    public static Specification<JournalEntry> createSpecification(String transactionNo, TransactionType transactionType, JournalState status, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (transactionNo != null) {
                predicates.add(cb.equal(root.get("transactionNo"), transactionNo));
            }

            if (transactionType != null) {
                predicates.add(cb.equal(root.get("transactionType"), transactionType));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("date"), range.getStartDateTime().toLocalDate(), range.getEndDateTime().toLocalDate())
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<JournalEntryItem> getTransactions(String accountNumber, LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (accountNumber != null) {
                predicates.add(cb.equal(root.get("account").get("identifier"), accountNumber));
            }

            predicates.add(
                    cb.between(root.get("journalEntry").get("date"), startDate, endDate)
            );
            
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}
