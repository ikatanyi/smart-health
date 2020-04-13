/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.inpatient.admission.data;

import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class AdmissionPayment {

    private VisitEnum.PaymentMethod paymentMethod;
    
    private Long payerId;
    private String payer;

    private Long schemeId;
    private String scheme;

    private String memberNumber;
    private String memberName;

    private String benefitId;
    private String benefit;
    private BigDecimal benefitAmount;

}
