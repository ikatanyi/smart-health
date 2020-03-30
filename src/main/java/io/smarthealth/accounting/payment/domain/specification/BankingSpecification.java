package io.smarthealth.accounting.payment.domain.specification;

import io.smarthealth.accounting.payment.domain.Banking;
import io.smarthealth.accounting.payment.domain.enumeration.BankingType;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class BankingSpecification {

    private BankingSpecification() {
        super();
    }

    public static Specification<Banking> createSpecification(String accountNumber, String client, String referenceNumber, String transactionNo, BankingType transactionType, DateRange range) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (client != null) {
                predicates.add(cb.equal(root.get("client"), client));
            }
            if (referenceNumber != null) {
                predicates.add(cb.equal(root.get("referenceNumber"), referenceNumber));
            }
            if (accountNumber != null) {
                predicates.add(cb.equal(root.get("bankAccount").get("accountNumber"), accountNumber));
            }
            if (transactionNo != null) {
                predicates.add(cb.equal(root.get("transactionNo"), transactionNo));
            }
            if (transactionType != null) {
                predicates.add(cb.equal(root.get("transactionType"), transactionType));
            }

            if (range != null) {
                predicates.add(
                        cb.between(root.get("date"), range.getStartDate(), range.getEndDate())
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
