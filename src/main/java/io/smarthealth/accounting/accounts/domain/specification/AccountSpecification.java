package io.smarthealth.accounting.accounts.domain.specification;
 
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.AccountState;
import io.smarthealth.accounting.accounts.domain.AccountType;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class AccountSpecification {

    private AccountSpecification() {
        super();
    }

    public static Specification<Account> createSpecification(
            final Boolean includeClosed, final String term, final AccountType type, final Boolean includeCustomerAccounts) {

        return (root, query, cb) -> {

            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (includeClosed!=null && !includeClosed) {
                predicates.add(
                        root.get("state").in(
                                AccountState.OPEN,
                                AccountState.LOCKED
                        )
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
