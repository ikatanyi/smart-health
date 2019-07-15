package io.smarthealth.organization.domain;

import io.smarthealth.common.domain.BaseMetadata;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "organization_service_point")
public class ServicePoint  extends BaseMetadata{

    private String description;
    
    @ManyToOne
    private Facility facility;
    
}
