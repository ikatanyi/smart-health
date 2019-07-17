package io.smarthealth.clinical.domain;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Embeddable
@Data
public class Diagnosis implements Serializable {

    private String code;
    private String description;
}
