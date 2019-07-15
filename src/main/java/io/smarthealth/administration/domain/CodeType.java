package io.smarthealth.administration.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.Data;

/**
 * Medical Standards which includes: ICD10, LOINC,SNOMED,ICD10-PCS, CPT4
 *
 * @author Kelsas
 */
@Entity
@Data
public class CodeType implements Serializable {

    public enum Category {
        diagnosis,
        procedure,
        terminology,
        drug,
        problem
    }
    @Id
    private String ctKey;
    private String ctLabel;
    @Enumerated(EnumType.STRING)
    private Category ctCategory;
    private String ctActive;
    
    //a list of services
    
}
