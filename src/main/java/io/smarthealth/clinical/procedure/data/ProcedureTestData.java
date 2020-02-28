package io.smarthealth.clinical.procedure.data;

import io.smarthealth.clinical.procedure.domain.ProcedureTest;
import io.smarthealth.clinical.procedure.domain.enumeration.Gender;
import io.smarthealth.stock.item.data.SimpleItemData;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class ProcedureTestData {
    private Long id;
    private String itemCode;
    private String procedureName; //government classifications
    private Boolean consent; 
    private Boolean withRef; 
    private Boolean refOut; 
    private Boolean status; 
    private String notes;
    private Boolean supervisorConfirmation;
    @Enumerated(EnumType.STRING)
    private Gender gender;  
    private SimpleItemData itemData;
    
    public static ProcedureTestData map(ProcedureTest rtd){
        ProcedureTestData entity = new ProcedureTestData();
        entity.setId(rtd.getId());
        entity.setConsent(rtd.getConsent());
        entity.setGender(rtd.getGender());
        entity.setNotes(rtd.getNotes());
        entity.setRefOut(rtd.getRefOut());
        entity.setProcedureName(rtd.getProcedureName());
        if(rtd.getItem()!=null)
           entity.setItemData(rtd.getItem().toSimpleData());
        entity.setStatus(rtd.getStatus());
        entity.setSupervisorConfirmation(rtd.getSupervisorConfirmation());
        entity.setWithRef(rtd.getWithRef());
        
        
        return entity;
    }
    
     public static ProcedureTest map(ProcedureTestData rtd){
        ProcedureTest entity = new ProcedureTest();
        entity.setConsent(rtd.getConsent());
        entity.setGender(rtd.getGender());
        entity.setNotes(rtd.getNotes());
        entity.setRefOut(rtd.getRefOut());
        entity.setProcedureName(rtd.getProcedureName());
        entity.setStatus(rtd.getStatus());
        entity.setSupervisorConfirmation(rtd.getSupervisorConfirmation());
        entity.setWithRef(rtd.getWithRef());
        
        return entity;
    }
}
