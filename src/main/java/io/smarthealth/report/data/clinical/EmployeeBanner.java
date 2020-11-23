/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.clinical;

import io.smarthealth.organization.facility.data.EmployeeData;
import lombok.Data;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Data
public class EmployeeBanner {

    private String gender;
    private String employeeCategory;
    private String staffNumber;
    private String departmentName;
    private String departmentCode;
    private String email;
    private String status;
    private String address;
    private String mobile;
    private String phoneNumber;
    private String fullName;

    public static EmployeeBanner map(EmployeeData data) {
        EmployeeBanner employee = new EmployeeBanner();
        if (data.getAddress() != null) {
            employee.setAddress(data.getAddress().get(0).getPostalCode() + "," + data.getAddress().get(0).getCounty() + "," + data.getAddress().get(0).getCountry() + ", " + data.getAddress().get(0).getTown());
        }
        employee.setDepartmentCode(data.getDepartmentCode());
        employee.setDepartmentName(data.getDepartmentName());
        employee.setEmail(data.getEmail());
        if (data.getEmployeeCategory() != null) {
            employee.setEmployeeCategory(data.getEmployeeCategory().name());
        }
        employee.setStaffNumber(data.getStaffNumber());
        if (data.getContact() != null) {
            employee.setEmail(data.getContact().get(0).getEmail());
            employee.setPhoneNumber(data.getContact().get(0).getMobile());
        }
        employee.setFullName(data.getFullName());
        employee.setGender(data.getGender().name());
        employee.setStatus(data.getStatus());
        return employee;
    }
}
