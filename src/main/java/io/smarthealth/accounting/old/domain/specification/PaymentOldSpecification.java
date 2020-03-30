package io.smarthealth.accounting.old.domain.specification;


import io.smarthealth.accounting.old.domain.FinancialTransaction;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class PaymentOldSpecification {

  private PaymentOldSpecification() {
    super();
  }

  public static Specification<FinancialTransaction> createSpecification( String customer,String invoice, String receipt ) {

    return (root, query, cb) -> {

      final ArrayList<Predicate> predicates = new ArrayList<>();
  

      if (customer != null) {
        predicates.add(cb.equal(root.get("payer"), customer));
      } 
        if (invoice != null) {
        predicates.add(cb.equal(root.get("invoice"), invoice));
      } 
          if (receipt != null) {
        predicates.add(cb.equal(root.get("receiptNo"), receipt));
      } 

      return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    };
  }
}
