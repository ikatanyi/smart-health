package io.smarthealth.clinical.lab.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.lab.domain.Analyte;
import io.smarthealth.clinical.lab.domain.Testtype;
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

    private Long id;
    @NotNull
    @Column(length = 25)
    private String code;
    @NotNull
    @Column(length = 25)
    private String testType;  

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime recorded = LocalDateTime.now();    
    
    private List<AnalyteData> analytes;

    public static Testtype map(LabTestTypeData testtype) {
        Testtype entity = new Testtype();
        entity.setServiceCode(testtype.getCode());
        entity.setTestType(testtype.getTestType());        
        return entity;
    }

    public static LabTestTypeData map(Testtype entity) {
        ModelMapper modelMapper = new ModelMapper();
        LabTestTypeData test = new LabTestTypeData();
        test.setId(entity.getId());
        test.setCode(entity.getServiceCode());
        test.setTestType(entity.getTestType());    
        for(Analyte analyte:entity.getAnalytes()){
            AnalyteData analytedata = modelMapper.map(analyte,AnalyteData.class);
            if(!test.getAnalytes().isEmpty())
               test.getAnalytes().add(analytedata);
            else{
                test.setAnalytes(new ArrayList());
                test.getAnalytes().add(analytedata);
            }
        }
        return test;
    }
    
//     public AnalyteData convertAnalyteToData(Analyte analyte) {
//        AnalyteData analyteData = modelMapper.map(analyte, AnalyteData.class);
//        return analyteData;
//    }
}
