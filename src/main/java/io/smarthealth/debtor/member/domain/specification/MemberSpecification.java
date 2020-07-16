/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.member.domain.specification;

import io.smarthealth.debtor.member.domain.PayerMember;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author simz
 */
public class MemberSpecification {

    private MemberSpecification() {
        super();
    }

    public static Specification<PayerMember> createMemberSpecification(final Payer payer, final Scheme scheme, final String policyNo, final String term) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (scheme != null) {
                predicates.add(cb.equal(root.get("scheme"), scheme));
            }

            if (payer != null) {
                predicates.add(cb.equal(root.get("scheme").get("payer"), payer));
            }

            if (policyNo != null) {
                predicates.add(cb.equal(root.get("policyNo"), policyNo));
            }

            if (term != null) {
                final String termExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("memberName"), termExpression),
                                cb.like(root.get("policyNo"), termExpression),
                                cb.like(root.get("contactNo"), termExpression)
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
