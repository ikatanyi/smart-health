/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.supplier.domain;

import io.smarthealth.accounting.pricebook.data.PriceBookData;
import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.administration.app.domain.PaymentTerms;
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
}
