package io.smarthealth.clinical.pharmacy.domain;

import io.smarthealth.clinical.pharmacy.data.DispensedDrugData;
import io.smarthealth.clinical.record.domain.Prescription;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import java.time.LocalDate;
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
@Entity
@Data
@Table(name = "pharmacy_dispensed_drugs")
public class DispensedDrug extends Auditable implements Cloneable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pharm_dispensed_drugs_patient_id"))
    private Patient patient;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pharmacy_dispensed_drugs_visit_id"))
    private Visit visit;

    private LocalDate dispensedDate;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pharm_dispensed_drugs_item_id"))
    private Item drug;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pharm_dispensed_drugs_presc_id"))
    private Prescription prescription;
    private String transactionId;
    private Double qtyIssued;
    private Double price;
    private Double amount;
    private Double discount;
    private Double taxes;
    private String units;
    private String doctorName;
    private Boolean paid;
    private Boolean collected;
    private String dispensedBy;
    private String collectedBy;
    private Boolean isReturn;
    private String returnReason;
    private LocalDate returnDate;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pharm_dispensed_drugs_store_id"))
    private Store store;
    private String instructions;
    private String otherReference;
    private Boolean walkinFlag = Boolean.FALSE;

//    @Enumerated(EnumType.STRING)
    public DispensedDrugData toData() {
        DispensedDrugData data = new DispensedDrugData();

        if (this.patient != null) {
            data.setPatientNumber(this.patient.getPatientNumber());
            data.setPatientName(this.patient.getFullName());
        }
        data.setDispensedDate(this.dispensedDate);
        if (this.prescription != null) {
            data.setPrescriptionNo(this.prescription.getOrderNumber());
        }
        data.setQtyIssued(this.qtyIssued);
        data.setPrice(this.price);
        data.setAmount(this.amount);
        data.setUnits(this.units);
        data.setDoctorName(this.doctorName);
        data.setPaid(this.paid);
        data.setCollected(this.collected);
        data.setDispensedBy(this.dispensedBy);
        data.setCollectedBy(this.collectedBy);
        data.setTransactionId(this.transactionId);
        data.setIsReturn(this.isReturn);
        if (this.getDrug() != null) {
            data.setDrugId(this.getDrug().getId());
            data.setDrug(this.drug.getItemName());
        }

        data.setId(this.getId());
        if (this.store != null) {
            data.setStoreId(this.store.getId());
            data.setStoreName(this.store.getStoreName());
        }
        data.setInstructions(this.instructions);
        data.setOtherReference(this.otherReference);
        data.setWalkinFlag(this.walkinFlag);
        return data;
    }

    // It has to be exactly this method signature
    @Override
    public Object clone() {
        try {
            // call clone in Object.
            return super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("Unable to clone object");

            // Depends on your own use-case. I don't want the object modified somewhere!
            return null;
        }
    }
}
