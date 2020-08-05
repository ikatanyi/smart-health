package io.smarthealth.clinical.admission.domain.specification;

import io.smarthealth.clinical.admission.domain.Room;
import io.smarthealth.clinical.admission.domain.Room.Type;
import io.smarthealth.clinical.admission.domain.Ward;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters for searching the data and filtering for the user
 *
 * @author Kennedy.ikatanyi
 */
public class RoomSpecification {

    public RoomSpecification() {
        super();
    }

    public static Specification<Room> createSpecification(final String name, final Type type, final boolean active, final Long wardId,  final String term) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (!active) {
                predicates.add(cb.equal(root.get("isActive"), true));
            }
            if (name!=null) {
                predicates.add(cb.equal(root.get("name"), name));
            }
            if (type!=null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if(wardId!=null){
                predicates.add(cb.equal(root.get("ward").get("id"), wardId));
            }

            if (term != null) {
                final String likeExpression = "%" + term + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("name"), likeExpression)
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
