package io.smarthealth.clinical.laboratory.data;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class CreateTestPanel {

    private Long itemId;
    private String itemCode;
    private String itemName;
    private Boolean active;
    private List<Labs> tests = new ArrayList<>();
}
