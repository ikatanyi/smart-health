package io.smarthealth.stock.inventory.domain.specification;

import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class StockEntrySpecification {

    public StockEntrySpecification() {
        super();
    }

    public static Specification<StockEntry> createSpecification(Long storeId, Long itemId, String referenceNumber, String transactionId, String deliveryNumber, DateRange range, MovementPurpose purpose, MovementType moveType) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (storeId != null) {
                predicates.add(cb.equal(root.get("store").get("id"), storeId));
            }

            if (itemId != null) {
                predicates.add(cb.equal(root.get("item").get("id"), itemId));
            }

            if (transactionId != null) {
                predicates.add(cb.equal(root.get("transactionNumber"), transactionId));
            }
            if (deliveryNumber != null) {
                predicates.add(cb.equal(root.get("deliveryNumber"), deliveryNumber));
            }
            if (referenceNumber != null) {
                predicates.add(cb.equal(root.get("referenceNumber"), referenceNumber));
            }
            if (purpose != null) {
                predicates.add(cb.equal(root.get("purpose"), purpose));
            }
            if (moveType != null) {
                predicates.add(cb.equal(root.get("moveType"), moveType));
            }
            if (range != null) {
                predicates.add(
                        cb.between(root.get("transactionDate"), range.getStartDate(), range.getEndDate())
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<StockEntry> getStockMovement(Long storeId, Long itemId, DateRange range) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (storeId != null) {
                predicates.add(cb.equal(root.get("store").get("id"), storeId));
            }

            if (itemId != null) {
                predicates.add(cb.equal(root.get("item").get("id"), itemId));
            }

            if (range != null) {
                predicates.add(
                        cb.between(root.get("transactionDate"), range.getStartDate(), range.getEndDate())
                );
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
