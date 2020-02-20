package io.smarthealth.accounting.cashier.domain;

import io.smarthealth.accounting.cashier.data.CashPointData;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table; 
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "acc_cash_point")
public class CashPoint extends Identifiable {

    @Column(name = "drawer_name")
    private String name;
    
    private String tenderTypes;
 
    private boolean active;
    public CashPointData toData(){
        CashPointData data=new CashPointData();
        data.setId(this.getId());
        data.setActive(this.active);
        data.setName(this.name);
        data.setTenderTypes(StringUtils.split(this.tenderTypes, ','));
        return data;
    }
}
