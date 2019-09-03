package io.smarthealth.financial.account.domain.specification;

import io.smarthealth.financial.account.domain.Ledger;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class LedgerSpecification {

    private LedgerSpecification() {
        super();
    }

    public static Specification<Ledger> createSpecification(
            final boolean includeSubLedger, final String term, final String type) {
        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (!includeSubLedger) {
                predicates.add(
                        cb.isNull(root.get("parentLedger"))
                );
            }

            if (term != null) {
                final String likeExpression = "%" + term + "%";

                predicates.add(
                        cb.or(
                                cb.like(root.get("identifier"), likeExpression),
                                cb.like(root.get("name"), likeExpression)
                        )
                );
            }

            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
