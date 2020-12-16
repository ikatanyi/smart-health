package io.smarthealth.documents.domain.specification;

import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.documents.domain.Document;
import io.smarthealth.documents.domain.enumeration.DocumentType;
import io.smarthealth.documents.domain.enumeration.Status;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kennedy.Imbenzi
 */
public class DocumentSpecification {

    public DocumentSpecification() {
        super();
    }

    public static Specification<Document> createSpecification(String PatientNumber, DocumentType documentType, Status status, Long servicePointId, DateRange range, Visit visit, String fileName) {
        return (root, query, cb) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
 
            if (PatientNumber != null) {
                predicates.add(cb.equal(root.get("patient").get("patientNumber"), PatientNumber));
            }
            if (documentType != null) {
                predicates.add(cb.equal(root.get("accessNo"), documentType));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (servicePointId != null) {
                predicates.add(cb.equal(root.get("servicePoint").get("id"), servicePointId));
            }
             if(range!=null){
                  predicates.add(
                     cb.between(root.get("createdOn"), range.getStartDateTime(), range.getEndDateTime())
                  );
              }
             if(visit!=null){
                 predicates.add(cb.equal(root.get("visit"), visit));
             }
             if(fileName!=null){
                 predicates.add(cb.equal(root.get("fileName"), fileName));
             }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
