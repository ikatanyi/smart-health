package io.smarthealth.clinical.lab.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.lab.domain.Analyte;
import io.smarthealth.clinical.lab.domain.Discipline;
import io.smarthealth.clinical.lab.domain.Specimen;
import io.smarthealth.clinical.lab.domain.LabTestType;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;
import java.time.LocalDateTime;
import javax.persistence.Column;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LabTestTypeData {  
    
    public enum Gender {
        Male,
        Female,
        Both
    }
    
    private Boolean consent; 
    private Boolean withRef; 
    private Boolean refOut; 
    private Long duration;
    private String durationDesc;
    private String notes;
    private Boolean supervisorConfirmation;
    private Discipline discipline;
    
    private List<Long> specimenId;
    
    private Long disciplineId;
    private Gender gender;

    private Long id;
    @NotNull
    @Column(length = 25)
    private String code;
    @NotNull
    @Column(length = 50)
    private String testType;  

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime recorded = LocalDateTime.now();    
    
    private List<AnalyteData> analytes;
    private List<SpecimenData>specimens;

    public static LabTestType map(LabTestTypeData testtype) {
        System.out.println("Test type received "+testtype.toString());
        LabTestType entity = new LabTestType();
        entity.setServiceCode(testtype.getCode());
        entity.setTestType(testtype.getTestType());
        entity.setConsent(testtype.getConsent());
        entity.setWithRef(testtype.getWithRef());
        entity.setRefOut(testtype.getRefOut());
        entity.setDuration(testtype.getDuration());
        entity.setDurationDesc(testtype.getDurationDesc());
        entity.setDiscipline(testtype.getDiscipline());
        entity.setGender(testtype.getGender());
        entity.setId(testtype.getId());
        return entity;
    }

    public static LabTestTypeData map(LabTestType entity) {
        ModelMapper modelMapper = new ModelMapper();
        LabTestTypeData test = new LabTestTypeData();
        test.setId(entity.getId());
        test.setCode(entity.getServiceCode());
        test.setTestType(entity.getTestType());  
        test.setConsent(entity.getConsent());
        test.setDuration(entity.getDuration());
        test.setDurationDesc(entity.getDurationDesc());
        test.setNotes(entity.getNotes());
        test.setRefOut(entity.getRefOut());
        test.setTestType(entity.getTestType());
        test.setGender(entity.getGender());
        for(Analyte analyte:entity.getAnalytes()){
            AnalyteData analytedata = modelMapper.map(analyte,AnalyteData.class);
            if(!test.getAnalytes().isEmpty())
               test.getAnalytes().add(analytedata);
            else{
                test.setAnalytes(new ArrayList());
                test.getAnalytes().add(analytedata);
            }
        }
        for(Specimen specimen:entity.getSpecimens()){
            SpecimenData specimendata = modelMapper.map(specimen,SpecimenData.class);
            if(!test.getSpecimens().isEmpty())
               test.getSpecimens().add(specimendata);
            else{
                test.setSpecimens(new ArrayList());
                test.getSpecimens().add(specimendata);
            }
        }
        return test;
    }
    
//     public AnalyteData convertAnalyteToData(Analyte analyte) {
//        AnalyteData analyteData = modelMapper.map(analyte, AnalyteData.class);
//        return analyteData;
//    }
}
