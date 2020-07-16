/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.cashier.domain;

import io.smarthealth.accounting.cashier.data.CashierData;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.security.domain.User;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "acc_cashiers")
public class Cashier extends Identifiable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ac_cashier_user_id"))
    private User user;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ac_cashier_cashpoint_id"))
    private CashPoint cashPoint;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active = Boolean.TRUE;

    @OneToMany(mappedBy = "cashier")
    private List<Shift> shiftNumbers;

    public CashierData toData() {
        CashierData data = new CashierData();
        data.setActive(this.active);
        data.setId(this.getId());
        if (this.user != null) {
            data.setUser(this.user.getName());
            data.setUserId(this.user.getId());
            data.setUsername(this.user.getUsername());
            data.setEmail(this.user.getEmail());
        }
        if (this.cashPoint != null) {
            data.setCashPoint(this.cashPoint.getName());
            data.setCashPointId(this.cashPoint.getId());
        }
        data.setStartDate(this.startDate);
        data.setEndDate(this.endDate);
         
        return data;
    }
    
}
