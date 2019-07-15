package io.smarthealth.administration.domain;

import io.smarthealth.common.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 * Represents All know international Medical Standards codes
 *
 * @author Kelsas
 */
@Entity
@Data
public class Codes extends Identifiable {

    private String code;
    private String codeText;
    private String codeTextShort; //short code

    @ManyToOne
    private CodeType codeType;

    private boolean active = true;
 
}
