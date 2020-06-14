package io.smarthealth.clinical.laboratory.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Labs {

    private Long testId;
    private String testName;
    private String code;
}
