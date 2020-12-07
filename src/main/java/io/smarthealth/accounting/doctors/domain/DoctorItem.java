package io.smarthealth.accounting.doctors.domain;

import io.smarthealth.accounting.doctors.data.DoctorItemData;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.stock.item.domain.Item;
import java.math.BigDecimal;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
 @Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "acc_doctor_items")
// @Inheritance(strategy = InheritanceType.JOINED)
 @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "fee_type")
public class DoctorItem extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_doctor_service_staff_id"))
    private Employee doctor;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_doctor_service_service_id"))
    private Item serviceType;

    private BigDecimal amount;
    private Boolean isPercentage;
    private Boolean active;

    public DoctorItemData toData() {
        DoctorItemData data = new DoctorItemData();
        data.setId(this.getId());
        data.setCommisionValue(this.getAmount());
        if (this.doctor != null) {
            data.setDoctorId(this.doctor.getId());
            data.setDoctorName(this.doctor.getFullName());

        }
        if (this.serviceType != null) {
            data.setServiceId(this.serviceType.getId());
            data.setServiceCode(this.getServiceType().getItemCode());
            data.setServiceName(this.getServiceType().getItemName());
        }
        if (this.isPercentage) {
            BigDecimal doctorRate = this.amount.divide(BigDecimal.valueOf(100)).multiply(this.serviceType.getRate());
            this.amount = doctorRate;
        }
        data.setIsPercentage(this.isPercentage);

        data.setAmount(this.amount);
        data.setActive(this.active);

        return data;
    }
}
