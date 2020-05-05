package io.smarthealth.accounting.pricelist.domain.specification;

import io.smarthealth.accounting.pricelist.domain.PriceList;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class PriceListSpecification {

    public PriceListSpecification() {
        super();
    }

    public static Specification<PriceList> searchSpecification(String queryItem, Long servicePointId) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (queryItem != null) {
                final String likeExpression = "%" + queryItem + "%";
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

    public static Specification<PriceList> createSpecification(String queryItem, Long servicePointId, Boolean defaultPrice, List<ItemCategory> category, ItemType itemType) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (queryItem != null) {
                final String likeExpression = "%" + queryItem + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("item").get("itemName"), likeExpression),
                                cb.like(root.get("item").get("itemCode"), likeExpression)
                        )
                );
            }

            if (defaultPrice != null) {
                predicates.add(cb.equal(root.get("defaultPrice"), defaultPrice));
            }
//            if (category != null) {
//                predicates.add(cb.equal(root.get("item").get("category"), category));
//            }
//             predicates.add(root.get("status").in(status));
             if (category != null && !category.isEmpty()) {
                  predicates.add(root.get("item").get("category").in(category));
//                predicates.add(cb.equal(root.get("item").get("category"), category));
            }
             
            if (itemType != null) {
                predicates.add(cb.equal(root.get("item").get("itemType"), itemType));
            }
            if (servicePointId != null) {
                predicates.add(cb.equal(root.get("servicePoint").get("id"), servicePointId));
            }
//              if(names!=null && !names.isEmpty()) {
//                    predicates.add(root.get("employeeName").in(names));
//                }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
