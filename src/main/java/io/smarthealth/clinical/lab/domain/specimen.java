package io.smarthealth.clinical.lab.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
//@NamedQuery(name="tets",query = "SELECT e FROM Analyte e WHERE e.testType = :testType AND e.sex = :gender AND :age BETWEEN e.startAge and e.endAge ")
@Table(name = "lab_specimen")
public class specimen extends Identifiable {
    
    private Long id;
    private String specimen;
     private String code;
    private String abbreviation;
}
