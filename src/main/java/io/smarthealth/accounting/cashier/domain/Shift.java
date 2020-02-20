/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.cashier.domain;

import io.smarthealth.accounting.cashier.data.ShiftData;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.security.domain.User;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "ac_cashiers_shifts")
public class Shift extends Identifiable {
//    Cashpoint,status, start_date,end_date,user,shiftno

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ac_cashiers_shifts_cashpoint_id"))
    private CashPoint cashPoint;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ac_cashiers_shifts_cashier_id"))
    private Cashier cashier;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String shiftNo;
    @Enumerated(EnumType.STRING)
    private ShiftStatus status;
    
    // we can collect what the user started with

    public Shift() {
    }

    public Shift(Cashier cashier, String shiftNo) {
        this.cashier = cashier;
        this.cashPoint = cashier.getCashPoint();
        this.startDate = LocalDateTime.now();
        this.status = ShiftStatus.Running;
        this.shiftNo=shiftNo;
    }

    public Shift(CashPoint cashPoint, Cashier cashier, LocalDateTime startDate) {
        this.cashPoint = cashPoint;
        this.cashier = cashier;
        this.startDate = startDate;
         this.status = ShiftStatus.Running;
    }

    public Shift(CashPoint cashPoint, Cashier cashier, LocalDateTime startDate, LocalDateTime endDate, String shiftNo, ShiftStatus status) {
        this.cashPoint = cashPoint;
        this.cashier = cashier;
        this.startDate = startDate;
        this.endDate = endDate;
        this.shiftNo = shiftNo;
        this.status = status;
    }

    public ShiftData toData() {
        ShiftData data = new ShiftData();
        data.setCashPoint(this.cashPoint.getName());
        data.setCashier(this.cashier.getUser().getName());
        data.setEndDate(this.getEndDate());
        data.setShiftNo(this.getShiftNo());
        data.setStartDate(this.getStartDate());
        data.setStatus(this.getStatus());
        return data;
    }
}
