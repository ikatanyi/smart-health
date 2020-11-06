package io.smarthealth.organization.person.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Formula;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Indexed
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
public class Person extends Auditable {

    @Column(length = 25)
    private String title;
    @Column(length = 50)
    @Field(termVector = TermVector.YES)
    private String givenName;
    @Column(length = 50)
    @Field(termVector = TermVector.YES)
    private String middleName;
    @Column(length = 50)
    @Field(termVector = TermVector.YES)
    private String surname;
    @Column(length = 1)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate dateOfBirth;

    //@Transient
    @Column(length = 50)
    private String maritalStatus;
    private LocalDate dateRegistered = LocalDate.now();

    @Formula("YEAR(CURDATE()) - YEAR(date_of_birth)")
    private int age;

    @Formula(value = " concat(COALESCE(given_name, ''), ' ', COALESCE(middle_name, ''), ' ', COALESCE(surname, '')) ")
    private String fullName;

    @OneToMany(mappedBy = "person")
    private List<PersonAddress> addresses;
    @OneToMany(mappedBy = "person")
    private List<PersonContact> contacts;
    @OneToMany(mappedBy = "person")
    private List<PersonNextOfKin> nok;

//    @Formula("case when exists (select * from patient p where p.patient_id = person_id) then 1 else 0 end")
    private boolean isPatient;
    @Field
    private String primaryContact;
    private String residence, religion, nationalIdNumber;

//    @OneToMany(mappedBy = "person")
//    private List<Biometrics> biometrics =new ArrayList<>();
//    @OneToMany(mappedBy = "person")
//    private List<ContactDetail> contactDetails=new ArrayList<>();
}
