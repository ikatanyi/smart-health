package io.smarthealth.stock.inventory.domain.specification;

import io.smarthealth.stock.inventory.domain.InventoryItem;
import io.smarthealth.stock.stores.domain.Store;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
public class InventoryItemSpecification {

    public InventoryItemSpecification() {
        super();
    }

    public static Specification<InventoryItem> createSpecification(Long storeId, Long itemId, String search, Boolean includeClosed) {

        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (includeClosed == null) {
                predicates.add(cb.equal(root.get("item").get("active"), true));
            } else {
                if (!includeClosed) {
                    predicates.add(cb.equal(root.get("item").get("active"), true));
                }
            }

            if (itemId != null) {
                predicates.add(cb.equal(root.get("item").get("id"), itemId));
            }
            if (storeId != null) {
                predicates.add(cb.equal(root.get("store").get("id"), storeId));
            }

            if (search != null) {
                final String likeExpression = "%" + search + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("item").get("itemName"), likeExpression),
                                cb.like(root.get("item").get("itemCode"), likeExpression)
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
