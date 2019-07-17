package io.smarthealth.organization.facility.domain;

import io.smarthealth.infrastructure.domain.SetupMetadata;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility_department")
public class Department extends SetupMetadata{
    private String departmentCode;
    
}
