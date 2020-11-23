package io.smarthealth.organization.facility.service;

import io.smarthealth.administration.employeespecialization.data.enums.EmployeeCategory.Category;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.notification.data.EmailData;
import io.smarthealth.notification.service.EmailerService;
import io.smarthealth.infrastructure.utility.PassayPassword;
import io.smarthealth.organization.facility.data.EmployeeData;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.DepartmentRepository;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.domain.EmployeeRepository;
import io.smarthealth.organization.person.domain.PersonContact;
import io.smarthealth.organization.person.patient.service.PersonContactService;
import io.smarthealth.security.domain.Role;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    EmailerService mailService;

    @Transactional
    public Employee createFacilityEmployee(Employee employee, PersonContact personContact, boolean createUserAccount, String[] roles) {
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
        //fetch primary contact details
        PersonContact savedContact = personContactService.fetchPersonPrimaryContact(employee);
        if (createUserAccount) {
            //generate password
            String password = PassayPassword.generatePassayPassword();
            User user = new User(
                    savedContact.getEmail(),
                    savedContact.getEmail(),
                    password,
                    savedContact.getPerson().getGivenName().concat(" ").concat(savedContact.getPerson().getSurname())
            );
            Set<Role> userRoles = new HashSet<>();

            for (String role : roles) {
                Role userRole = userService.findRoleByName(role)
                        .orElseThrow(() -> APIException.internalError("User Role not set."));
                userRoles.add(userRole);
            }
            user.setRoles(userRoles);

            User userSaved = userService.saveUser(user);

            savedEmployee.setLoginAccount(userSaved);

            employeeRepository.save(savedEmployee);
//.concat(" / ").concat(" ").concat(user.getUsername()
            //send welcome message to the new system user
            mailService.send(EmailData.of(user.getEmail(), "Registration Success", "<b>Welcome</b> " + personContact.getPerson().getGivenName().concat(" ").concat(personContact.getPerson().getSurname()).concat(". Your login credentials are <br/> username : " + userSaved.getUsername() + "<br/> password : " + password)));
        }
        return savedEmployee;
    }

    public Page<Employee> fetchAllEmployees(final MultiValueMap<String, String> queryParams, final Pageable pg) {
        return employeeRepository.findAll(pg);
    }

    public List<Employee> findEmployeeByDepartment(final MultiValueMap<String, String> queryParams, final Department department, final Pageable pg) {
        return employeeRepository.findAllByDepartment(department, pg);
    }

    public List<Employee> findEmployeeByCategory(final MultiValueMap<String, String> queryParams, final String category, final Pageable pg) {
        return employeeRepository.findAllByEmployeeCategory(Category.valueOf(category), pg);
    }

    Page<Employee> fetchEmployeeByCategory(final String categoryName, final Pageable pg) {
        return employeeRepository.findByEmployeeCategory(categoryName, pg);
    }

    public Employee fetchEmployeeByUser(final User user) {
        return employeeRepository.findByLoginAccount(user).orElseThrow(() -> APIException.notFound("Employee identified by user {0} was not found ", user.getEmail()));
    }
    public Optional<Employee> fetchEmployeeByUserWithoutFoundDetection(final User user) {
        return employeeRepository.findByLoginAccount(user);
    }

    public Employee findEmployeeByIdOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Employee with ID not found", id));
    }

    public Employee findEmployeeById(Long id) {
        if (id != null) {
            return employeeRepository.findById(id).orElseThrow(() -> APIException.notFound("Employee with ID not found", id));
        } else {
            return null;
        }
    }

    public Employee fetchEmployeeByAccountUsername(final String username) {
        final User user = userService.findUserByUsernameOrEmail(username).orElseThrow(() -> APIException.notFound("Account identified by username {0} was not found", username));
        return employeeRepository.findByLoginAccount(user).orElseThrow(() -> APIException.notFound("Employee identified by account username  {0} was not found ", username));
    }

    public Optional<Employee> findEmployeeByUsername(final String username) {
        return employeeRepository.findEmployeeBylogin(username);
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
        if (employee == null) {
            return null;
        }
        EmployeeData employeeData = modelMapper.map(employee, EmployeeData.class);
        employeeData.setEmployeeId(employee.getId());
        employeeData.setDepartmentCode(employee.getDepartment().getCode());
        if (employee.getLoginAccount() != null) {
            employeeData.setUsername(employee.getLoginAccount().getUsername());
        }
        return employeeData;
    }

    boolean containsWhitespace(String str) {
        return str.matches(".*\\s.*");
    }
}
