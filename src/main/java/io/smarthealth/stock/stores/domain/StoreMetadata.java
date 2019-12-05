package io.smarthealth.stock.stores.domain;

import io.smarthealth.accounting.acc.data.SimpleAccountData;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class StoreMetadata {

    private String code = "0";
    private String message = "success";
    private List<SimpleAccountData> incomeAccounts;
    private List<SimpleAccountData> expensesAccounts;

}
