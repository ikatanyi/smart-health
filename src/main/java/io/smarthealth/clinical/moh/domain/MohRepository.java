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

/**
 *
 * @author kent
 */
public interface MohRepository extends JpaRepository<Moh, Long>, JpaSpecificationExecutor<Moh> {

    Optional<Moh> findByDescriptionContainingIgnoreCase(String name);

    @Query(value = "SELECT"
            + "     m.description AS disease,"
            + "     COUNT(d.code) AS occurrences,"
            + "     case when DAYOFMONTH(d.created_on) = '1' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='1' AND pd.code=d.code)  else 0 end AS day_1,"
            + "     case when DAYOFMONTH(d.created_on) = '2' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='2' AND pd.code=d.code) else 0 end AS day_2,"
            + "     case when DAYOFMONTH(d.created_on) = '3' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='3' AND pd.code=d.code) else 0 end AS day_3,"
            + "     case when DAYOFMONTH(d.created_on) = '4' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='4' AND pd.code=d.code) else 0 end AS day_4,"
            + "     case when DAYOFMONTH(d.created_on) = '5' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='5' AND pd.code=d.code) else 0 end AS day_5,"
            + "     case when DAYOFMONTH(d.created_on) = '6' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='6' AND pd.code=d.code) else 0 end AS day_6,"
            + "     case when DAYOFMONTH(d.created_on) = '7' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='7' AND pd.code=d.code) else 0 end AS day_7,"
            + "     case when DAYOFMONTH(d.created_on) = '8' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='8' AND pd.code=d.code) else 0 end AS day_8,"
            + "     case when DAYOFMONTH(d.created_on) = '9' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='9' AND pd.code=d.code) else 0 end AS day_9,"
            + "     case when DAYOFMONTH(d.created_on) = '10' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='10' AND pd.code=d.code) else 0 end AS day_10,"
            + "     case when DAYOFMONTH(d.created_on) = '11' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='11' AND pd.code=d.code) else 0 end AS day_11,"
            + "     case when DAYOFMONTH(d.created_on) = '12' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='12' AND pd.code=d.code) else 0 end AS day_12,"
            + "     case when DAYOFMONTH(d.created_on) = '13' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='13' AND pd.code=d.code) else 0 end AS day_13,"
            + "     case when DAYOFMONTH(d.created_on) = '14' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='14' AND pd.code=d.code) else 0 end AS day_14,"
            + "     case when DAYOFMONTH(d.created_on) = '15' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='15' AND pd.code=d.code) else 0 end AS day_15,"
            + "     case when DAYOFMONTH(d.created_on) = '16' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='16' AND pd.code=d.code) else 0 end AS day_16,"
            + "     case when DAYOFMONTH(d.created_on) = '17' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='17' AND pd.code=d.code) else 0 end AS day_17,"
            + "     case when DAYOFMONTH(d.created_on) = '18' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='18' AND pd.code=d.code) else 0 end AS day_18,"
            + "     case when DAYOFMONTH(d.created_on) = '19' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='19' AND pd.code=d.code) else 0 end AS day_19,"
            + "     case when DAYOFMONTH(d.created_on) = '20' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='20' AND pd.code=d.code) else 0 end AS day_20,"
            + "     case when DAYOFMONTH(d.created_on) = '21' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='21' AND pd.code=d.code) else 0 end AS day_21,"
            + "     case when DAYOFMONTH(d.created_on) = '22' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='22' AND pd.code=d.code) else 0 end AS day_22,"
            + "     case when DAYOFMONTH(d.created_on) = '23' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='23' AND pd.code=d.code) else 0 end AS day_23,"
            + "     case when DAYOFMONTH(d.created_on) = '24' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='24' AND pd.code=d.code) else 0 end AS  day_24,"
            + "     case when DAYOFMONTH(d.created_on) = '25' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='25' AND pd.code=d.code) else 0 end AS day_25,"
            + "     case when DAYOFMONTH(d.created_on) = '26' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='26' AND pd.code=d.code) else 0 end AS day_26,"
            + "     case when DAYOFMONTH(d.created_on) = '27' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='27' AND pd.code=d.code) else 0 end AS day_27,"
            + "     case when DAYOFMONTH(d.created_on) = '28' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='28' AND pd.code=d.code) else 0 end AS day_28,"
            + "     case when DAYOFMONTH(d.created_on) = '29' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='29' AND pd.code=d.code) else 0 end AS day_29,"
            + "     case when DAYOFMONTH(d.created_on) = '30' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='30' AND pd.code=d.code) else 0 end AS day_30,"
            + "     case when DAYOFMONTH(d.created_on) = '31' then (select count(pd.code) FROM patient_diagnosis pd WHERE DAYOFMONTH(pd.created_on)='31' AND pd.code=d.code) else 0 end AS  day_31,"
            + "     d.id AS diagnosis_id,"
            + "     d.created_on AS created_on,"
            + "     m.code AS m_ICD10"
            + "   FROM"
            + "      moh m LEFT JOIN patient_diagnosis d ON m.code = d.code AND  d.created_on BETWEEN :fromDate AND :toDate"
            + "   GROUP BY DAYOFMONTH(d.created_on), m.code", nativeQuery=true)
    List<MonthlyMobidity>findMorbiditySummaryInterface(LocalDate fromDate, LocalDate toDate);

}
