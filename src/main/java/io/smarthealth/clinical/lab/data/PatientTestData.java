package io.smarthealth.clinical.lab.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.lab.domain.PatientLabTest;
import io.smarthealth.clinical.lab.domain.Results;
import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientTestData {
    private Long id;
    
    
    private Long specimenId;
    private SpecimenData specimenData;
    private LocalDateTime specimenCollectionTime;
    private LabTestTypeData testTypeData;
    private String testCode;
    private String patientNumber;
    
//    private Visit visit;
    @Enumerated(EnumType.STRING)
    private LabTestState state;
    private List<ResultsData> resultData=new ArrayList();

    public static PatientLabTest map(PatientTestData ptestdata) {
        PatientLabTest entity = new PatientLabTest();
        entity.setId(ptestdata.getId());
        entity.setState(ptestdata.getState());
        return entity;
    }

    public static PatientTestData map(PatientLabTest patientTest) {
        PatientTestData test = new PatientTestData();
        test.setId(patientTest.getId());
        test.setSpecimenCollectionTime(patientTest.getSpecimenCollectionTime());
        if(patientTest.getSpecimen()!=null){
            SpecimenData spdata = SpecimenData.map(patientTest.getSpecimen());
            test.setSpecimenData(spdata);
        }
        test.setTestCode(patientTest.getTesttype()!=null?patientTest.getTesttype().getServiceCode():"");
        test.setState(patientTest.getState());
        
        test.setResultData(new ArrayList());
        if (patientTest.getResults() != null) {
            for (Results result : patientTest.getResults()) {
                ResultsData resultdata = ResultsData.map(result);
                if (!test.getResultData().isEmpty()) {
                    test.getResultData().add(resultdata);
                } else {                    
                    test.getResultData().add(resultdata);
                }
            }
        }
        
        if(patientTest.getTesttype()!=null){
            LabTestTypeData testTypeData = LabTestTypeData.map(patientTest.getTesttype());
            test.setTestTypeData(testTypeData);
        }
        
        return test;
    }

}
