package io.smarthealth.accounting.doctors.domain.specification;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.doctors.data.DoctorInvoiceStatus;
import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class DoctorInvoiceSpecification {

    public DoctorInvoiceSpecification() {
        super();
    }

    public static Specification<DoctorInvoice> createSpecification(Long doctorId, String serviceItem, Boolean paid, String paymentMode, String patientNo, String invoiceNumber, String transactionId, DateRange range, DoctorInvoiceStatus invoiceStatus) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            if (doctorId != null) {
                predicates.add(cb.equal(root.get("doctor").get("id"), doctorId));
            }
            if (serviceItem != null) {
                final String likeExpression = "%" + serviceItem + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("serviceItem").get("serviceType").get("itemName"), likeExpression),
                                cb.like(root.get("serviceItem").get("serviceType").get("itemCode"), likeExpression)
                        )
                );
            }

            if (paid != null) {
                predicates.add(cb.equal(root.get("paid"), paid));
            }

            if (paymentMode != null) {
                predicates.add(cb.equal(root.get("paymentMode"), paymentMode));
            }

            if (patientNo != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), patientNo));
            }

            if (invoiceNumber != null) {
                predicates.add(cb.equal(root.get("invoiceNumber"), invoiceNumber));
            }

            if (transactionId != null) {
                predicates.add(cb.equal(root.get("transactionId"), transactionId));
            }

            if (range != null) {
                predicates.add(
                        cb.between(root.get("invoiceDate"), range.getStartDate(), range.getEndDate())
                );
            }

            if(invoiceStatus!=null){

                predicates.add(cb.equal(root.get("invoiceStatus"), invoiceStatus));
            }
 
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
