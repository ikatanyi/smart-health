package io.smarthealth.clinical.record.domain;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

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
