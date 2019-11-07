package io.smarthealth.clinical.lab.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.lab.domain.LabTestType;
import io.smarthealth.clinical.lab.domain.PatientLabTest;
import io.smarthealth.clinical.lab.domain.Results;
import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.visit.domain.Visit;
import java.time.LocalDateTime;
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
    private String requestId;
    private String testCode;
    private String clinicalDetails;
    private String physicianId;
    private String physicianName;
    private String LabTestNumber;
    private String specimen;
    private LocalDateTime specimenCollectionTime;
    private LabTestTypeData testTypeData;
    
//    private Visit visit;
    private DoctorRequest request;

    @Enumerated(EnumType.STRING)
    private LabTestState state;
    private List<ResultsData> resultData=new ArrayList();

    public static PatientLabTest map(PatientTestData ptestdata) {
        PatientLabTest entity = new PatientLabTest();
        entity.setId(ptestdata.getId());
        entity.setClinicalDetails(ptestdata.getClinicalDetails());
        entity.setLabTestNumber(ptestdata.getLabTestNumber());
        entity.setSpecimen(ptestdata.getSpecimen());
        entity.setState(ptestdata.getState());
        return entity;
    }

    public static PatientTestData map(PatientLabTest patientTest) {
        ModelMapper modelMapper = new ModelMapper();
        PatientTestData test = new PatientTestData();
        test.setId(patientTest.getId());
        test.setSpecimenCollectionTime(patientTest.getSpecimenCollectionTime());
        if(patientTest.getVisit()!=null){
           test.setVisitNumber(patientTest.getVisit().getVisitNumber());
        }
        if(patientTest.getRequest()!=null){
            test.setRequestId(String.valueOf(patientTest.getRequest().getId()));
            test.setPhysicianId(patientTest.getRequest().getRequestedBy().getStaffNumber());
            test.setPhysicianName(patientTest.getRequest().getCreatedBy());            
        }
        test.setTestCode(patientTest.getTesttype()!=null?patientTest.getTesttype().getServiceCode():"");
        test.setClinicalDetails(patientTest.getClinicalDetails());
        test.setState(patientTest.getState());
        if(patientTest.getRequest()!=null){
            test.setPhysicianId(patientTest.getRequest().getRequestedBy().getStaffNumber());
            test.setPhysicianName(patientTest.getRequest().getRequestedBy().getGivenName());
        }
        test.setLabTestNumber(patientTest.getLabTestNumber());
        test.setResultData(new ArrayList());
        if (patientTest.getResults() != null) {
            for (Results result : patientTest.getResults()) {
                ResultsData resultdata = modelMapper.map(result, ResultsData.class);
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
