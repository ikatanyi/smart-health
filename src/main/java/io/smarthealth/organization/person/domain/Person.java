package io.smarthealth.organization.person.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
public class Person extends Auditable {

    @Column(length = 25)
    private String title;
    @Column(length = 50)
    private String givenName;
    @Column(length = 50)
    private String middleName;
    @Column(length = 50)
    private String surname;
    @Column(length = 1)
    private String gender;
    private LocalDate dateOfBirth;
    @Column(length = 50)
    private String maritalStatus;
    private LocalDate dateRegistered = LocalDate.now();

    @OneToMany(mappedBy = "person")
    private List<PersonAddress> addresses;
    @OneToMany(mappedBy = "person")
    private List<PersonContact> contacts;

//    @Formula("case when exists (select * from patient p where p.patient_id = person_id) then 1 else 0 end")
    private boolean isPatient;
    
    private String contactPerson;

//    @OneToMany(mappedBy = "person")
//    private List<Biometrics> biometrics =new ArrayList<>();
//    @OneToMany(mappedBy = "person")
//    private List<ContactDetail> contactDetails=new ArrayList<>();
}
