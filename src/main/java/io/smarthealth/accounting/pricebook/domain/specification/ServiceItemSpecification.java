package io.smarthealth.accounting.pricebook.domain.specification;

import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.accounting.pricebook.domain.PriceBook;
import io.smarthealth.accounting.pricebook.domain.ServiceItem;
import io.smarthealth.accounting.pricebook.domain.enumeration.PriceCategory;
import io.smarthealth.accounting.pricebook.domain.enumeration.PriceType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kelsas
 */
public class ServiceItemSpecification {

    public ServiceItemSpecification() {
        super();
    }

    public static Specification<ServiceItem> createSpecification(String item, ServicePoint point,  boolean includeClosed) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (!includeClosed) {
                predicates.add(cb.equal(root.get("active"), true));
            }
            if (point != null) {
                predicates.add(cb.equal(root.get("mainService"), point));
            }  
            
              if (item != null) {
                final String likeExpression = "%" + item + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("serviceType").get("itemName"), likeExpression),
                                cb.like(root.get("serviceType").get("itemCode"), likeExpression)
                        )
                );
            }
              
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
