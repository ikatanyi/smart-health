/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.employeespecialization.domain;

import io.smarthealth.administration.employeespecialization.data.enums.EmployeeCategory;
import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity 
public class EmployeeSpecialization extends Auditable {

    @Enumerated(EnumType.STRING)
    private EmployeeCategory.Category category;
    private String specialization;
}
