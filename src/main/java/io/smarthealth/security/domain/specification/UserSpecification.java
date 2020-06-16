package io.smarthealth.security.domain.specification;
 
import io.smarthealth.accounting.accounts.domain.specification.*;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.AccountState;
import io.smarthealth.accounting.accounts.domain.AccountType;
import io.smarthealth.security.domain.User;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    private UserSpecification() {
        super();
    }

    public static Specification<User> createSpecification(String search) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (search != null) {
                final String likeExpression = "%" + search + "%";
                predicates.add( cb.like(root.get("name"), likeExpression));
            } 
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
