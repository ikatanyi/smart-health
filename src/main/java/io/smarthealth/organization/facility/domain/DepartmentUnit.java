 package io.smarthealth.organization.facility.domain;

import io.smarthealth.common.domain.SetupMetadata;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
 @Entity
 @Data 
 @Table(name = "facility_department_unit")
public class DepartmentUnit extends SetupMetadata{
    private String description;
}
