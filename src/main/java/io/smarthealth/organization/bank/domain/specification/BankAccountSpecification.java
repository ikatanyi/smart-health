package io.smarthealth.appointment.domain.specification;

import io.smarthealth.appointment.domain.Appointment;
import io.smarthealth.debtor.claim.remittance.domain.enumeration.PaymentMode;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.bank.domain.enumeration.BankType;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class BankAccountSpecification {

    public BankAccountSpecification() {
        super();
    }

    public static Specification<BankAccount> createSpecification(String bankName, String bankBranch, BankType type) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
            if (bankName != null) {
                predicates.add(cb.equal(root.get("bankName"), bankName));
            }
            if (bankBranch != null) {
                predicates.add(cb.equal(root.get("bankBranch"), bankBranch));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
