package io.smarthealth.clinical.laboratory.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateResultData {
    private Long id;
    private String labNumber;
    private String comments;
    private String resultValue;
    private Boolean validated;
    private Boolean rejected;
    private String validateBy;
}
