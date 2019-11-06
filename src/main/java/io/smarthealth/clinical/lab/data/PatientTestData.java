package io.smarthealth.clinical.lab.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.lab.domain.Analyte;
import io.smarthealth.clinical.lab.domain.PatientLabTest;
import io.smarthealth.clinical.lab.domain.Results;
import io.smarthealth.clinical.lab.domain.LabTestType;
import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.modelmapper.ModelMapper;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientTestData {
    private Long id;
    private String visitNumber;
    private Long requestId;
    private String clinicalDetails;
    private String physicianId;
    private String physicianName;
    private String requestNumber;
    
    @Enumerated(EnumType.STRING)
    private LabTestState state;
    private List<ResultsData> resultData;
    
    public static PatientLabTest map(PatientTestData ptestdata) {
        PatientLabTest entity = new PatientLabTest();
        entity.setClinicalDetails(ptestdata.getClinicalDetails());
        entity.setRequestNumber(ptestdata.getRequestNumber());
        return entity;
    }

    public static PatientTestData map(PatientLabTest patientTest) {
        ModelMapper modelMapper = new ModelMapper();
        PatientTestData test = new PatientTestData();
        test.setId(patientTest.getId());
        test.setVisitNumber(test.getVisitNumber());
        test.setState(patientTest.getState());
        for(Results result:patientTest.getResults()){
            ResultsData resultdata = modelMapper.map(result,ResultsData.class);
            if(!test.getResultData().isEmpty())
               test.getResultData().add(resultdata);
            else{
               test.setResultData(new ArrayList());
               test.getResultData().add(resultdata);
            }
        }
        return test;
    }
    
}
