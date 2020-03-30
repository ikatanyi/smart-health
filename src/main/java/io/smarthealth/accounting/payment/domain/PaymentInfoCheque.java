/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.time.LocalDate;

/**
 *
 * @author Kelsas
 */
public class PaymentInfoCheque extends Identifiable {

    private String bank;
    private String chequeNumber;
    private LocalDate chequeDate;
}
