/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.data;

import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.clinical.procedure.domain.PatientProcedureTest;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class PatientProcedureTestData {

    @ApiModelProperty(required = false, hidden = true)
    private Long id;
    private String results;
    private String comments;
    private String patientNo;
    private String patientName;
    private String procedureName;
    @ApiModelProperty(required = false, hidden = true)
    private ProcedureData testData;
    @Enumerated(EnumType.STRING)
    private ProcedureTestState state;
    private Long requestId;
    private String requestedBy;
    private Boolean paid;
    private double testPrice;
    private double discount;
    private String paymentMode;
    private double quantity;
    @ApiModelProperty(required = false, hidden = true)
    private String medicName;
    private Long medicId;
    private LocalDate procedureDate=LocalDate.now();
   

    public static PatientProcedureTest map(PatientProcedureTestData scan) {
        PatientProcedureTest entity = new PatientProcedureTest();
        entity.setResult(scan.getResults());
        entity.setStatus(scan.getState());
        return entity;
    }
}
