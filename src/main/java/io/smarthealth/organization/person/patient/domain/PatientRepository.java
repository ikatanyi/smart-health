package io.smarthealth.organization.person.patient.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Simon.waweru
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient>, CustomizedPatientRepository {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM Patient c WHERE c.patientNumber = :patientNumber")
    Boolean existsByPatientNumber(@Param("patientNumber") final String patientNumber);

    Page<Patient> findByPatientNumberContainingOrGivenNameContainingOrSurnameContaining(
            final String patientNumber, final String givenName, final String surname, final Pageable pageable);

    Optional<Patient> findByPatientNumber(final String patientNumber);

    Optional<Patient> findById(final int id);

    Page<Patient> findByStatus(final String currentStatus, final Pageable pageable);

    @Query(value = "SELECT max(id) FROM Patient")
    public Integer maxId();

//    @Query("SELECT * FROM person")
//    public Integer male_under_5;
    
    //Testing this to easy dependency on JPA - Kelsas 2020-11-24 19:13
     @Query("select p from Patient p where p.givenName like lower(concat('%', ?1,'%')) or p.middleName like lower(concat('%', ?1,'%')) or p.surname like lower(concat('%', ?1,'%')) or p.patientNumber like lower(concat('%', ?1,'%'))")
    List<Patient> search(String searchValue);

}
