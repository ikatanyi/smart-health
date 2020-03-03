/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.data;

import io.smarthealth.administration.employeespecialization.data.enums.EmployeeCategory.Category;
import io.smarthealth.organization.person.data.PersonData;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class EmployeeData extends PersonData {

    @ApiModelProperty(hidden = true)
    private Long employeeId;
    @Enumerated(EnumType.STRING)
    private Category employeeCategory;
    private String departmentName;
    private DepartmentData department;
    private DepartmentUnitData departmentUnit;
    private String departmentCode;
    private String status;
    private String staffNumber;

    private String email;
    private String mobile;
    private String telephone;
    private String specialization;
    private String licenseNo;
    private String username;
    private String text, value;

}
