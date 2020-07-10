package io.smarthealth.clinical.record.domain;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Embeddable
@Data
public class Diagnosis implements Serializable {

    private String code;
    @Lob
    private String description;
}
