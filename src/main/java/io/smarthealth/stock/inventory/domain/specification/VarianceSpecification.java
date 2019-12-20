/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.inventory.domain.specification;

import io.smarthealth.stock.inventory.domain.InventoryItem;
import io.smarthealth.stock.inventory.domain.InventoryVariance;
import io.smarthealth.stock.stores.domain.Store;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kennedy.Imbenzi
 */
public class VarianceSpecification {

    public VarianceSpecification() {
        super();
    }

    public static Specification<InventoryVariance> createSpecification(final LocalDate from, final LocalDate to, final Store store/*, Date from , Date to*/) {
        
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();

            if (from != null) {
                predicates.add(cb.greaterThan(root.get("dateRecorded"), from));
            }

            if (to != null) {
                predicates.add(cb.lessThan(root.get("dateRecorded"), to));
            }

            if (store != null) {
                predicates.add(cb.equal(root.get("store"), store));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
