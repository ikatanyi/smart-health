 package io.smarthealth.clinical.visit.domain.impl;

import io.smarthealth.clinical.record.data.DocResults;
import io.smarthealth.clinical.visit.domain.ResultsRepository;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.DateUtility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Repository
@Transactional(readOnly = true)
public class ResultsRepositoryImpl implements ResultsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<DocResults> getPatientResults(String visitNumber, String patientNumber, DocResults.Type type, DateRange range) {
        StringBuilder sb = new StringBuilder(); 
        sb.append("SELECT tr.entry_date_time AS result_date, v.visit_number, r.patient_no, CONCAT(p.given_name, ' ', p.surname)AS patient_name, 'Laboratory' AS resultsType FROM lab_register_tests tr CROSS JOIN lab_register r CROSS JOIN patient_visit v CROSS JOIN person p WHERE tr.lab_register_id=r.id AND r.visit_id =v.id AND v.patient_id=p.id ");
        if (visitNumber != null) {
            sb.append(" and v.visit_number like '").append(visitNumber).append("' ");
        }
        if (patientNumber != null) {
            sb.append("and r.patient_no '").append(patientNumber).append("' ");
        }
//         if (type != null ) {
//            sb.append("and resultsType like  '").append(type.name()).append("' ");
//        }
        if (range != null) {
            sb.append(" and  tr.entry_date_time between '").append(range.getStartDateTime()).append("' and '").append(range.getEndDateTime()).append("' ");
        }
        sb.append("GROUP BY v.visit_number ");
        
        
        sb.append("UNION all ");
        
        
        sb.append(" SELECT r.created_on AS result_date, v.visit_number,sr.patient_no, sr.patient_name, 'Radiology' AS resultsType from radiology_results r cross join patient_scan_test st cross join patient_scan_register sr cross join patient_visit v where r.patient_scan_test_id=st.id and st.patient_scan_register_id=sr.id and sr.visit_id=v.id ");
        if (visitNumber != null) {
            sb.append(" and v.visit_number like '").append(visitNumber).append("' ");
        }
        if (patientNumber != null) {
            sb.append(" and sr.patient_no '").append(patientNumber).append("' ");
        }
//         if (type != null) {
//            sb.append(" and resultsType like '").append(type.name()).append("' ");
//        }
        if (range != null) {
            sb.append(" and  r.created_on between '").append(range.getStartDateTime()).append("' and '").append(range.getEndDateTime()).append("' ");
        }
        sb.append(" GROUP BY v.visit_number ORDER BY 1 ");
 
        final DocResultMapper resultMapper = new DocResultMapper();
        final List<DocResults> results = this.jdbcTemplate.query(sb.toString(), resultMapper, new Object[]{});
        return results;
    }

    private static final class DocResultMapper implements RowMapper<DocResults> {

        @Override
        public DocResults mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Date date = rs.getTimestamp("result_date");
            final String visitNumber = rs.getString("visit_number");
            final String patientNo = rs.getString("patient_no");
            final String patientName = rs.getString("patient_name");
            final String resultType = rs.getString("resultsType");
            final LocalDateTime requestDate = DateUtility.toLocalDateTime(date);
            final DocResults.Type type = DocResults.Type.valueOf(resultType);

            return new DocResults(requestDate, visitNumber, patientNo, patientName, type);
        }

    }

}
