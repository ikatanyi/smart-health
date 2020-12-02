/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.moh.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import io.smarthealth.clinical.moh.data.MonthlyMobidity;
import io.smarthealth.clinical.moh.data.Register;

/**
 *
 * @author kent
 */
public interface MohRepository extends JpaRepository<Moh, Long>, JpaSpecificationExecutor<Moh> {

    Optional<Moh> findByDescriptionContainingIgnoreCase(String name);

    @Query(value = "SELECT"
            + "     m.description AS disease,"
            + "     COUNT(d.code) AS occurrences,"
            + "     case when DAYOFMONTH(d.created_on) = '1' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='1' AND d.certainty='Confirmed' AND pd.code=d.code)  else 0 end AS day1,"
            + "     case when DAYOFMONTH(d.created_on) = '2' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='2' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day2,"
            + "     case when DAYOFMONTH(d.created_on) = '3' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='3' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day3,"
            + "     case when DAYOFMONTH(d.created_on) = '4' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='4' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day4,"
            + "     case when DAYOFMONTH(d.created_on) = '5' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='5' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day5,"
            + "     case when DAYOFMONTH(d.created_on) = '6' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='6' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day6,"
            + "     case when DAYOFMONTH(d.created_on) = '7' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='7' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day7,"
            + "     case when DAYOFMONTH(d.created_on) = '8' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='8' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day8,"
            + "     case when DAYOFMONTH(d.created_on) = '9' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='9' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day9,"
            + "     case when DAYOFMONTH(d.created_on) = '10' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='10' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day10,"
            + "     case when DAYOFMONTH(d.created_on) = '11' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='11' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day11,"
            + "     case when DAYOFMONTH(d.created_on) = '12' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='12' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day12,"
            + "     case when DAYOFMONTH(d.created_on) = '13' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='13' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day13,"
            + "     case when DAYOFMONTH(d.created_on) = '14' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='14' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day14,"
            + "     case when DAYOFMONTH(d.created_on) = '15' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='15' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day15,"
            + "     case when DAYOFMONTH(d.created_on) = '16' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='16' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day16,"
            + "     case when DAYOFMONTH(d.created_on) = '17' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='17' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day17,"
            + "     case when DAYOFMONTH(d.created_on) = '18' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='18' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day18,"
            + "     case when DAYOFMONTH(d.created_on) = '19' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='19' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day19,"
            + "     case when DAYOFMONTH(d.created_on) = '20' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='20' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day20,"
            + "     case when DAYOFMONTH(d.created_on) = '21' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='21' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day21,"
            + "     case when DAYOFMONTH(d.created_on) = '22' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='22' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day22,"
            + "     case when DAYOFMONTH(d.created_on) = '23' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='23' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day23,"
            + "     case when DAYOFMONTH(d.created_on) = '24' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='24' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS  day24,"
            + "     case when DAYOFMONTH(d.created_on) = '25' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='25' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day25,"
            + "     case when DAYOFMONTH(d.created_on) = '26' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='26' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day26,"
            + "     case when DAYOFMONTH(d.created_on) = '27' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='27' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day27,"
            + "     case when DAYOFMONTH(d.created_on) = '28' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='28' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day28,"
            + "     case when DAYOFMONTH(d.created_on) = '29' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='29' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day29,"
            + "     case when DAYOFMONTH(d.created_on) = '30' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='30' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day30,"
            + "     case when DAYOFMONTH(d.created_on) = '31' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='31' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS  day31,"
            + "     d.id AS diagnosis_id,"
            + "     d.created_on AS created_on,"
            + "     m.code AS code,"
            + "     p.date_of_birth"
            + "   FROM"
            + "      moh m LEFT JOIN (patient_diagnosis d JOIN disease s ON d.code=s.code)  ON m.code = s.m_code LEFT JOIN person p ON d.patient_id = p.id AND m.active='1' AND d.certainty='Confirmed' AND m.category='MORBIDITY' AND  d.created_on BETWEEN :fromDate AND :toDate"
            + "  WHERE d.certainty='Confirmed' AND m.a705='true' AND (YEAR(NOW())-YEAR(p.date_of_birth))>=5"
            + "   GROUP BY DAYOFMONTH(d.created_on), m.code ORDER BY m.code ASC"  , nativeQuery=true)
    List<MonthlyMobidity> findMorbidityOver5(LocalDate fromDate, LocalDate toDate);
    
    @Query(value = "SELECT"
            + "     m.description AS disease,"
            + "     COUNT(d.code) AS occurrences,"
            + "     case when DAYOFMONTH(d.created_on) = '1' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='1' AND d.certainty='Confirmed' AND pd.code=d.code)  else 0 end AS day1,"
            + "     case when DAYOFMONTH(d.created_on) = '2' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='2' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day2,"
            + "     case when DAYOFMONTH(d.created_on) = '3' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='3' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day3,"
            + "     case when DAYOFMONTH(d.created_on) = '4' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='4' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day4,"
            + "     case when DAYOFMONTH(d.created_on) = '5' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='5' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day5,"
            + "     case when DAYOFMONTH(d.created_on) = '6' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='6' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day6,"
            + "     case when DAYOFMONTH(d.created_on) = '7' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='7' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day7,"
            + "     case when DAYOFMONTH(d.created_on) = '8' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='8' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day8,"
            + "     case when DAYOFMONTH(d.created_on) = '9' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='9' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day9,"
            + "     case when DAYOFMONTH(d.created_on) = '10' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='10' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day10,"
            + "     case when DAYOFMONTH(d.created_on) = '11' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='11' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day11,"
            + "     case when DAYOFMONTH(d.created_on) = '12' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='12' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day12,"
            + "     case when DAYOFMONTH(d.created_on) = '13' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='13' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day13,"
            + "     case when DAYOFMONTH(d.created_on) = '14' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='14' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day14,"
            + "     case when DAYOFMONTH(d.created_on) = '15' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='15' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day15,"
            + "     case when DAYOFMONTH(d.created_on) = '16' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='16' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day16,"
            + "     case when DAYOFMONTH(d.created_on) = '17' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='17' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day17,"
            + "     case when DAYOFMONTH(d.created_on) = '18' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='18' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day18,"
            + "     case when DAYOFMONTH(d.created_on) = '19' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='19' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day19,"
            + "     case when DAYOFMONTH(d.created_on) = '20' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='20' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day20,"
            + "     case when DAYOFMONTH(d.created_on) = '21' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='21' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day21,"
            + "     case when DAYOFMONTH(d.created_on) = '22' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='22' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day22,"
            + "     case when DAYOFMONTH(d.created_on) = '23' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='23' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day23,"
            + "     case when DAYOFMONTH(d.created_on) = '24' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='24' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS  day24,"
            + "     case when DAYOFMONTH(d.created_on) = '25' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='25' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day25,"
            + "     case when DAYOFMONTH(d.created_on) = '26' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='26' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day26,"
            + "     case when DAYOFMONTH(d.created_on) = '27' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='27' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day27,"
            + "     case when DAYOFMONTH(d.created_on) = '28' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='28' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day28,"
            + "     case when DAYOFMONTH(d.created_on) = '29' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='29' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day29,"
            + "     case when DAYOFMONTH(d.created_on) = '30' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='30' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS day30,"
            + "     case when DAYOFMONTH(d.created_on) = '31' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='31' AND d.certainty='Confirmed' AND pd.code=d.code) else 0 end AS  day31,"
            + "     d.id AS diagnosis_id,"
            + "     d.created_on AS created_on,"
            + "     m.code AS code,"
            + "     p.date_of_birth"
            + "   FROM"
            + "      moh m LEFT JOIN (patient_diagnosis d JOIN disease s ON d.code=s.code)  ON m.code = s.m_code LEFT JOIN person p ON d.patient_id = p.id AND m.active='1' AND d.certainty='Confirmed' AND m.category='MORBIDITY' AND  d.created_on BETWEEN :fromDate AND :toDate"
            + "  WHERE d.certainty='Confirmed' AND m.b705='true' AND (YEAR(NOW())-YEAR(p.date_of_birth))<=5"
            + "   GROUP BY DAYOFMONTH(d.created_on), m.code ORDER BY m.code ASC"   , nativeQuery=true)
    List<MonthlyMobidity> findMorbidityUnder5(LocalDate fromDate, LocalDate toDate);
   
    
    

}
