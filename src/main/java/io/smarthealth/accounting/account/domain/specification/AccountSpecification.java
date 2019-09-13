package io.smarthealth.accounting.account.domain.specification;

import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.accounting.account.domain.AccountType;
import io.smarthealth.accounting.account.domain.enumeration.AccountCategory;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class AccountSpecification {

    public AccountSpecification() {
        super();
    }

    public static Specification<Account> createSpecification(final boolean includeClosed, final String term, final AccountType type, String category) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (!includeClosed) {
                predicates.add(cb.equal(root.get("enabled"), true));
            }

            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("accountNumber"), likeExpression),
                                cb.like(root.get("accountName"), likeExpression)
                        )
                );
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("accountType"), type));
            }
            
            if (category != null) {
                predicates.add(cb.equal(root.get("accountType").get("glAccountType"), AccountCategory.valueOf(category.toUpperCase())));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
