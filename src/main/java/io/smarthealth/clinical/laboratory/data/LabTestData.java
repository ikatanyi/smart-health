package io.smarthealth.clinical.laboratory.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class LabTestData {

    private Long id;
    private Long itemId;
    private String itemCode;
    private String itemName;
    private String shortName;
    private String testName;
    private Long categoryId;
    private String category;
    private String gender;
    private Boolean requiresConsent;
    private Boolean hasReferenceValue;
    private String turnAroundTime;
    private Boolean active;
    @NotNull(message = "indicate if normal test or test panel")
    private Boolean isPanel=false;
    private List<AnalyteData> analytes = new ArrayList<>();
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<Labs> panelTests = new HashSet<>();

}
