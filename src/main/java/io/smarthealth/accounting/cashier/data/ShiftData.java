/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.cashier.data;

import io.smarthealth.accounting.cashier.domain.*;
import io.smarthealth.accounting.cashier.domain.CashPoint;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.security.domain.User;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ShiftData {
    private String cashPoint;
    private String cashier;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String shiftNo;
    private ShiftStatus status;
}
