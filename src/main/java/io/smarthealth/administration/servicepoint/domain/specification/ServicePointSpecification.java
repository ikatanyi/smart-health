package io.smarthealth.administration.servicepoint.domain.specification;

import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePointsss;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ServicePointSpecification {

    private ServicePointSpecification() {
        super();
    }

    public static Specification<ServicePointsss> createSpecification(ServicePointType servicePointType, String pointType) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (pointType != null) {
                predicates.add(cb.equal(root.get("pointType"), pointType));
            }
            if (servicePointType != null) {
                predicates.add(cb.equal(root.get("servicePointType"), servicePointType));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
