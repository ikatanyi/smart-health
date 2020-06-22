/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.supplier.domain;

import io.smarthealth.supplier.data.SupplierStatement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Kelsas
 */
public interface StatementRepository {

    public List<SupplierStatement> getSupplierStatement(Long supplierId);

    public BigDecimal getSupplierBalance(Long supplierId, LocalDate date);
}
