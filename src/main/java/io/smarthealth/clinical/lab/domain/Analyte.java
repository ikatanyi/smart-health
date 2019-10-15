package io.smarthealth.clinical.lab.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
//@NamedQuery(name="tets",query = "SELECT e FROM Analyte e WHERE e.testType = :testType AND e.sex = :gender AND :age BETWEEN e.startAge and e.endAge ")
@Table(name = "test_analyte")
public class Analyte extends Identifiable {
    
    private Long id;
    private String testCode;
    private String testName;
    private String gender;
    private String startAge;
    private String endAge;
    private Double lowerRange;
    private Double upperRange;
    private String units;
    private String category;
    private String description;   
    
    @ManyToOne                           
    private Testtype testType;
}
