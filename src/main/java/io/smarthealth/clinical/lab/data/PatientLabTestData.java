package io.smarthealth.clinical.lab.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.smarthealth.clinical.lab.domain.PatientLabTest;
import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL) 	//  ignore all null fields
public class PatientLabTestData {
    
    @ApiModelProperty(required = false, hidden = true)
    private Long patientLabTestId;

//    @ApiModelProperty(required = false, hidden = true)
//    private LabTestType labTestType;
    //@ApiModelProperty(required = false, hidden = true)
    private Long labTestTypeId;
    private String testName;
    private int quantity;
    
    private double testPrice;
    @Enumerated(EnumType.STRING)
    private LabTestState status;
    
    @ApiModelProperty(hidden = true, required = false)
    List<SpecimenData> specimenData;
    @ApiModelProperty(hidden = true, required = false)
    private String accessNo;
    private List<PatientLabTestSpecimenData> patientLabTestSpecimen;
    
    @ApiModelProperty(hidden = true, required = false)
    private List<ResultsData> resultsData = new ArrayList();
    
    public static PatientLabTest map(PatientLabTestData ptestdata) {
        PatientLabTest entity = new PatientLabTest();
        entity.setStatus(ptestdata.getStatus());
        entity.setTestPrice(ptestdata.getTestPrice());
        return entity;
    }
    
    public static PatientLabTestData map(PatientLabTest patientTest) {
        PatientLabTestData test = new PatientLabTestData();
        test.setLabTestTypeId(patientTest.getId());
//        test.setSpecimenCollectionTime(patientTest.getSpecimenCollectionTime());
//        if (patientTest.getSpecimen() != null) {
//            SpecimenData spdata = SpecimenData.map(patientTest.getSpecimen());
//            test.setSpecimenData(spdata);
//        }
//        test.setTestCode(patientTest.getTesttype() != null ? patientTest.getTesttype().getServiceCode() : "");
//        test.setStatus(patientTest.getStatus());
//
//        test.setResultData(new ArrayList());
//        if (patientTest.getResults() != null) {
//            for (Results result : patientTest.getResults()) {
//                ResultsData resultdata = ResultsData.map(result);
//                if (!test.getResultData().isEmpty()) {
//                    test.getResultData().add(resultdata);
//                } else {
//                    test.getResultData().add(resultdata);
//                }
//            }
//        }
//
//        if (patientTest.getTesttype() != null) {
//            LabTestTypeData testTypeData = LabTestTypeData.map(patientTest.getTesttype());
//            test.setTestTypeData(testTypeData);
//        }

        return test;
    }
    
    public static List<PatientLabTestData> map(List<PatientLabTest> patientLabTests) {
        List<PatientLabTestData> patientLabTestsData = new ArrayList<>();
        for (PatientLabTest labTest : patientLabTests) {
            PatientLabTestData p = new PatientLabTestData();
            p.setPatientLabTestId(labTest.getId());
            p.setStatus(labTest.getStatus());
            p.setTestName(labTest.getTestType().getTestType());
            p.setQuantity(labTest.getQuantity());
            p.setTestPrice(labTest.getTestPrice());

            //Display possible specimen
            p.setSpecimenData(SpecimenData.map(labTest.getTestType().getSpecimen()));
            
            patientLabTestsData.add(p);
            
        }
        return patientLabTestsData;
    }
    
    public static List<PatientLabTestData> mapConfirmedTests(List<PatientLabTest> patientLabTests) {
        List<PatientLabTestData> patientLabTestsData = new ArrayList<>();
        for (PatientLabTest labTest : patientLabTests) {
            PatientLabTestData p = new PatientLabTestData();
            p.setPatientLabTestId(labTest.getId());
            p.setStatus(labTest.getStatus());
            p.setTestName(labTest.getTestType().getTestType());
            p.setQuantity(labTest.getQuantity());
            p.setTestPrice(labTest.getTestPrice());

            //Display collected specimen
            p.setPatientLabTestSpecimen(PatientLabTestSpecimenData.map(labTest.getPatientLabTestSpecimens()));

            //Display confirmed/pending results
            p.setResultsData(ResultsData.map(labTest.getResults()));
            patientLabTestsData.add(p);
            
        }
        return patientLabTestsData;
    }
    
}
