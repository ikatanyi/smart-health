package io.smarthealth.supplier.domain;

import io.smarthealth.accounting.acc.data.SimpleAccountData;
import io.smarthealth.accounting.pricebook.data.PriceBookData;
import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.accounting.payment.domain.PaymentTerms;
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
