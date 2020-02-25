package io.smarthealth.accounting.doctors.domain.specification;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.doctors.domain.DoctorItem;
import io.smarthealth.accounting.doctors.service.DoctorItemService;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class DoctorItemSpecification {

    public DoctorItemSpecification() {
        super();
    }

    public static Specification<DoctorItem> createSpecification(Long doctorId, String service) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            if (doctorId != null) {
                predicates.add(cb.equal(root.get("doctor").get("id"), doctorId));
            }
            if (service != null) {
                final String likeExpression = "%" + service + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("serviceType").get("itemName"), likeExpression),
                                cb.like(root.get("serviceType").get("itemCode"), likeExpression)
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
