package io.smarthealth.accounting.invoice.domain.specification;


import io.smarthealth.accounting.invoice.domain.Invoice;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class InvoiceSpecification {

  private InvoiceSpecification() {
    super();
  }

  public static Specification<Invoice> createSpecification( String customer,String invoice ) {

    return (root, query, cb) -> {

      final ArrayList<Predicate> predicates = new ArrayList<>();
  

      if (customer != null) {
        predicates.add(cb.equal(root.get("payer"), customer));
      } 
        if (invoice != null) {
        predicates.add(cb.equal(root.get("number"), invoice));
      } 
//          if (receipt != null) {
//        predicates.add(cb.equal(root.get("receiptNo"), receipt));
//      } 

      return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    };
  }
}
