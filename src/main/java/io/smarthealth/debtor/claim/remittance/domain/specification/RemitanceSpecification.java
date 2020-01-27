package io.smarthealth.debtor.claim.remittance.domain.specification;

import io.smarthealth.appointment.domain.Appointment;
import io.smarthealth.debtor.claim.remittance.domain.enumeration.PaymentMode;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class RemitanceSpecification {

    public RemitanceSpecification() {
        super();
    }

    public static Specification<Appointment> createSpecification(Long payerId, Long bankId, Double balance, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
            if (payerId != null) {
                predicates.add(cb.equal(root.get("payer").get("id"), payerId));
            }
            if (bankId != null) {
                predicates.add(cb.equal(root.get("bankAccount").get("id"), bankId));
            }
            if (balance != null) {
                predicates.add(cb.greaterThan(root.get("balance").get("id"), balance));
            }
             if(range!=null){
                  predicates.add(
                     cb.between(root.get("appointmentDate"), range.getStartDateTime(), range.getEndDateTime())
                  );
              }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
