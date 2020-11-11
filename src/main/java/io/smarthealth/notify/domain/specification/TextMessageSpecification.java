/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notify.domain.specification;

import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.notify.domain.TextMessage;
import io.smarthealth.notify.domain.enumeration.ReceiverType;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kennedy.Ikatanyi
 */
public class TextMessageSpecification {

    public static Specification<TextMessage> createSpecification(String name, String status, String phoneNumber, ReceiverType type, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (type != null) {
                predicates.add(cb.equal(root.get("receiverType"), type));
            }

            if (phoneNumber != null) {
                predicates.add(cb.equal(root.get("phoneNumber"), phoneNumber));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("msgDate"), range.getStartDateTime().toLocalDate(), range.getEndDateTime().toLocalDate())
                );
            }
            if (name != null) {
                final String termExpression = "%" + name + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("name"), termExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
