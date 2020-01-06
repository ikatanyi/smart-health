/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.service;

import io.smarthealth.auth.domain.Role;
import io.smarthealth.auth.domain.User;
import io.smarthealth.auth.service.UserService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.mail.EmailData;
import io.smarthealth.infrastructure.mail.MailService;
import io.smarthealth.infrastructure.utility.PassayPassword;
import io.smarthealth.organization.facility.data.EmployeeData;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.DepartmentRepository;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.domain.EmployeeRepository;
import io.smarthealth.organization.person.domain.PersonContact;
import io.smarthealth.organization.person.patient.service.PersonContactService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class EmployeeService {

    /*
    a. Create a new  employee
    b. Read all  employees 
    c. Read employee by Id
    c. Update employee
    d. Fetch employee by number
    e. Fetch employee by category
     */
    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserService userService;

    @Autowired
    PersonContactService personContactService;

    @Autowired
    MailService mailSender;

    @Transactional
    public Employee createFacilityEmployee(Employee employee, PersonContact personContact) {
        //verify if exists
        if (employeeRepository.existsByStaffNumber(employee.getStaffNumber())) {
            throw APIException.conflict("Staff identified by number {0} already exists ", employee.getStaffNumber());
        }
        Employee savedEmployee = employeeRepository.save(employee);

        personContact.setPerson(employee);
        personContactService.createPersonContact(personContact);

        //save contact received as the primary contact
        //create a user 
        //find roles by group
        List<Role> roles = new ArrayList<>();
        //find roles by employee category/ employee group from the database
        List<String> employeeRoles = new ArrayList<>();
        for (String roleData : employeeRoles) {
            Role role = userService.findRoleByName(roleData)
                    .orElseThrow(
                            () -> APIException.notFound("No Role exisit with the name {0}", roleData)
                    );
            roles.add(role);
        }
        //fetch primary contact details
        PersonContact savedContact = personContactService.fetchPersonPrimaryContact(employee);

        //generate password
        String password = PassayPassword.generatePassayPassword();
        User user = new User(
                savedContact.getEmail(),
                savedContact.getEmail(),
                password,
                savedContact.getPerson().getGivenName().concat(" ").concat(savedContact.getPerson().getSurname()),
                roles
        );

        User userSaved = userService.saveUser(user);

        savedEmployee.setLoginAccount(userSaved);

        employeeRepository.save(savedEmployee);

        //send welcome message to the new system user
        mailSender.send(EmailData.of(user.getEmail(), "Registration Success", "<b>Welcome</b> " + personContact.getPerson().getGivenName().concat(" ").concat(personContact.getPerson().getSurname()).concat(". Your login credentials are <br/> username : " + savedContact.getEmail() + "<br/> password : " + password)));

        return savedEmployee;
    }

    public Page<Employee> fetchAllEmployees(final MultiValueMap<String, String> queryParams, final Pageable pg) {
        return employeeRepository.findAll(pg);
    }

    public List<Employee> findEmployeeByDepartment(final MultiValueMap<String, String> queryParams, final Department department, final Pageable pg) {
        return employeeRepository.findAllByDepartment(department, pg);
    }

    public List<Employee> findEmployeeByCategory(final MultiValueMap<String, String> queryParams, final String category, final Pageable pg) {
        return employeeRepository.findAllByEmployeeCategory(Employee.Category.valueOf(category), pg);
    }

    Page<Employee> fetchEmployeeByCategory(final String categoryName, final Pageable pg) {
        return employeeRepository.findByEmployeeCategory(categoryName, pg);
    }

    public Employee fetchEmployeeByUser(final User user) {
        return employeeRepository.findByLoginAccount(user).orElseThrow(() -> APIException.notFound("Employee identified by user {0} was not found ", user.getEmail()));
    }

    public Employee fetchEmployeeByAccountUsername(final String username) {
        final User user = userService.findUserByUsernameOrEmail(username).orElseThrow(() -> APIException.notFound("Account identified by username {0} was not found", username));
        return employeeRepository.findByLoginAccount(user).orElseThrow(() -> APIException.notFound("Employee identified by account username  {0} was not found ", username));
    }

    public Employee fetchEmployeeByNumberOrThrow(final String staffNumber) {
        return employeeRepository.findByStaffNumber(staffNumber).orElseThrow(() -> APIException.notFound("Employee identified by number {0} was not found ", staffNumber));
    }

    public Employee convertEmployeeDataToEntity(EmployeeData employeeData) {
        // use strict to prevent over eager matching (happens with ID fields)
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Employee employee = modelMapper.map(employeeData, Employee.class);
        System.out.println("employee " + employee.toString());
        return employee;
    }

    public Optional<Employee> findEmployeeByStaffNumber(final String staffNumber) {
        return employeeRepository.findByStaffNumber(staffNumber);
    }

    public EmployeeData convertEmployeeEntityToEmployeeData(Employee employee) {
        EmployeeData employeeData = modelMapper.map(employee, EmployeeData.class);
        employeeData.setDepartmentCode(employee.getDepartment().getCode());
        return employeeData;
    }

}
