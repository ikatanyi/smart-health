/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.domain;

/**
 *
 * @author Kennedy.Imbenzi
 */
import io.smarthealth.clinical.procedure.data.PatientProcedureTestData;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "patient_procedure_tests")
public class PatientProcedureTest extends Identifiable {

    @ManyToOne
    private PatientProcedureRegister patientProcedureRegister;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_procedure_tests_id_item_id"))
    private Item procedureTest;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_procedure_tests_id_request_id"))
    private DoctorRequest request;
    private Double testPrice;
    private Double quantity;
    private Boolean paid;
    @Enumerated(EnumType.STRING)
    private ProcedureTestState status;
    private String result;
    private String comments;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_procedure_tests_id_employee_id"))
    private Employee medic;
    private LocalDate procedureDate;
    @Transient
    private PaymentMethod paymentMethod;

    public PatientProcedureTestData toData() {
        PatientProcedureTestData entity = new PatientProcedureTestData();
        entity.setResults(this.getResult());
        entity.setState(this.getStatus());
        entity.setId(this.getId());
        entity.setPaid(this.paid);
        entity.setProcedureDate(this.getProcedureDate());
        entity.setComments(this.getComments());
        entity.setQuantity(this.getQuantity());
        entity.setTestPrice(this.getTestPrice());
        if (this.getProcedureTest() != null) {
            entity.setProcedureName(this.getProcedureTest().getItemName());
        }
        if (this.getMedic() != null) {
            entity.setMedicId(this.getMedic().getId());
            entity.setMedicName(this.getMedic().getFullName());
        }
        if (this.getPatientProcedureRegister() != null) {
            entity.setRequestedBy(this.patientProcedureRegister.getRequestedBy());
            if (this.getProcedureDate() == null) {
                entity.setProcedureDate(this.getPatientProcedureRegister().getReceivedDate());
            }
            entity.setPatientName(this.getPatientProcedureRegister().getPatientName());
            entity.setPatientNo(this.getPatientProcedureRegister().getPatientNo());
            entity.setCreatedBy(this.getPatientProcedureRegister().getCreatedBy());
        }
        if (this.getRequest() != null) {
            entity.setRequestId(this.getRequest().getRequestedBy().getId());
        }
        return entity;
    }

}
