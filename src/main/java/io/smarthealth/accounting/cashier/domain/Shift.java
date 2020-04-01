package io.smarthealth.accounting.cashier.domain;

import io.smarthealth.accounting.cashier.data.ShiftData;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import org.hibernate.annotations.Formula;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "acc_cashiers_shifts")
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
        data.setId(this.getId());
        data.setCashPoint(this.cashPoint.getName());
        data.setCashier(this.cashier.getUser().getName());
        data.setEndDate(this.getEndDate());
        data.setShiftNo(this.getShiftNo());
        data.setStartDate(this.getStartDate());
        data.setStatus(this.getStatus());
        return data;
    }
}
