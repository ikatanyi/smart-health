package io.smarthealth.clinical.lab.data;

import io.smarthealth.clinical.lab.domain.*;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ContainerData {
    
    private Long id;
    private String code;
    private String container;
}
