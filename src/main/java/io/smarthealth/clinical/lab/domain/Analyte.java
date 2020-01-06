package io.smarthealth.clinical.lab.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
//@NamedQuery(name="tets",query = "SELECT e FROM Analyte e WHERE e.testType = :testType AND e.sex = :gender AND :age BETWEEN e.startAge and e.endAge ")
@Table(name = "lab_test_analyte"
        ,uniqueConstraints = {
    @UniqueConstraint(columnNames = {"analyteName"}, name="unique_analyte_name_test_type_id")})
public class Analyte extends Identifiable {

    public enum Gender {
        Male,
        Female,
        Both
    }
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String analyteName;
    private int startAge;
    private int endAge;
    private Double lowerRange;
    private Double upperRange;
    private String units;
    private String category;
    private String description;

    @ManyToOne
    private LabTestType testType;
}
