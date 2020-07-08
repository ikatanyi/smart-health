/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notify.domain.specification;

import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.notify.data.NoticeType;
import io.smarthealth.notify.domain.Notification;
import io.smarthealth.security.domain.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class NotificationSpecification {

    public static Specification<Notification> createSpecification(String username, Boolean isRead, NoticeType noticeType, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (username != null) {
                predicates.add(cb.equal(root.get("recipient").get("username"), username));
            }

            if (isRead != null) {
                predicates.add(cb.equal(root.get("isRead"), isRead));
            }

            if (noticeType != null) {
                predicates.add(cb.equal(root.get("noticeType"), noticeType));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("datetime"), range.getStartDateTime().toLocalDate(), range.getEndDateTime().toLocalDate())
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
