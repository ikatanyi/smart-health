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
import org.springframework.data.repository.query.Param;

/**
 *
 * @author kent
 */
public interface MohRepository extends JpaRepository<Moh, Long>, JpaSpecificationExecutor<Moh> {

    Optional<Moh> findByDescriptionContainingIgnoreCase(String name);

    @Query(value = "SELECT"
            + "     m.description AS disease,"
            + "     COUNT(d.code) AS occurrences,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 1 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='1' AND pd.m_code=m.code)  else 0 end) AS day1,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 2 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='2' AND pd.m_code=m.code) else 0 end) AS day2,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 3 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='3' AND pd.m_code=m.code) else 0 end) AS day3,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 4 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='4' AND pd.m_code=m.code) else 0 end) AS day4,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 5 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='5' AND pd.m_code=m.code) else 0 end) AS day5,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 6 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='6' AND pd.m_code=m.code) else 0 end) AS day6,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 7 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='7' AND pd.m_code=m.code) else 0 end) AS day7,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 8 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='8' AND pd.m_code=m.code) else 0 end) AS day8,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 9 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='9' AND pd.m_code=m.code) else 0 end) AS day9,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 10 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='10' AND pd.m_code=m.code) else 0 end) AS day10,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 11 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='11' AND pd.m_code=m.code) else 0 end) AS day11,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 12 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='12' AND pd.m_code=m.code) else 0 end) AS day12,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 13 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='13' AND pd.m_code=m.code) else 0 end) AS day13,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 14 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='14' AND pd.m_code=m.code) else 0 end) AS day14,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 15 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='15' AND pd.m_code=m.code) else 0 end) AS day15,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 16 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='16' AND pd.m_code=m.code) else 0 end) AS day16,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 17 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='17' AND pd.m_code=m.code) else 0 end) AS day17,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 18 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='18' AND pd.m_code=m.code) else 0 end) AS day18,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 19 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='19' AND pd.m_code=m.code) else 0 end) AS day19,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 20 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='20' AND pd.m_code=m.code) else 0 end) AS day20,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 21 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='21' AND pd.m_code=m.code) else 0 end) AS day21,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 22 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='22' AND pd.m_code=m.code) else 0 end) AS day22,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 23 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='23' AND pd.m_code=m.code) else 0 end) AS day23,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 24 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='24' AND pd.m_code=m.code) else 0 end) AS  day24,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 25 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='25' AND pd.m_code=m.code) else 0 end) AS day25,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 26 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='26' AND pd.m_code=m.code) else 0 end) AS day26,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 27 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='27' AND pd.m_code=m.code) else 0 end) AS day27,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 28 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='28' AND pd.m_code=m.code) else 0 end) AS day28,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 29 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='29' AND pd.m_code=m.code) else 0 end) AS day29,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 30 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='30' AND pd.m_code=m.code) else 0 end) AS day30,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 31 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='31' AND pd.m_code=m.code) else 0 end) AS  day31,"
            + "     m.code AS code," 
            + "     p.date_of_birth"
            + "     FROM" 
            + "     moh m" 
            + "     left JOIN patient_diagnosis d ON TRIM(m.code) = TRIm(d.m_code) OR ISNULL(d.m_code)"
            + "     left JOIN person p ON p.id = d.patient_id"
            + "     WHERE  d.certainty='Confirmed' AND m.active='1' AND m.code=:code  AND date(d.date_recorded) BETWEEN :fromDate AND :toDate  AND (YEAR(NOW())-YEAR(p.date_of_birth))>5"
            + "     GROUP BY m.code ORDER BY CAST(m.code  AS UNSIGNED), m.code ASC", nativeQuery=true)
            
            
    List<MonthlyMobidity> findMorbidityOver5(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("code") String code);

    @Query(value = "SELECT"
            + "     m.description AS disease,"
            + "     COUNT(d.code) AS occurrences,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 1 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='1' AND pd.m_code=m.code)  else 0 end) AS day1,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 2 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='2' AND pd.m_code=m.code) else 0 end) AS day2,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 3 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='3' AND pd.m_code=m.code) else 0 end) AS day3,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 4 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='4' AND pd.m_code=m.code) else 0 end) AS day4,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 5 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='5' AND pd.m_code=m.code) else 0 end) AS day5,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 6 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='6' AND pd.m_code=m.code) else 0 end) AS day6,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 7 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='7' AND pd.m_code=m.code) else 0 end) AS day7,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 8 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='8' AND pd.m_code=m.code) else 0 end) AS day8,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 9 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='9' AND pd.m_code=m.code) else 0 end) AS day9,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 10 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='10' AND pd.m_code=m.code) else 0 end) AS day10,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 11 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='11' AND pd.m_code=m.code) else 0 end) AS day11,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 12 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='12' AND pd.m_code=m.code) else 0 end) AS day12,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 13 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='13' AND pd.m_code=m.code) else 0 end) AS day13,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 14 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='14' AND pd.m_code=m.code) else 0 end) AS day14,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 15 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='15' AND pd.m_code=m.code) else 0 end) AS day15,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 16 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='16' AND pd.m_code=m.code) else 0 end) AS day16,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 17 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='17' AND pd.m_code=m.code) else 0 end) AS day17,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 18 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='18' AND pd.m_code=m.code) else 0 end) AS day18,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 19 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='19' AND pd.m_code=m.code) else 0 end) AS day19,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 20 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='20' AND pd.m_code=m.code) else 0 end) AS day20,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 21 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='21' AND pd.m_code=m.code) else 0 end) AS day21,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 22 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='22' AND pd.m_code=m.code) else 0 end) AS day22,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 23 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='23' AND pd.m_code=m.code) else 0 end) AS day23,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 24 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='24' AND pd.m_code=m.code) else 0 end) AS  day24,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 25 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='25' AND pd.m_code=m.code) else 0 end) AS day25,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 26 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='26' AND pd.m_code=m.code) else 0 end) AS day26,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 27 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='27' AND pd.m_code=m.code) else 0 end) AS day27,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 28 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='28' AND pd.m_code=m.code) else 0 end) AS day28,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 29 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='29' AND pd.m_code=m.code) else 0 end) AS day29,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 30 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='30' AND pd.m_code=m.code) else 0 end) AS day30,"
            + "     MAX(case when DAYOFMONTH(d.date_recorded) = 31 then (select count(pd.m_code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.date_recorded)='31' AND pd.m_code=m.code) else 0 end) AS  day31,"
            + "     m.code AS code,"
            + "     p.date_of_birth"
            + "     FROM"
            + "     moh m"
            + "     left JOIN patient_diagnosis d ON TRIM(m.code) = TRIm(d.m_code) OR ISNULL(d.m_code)"
            + "     left JOIN person p ON p.id = d.patient_id"
            + "     WHERE  d.certainty='Confirmed' AND m.active='1' AND m.code=:code  AND date(d.date_recorded) BETWEEN :fromDate AND :toDate  AND (YEAR(NOW())-YEAR(p.date_of_birth))<=5"
            + "     GROUP BY m.code ORDER BY CAST(m.code  AS UNSIGNED), m.code ASC", nativeQuery=true)
    List<MonthlyMobidity> findMorbidityUnder5(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("code") String code);
   
    
    

}
