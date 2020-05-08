package io.smarthealth.supplier.domain;

import io.smarthealth.accounting.accounts.data.SimpleAccountData;
import io.smarthealth.accounting.pricelist.data.PriceBookData;
import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.administration.finances.domain.PaymentTerms;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class SupplierMetadata {

    private String code;
    private String message;

    private List<PriceBookData> pricelists;
    private List<Currency> currencies;
    private List<PaymentTerms> paymentTerms;
    private List<SimpleAccountData> accounts;
}
