package io.smarthealth.accounting.account.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ChartOfAccountEntry {

    private String code;
    private String name;
    private String description;
    private String type;
    private Integer level;
}
