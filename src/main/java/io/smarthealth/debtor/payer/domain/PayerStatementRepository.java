/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.payer.domain;

import io.smarthealth.debtor.payer.data.PayerStatement;
import io.smarthealth.infrastructure.lang.DateRange;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Kelsas
 */
public interface PayerStatementRepository {

    public List<PayerStatement> getPayerStatement(Long payerId, DateRange range);

    public BigDecimal getPayerBalance(Long payerId, LocalDate date);
}
