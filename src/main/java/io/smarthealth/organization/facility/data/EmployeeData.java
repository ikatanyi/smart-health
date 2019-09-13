/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.data.AddressData;
import io.smarthealth.organization.person.data.ContactData;
import io.smarthealth.organization.person.data.PersonData;
import io.smarthealth.organization.person.domain.Person;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.smarthealth.organization.person.domain.enumeration.MaritalStatus;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
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
public class EmployeeData extends PersonData {

    @Enumerated(EnumType.STRING)
    private Employee.Category employeeCategory;
    private String departmentName;
    private DepartmentData department;
    private DepartmentUnitData departmentUnit;
    private String status;
    private String staffNumber;

}
