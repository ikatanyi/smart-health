/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.employeespecialization.data;

import io.smarthealth.administration.employeespecialization.data.enums.EmployeeCategory;
import io.smarthealth.administration.employeespecialization.domain.EmployeeSpecialization;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */ 
@Data
public class EmployeeSpecializationData {

    @ApiModelProperty(hidden = true)
    private Long specializationId;

    @Enumerated(EnumType.STRING)
    private EmployeeCategory.Category category;

    private String specialization;

    public static EmployeeSpecializationData map(EmployeeSpecialization e) {
        EmployeeSpecializationData d = new EmployeeSpecializationData();
        d.setCategory(e.getCategory());
        d.setSpecialization(e.getSpecialization());
        d.setSpecializationId(e.getId());
        return d;
    }

    public static EmployeeSpecialization map(EmployeeSpecializationData d) {
        EmployeeSpecialization e = new EmployeeSpecialization();
        e.setCategory(d.getCategory());
        e.setSpecialization(d.getSpecialization());
        return e;
    }
}
