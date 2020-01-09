/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.data;

import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.organization.facility.domain.Department;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class DepartmentData {

    
    @Enumerated(EnumType.STRING)
    private Department.Type type;
    @NotBlank
    @NotNull
    private String code;
    @NotBlank
    @NotNull
    private String name;
    private Long departmentId;

    private String facilityName;
    private Long facilityId;
    private Long parentId;
    private Long servicePointId;
    private String parentName;
//    private Long incomeAccountId;
//    private Long expenseAccountId;
    private String servicePointTypeName;
    private boolean isStore;
    private boolean active;

    @Enumerated(EnumType.STRING)
    private ServicePointType servicePointType;
}
