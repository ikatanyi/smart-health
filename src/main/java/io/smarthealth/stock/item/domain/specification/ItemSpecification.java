package io.smarthealth.stock.item.domain.specification;

import io.smarthealth.accounting.account.domain.specification.*;
import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.accounting.account.domain.AccountType;
import io.smarthealth.accounting.account.domain.enumeration.AccountCategory;
import io.smarthealth.stock.item.domain.Item;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class ItemSpecification {

    public ItemSpecification() {
        super();
    }

    public static Specification<Item> createSpecification(final boolean includeClosed, final String term) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (!includeClosed) {
                predicates.add(cb.equal(root.get("active"), true));
            }

            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("itemName"), likeExpression),
                                cb.like(root.get("itemCode"), likeExpression)
                        )
                );
            } 
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
