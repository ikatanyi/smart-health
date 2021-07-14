package io.smarthealth.organization.person.patient.domain;

import io.smarthealth.organization.person.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
//public interface SummaryPatientRepository extends JpaRepository<SummaryPatient, Long> {
public interface SummaryPatientRepository extends CrudRepository<Person, Long> {

    @Query("SELECT COUNT(*) FROM Person")
    int findTotal();

    @Query("SELECT COUNT(*) FROM Person WHERE gender = 'M' AND (YEAR(CURDATE()) - YEAR(date_of_birth)) < 5")
    int findMaleUnder5();

    @Query("SELECT COUNT(*) FROM Person WHERE gender = 'M' AND (YEAR(CURDATE()) - YEAR(date_of_birth)) > 5")
    int findMaleAbove5();

    @Query("SELECT COUNT(*) FROM Person WHERE gender = 'F' AND (YEAR(CURDATE()) - YEAR(date_of_birth)) < 5")
    int findFemaleUnder5();

    @Query("SELECT COUNT(*) FROM Person WHERE gender = 'F' AND (YEAR(CURDATE()) - YEAR(date_of_birth)) > 5")
    int findFemaleAbove5();
}
