/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.domain.specification;

import io.smarthealth.clinical.visit.domain.SpecialistChangeAudit;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author kennedy.ikatanyi
 */
public class SpecialistChangeAuditSpecification {

    private SpecialistChangeAuditSpecification() {
        super();
    }

    public static Specification<SpecialistChangeAudit> createSpecification(String doctorName,  DateRange dateRange) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (doctorName != null) {
                final String name = "%" + doctorName + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("fromDoctor"), name),
                                cb.like(root.get("toDoctor"), name)
                        )
                );
            }          

            if (dateRange != null) {
                predicates.add(
                        cb.between(
                                root.get("startDatetime"), dateRange.getStartDateTime(), dateRange.getEndDateTime()
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
