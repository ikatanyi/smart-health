package io.smarthealth.clinical.procedure.data;

import io.smarthealth.clinical.procedure.domain.Procedure;
import io.smarthealth.clinical.procedure.domain.enumeration.Gender;
import io.smarthealth.stock.item.data.SimpleItemData;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class ProcedureData {
    @ApiModelProperty(required = false, hidden = true)
    private Long id;
    private String itemCode;
    private String procedureName;
    private Boolean status; 
    private String notes;
    @Enumerated(EnumType.STRING)
    private Gender gender;  
    @ApiModelProperty(required = false, hidden = true)
    private BigDecimal rate;
    @ApiModelProperty(required = false, hidden = true)
    private BigDecimal costRate;
    
    
    public static ProcedureData map(Procedure rtd){
        ProcedureData entity = new ProcedureData();
        entity.setId(rtd.getId());
        entity.setGender(rtd.getGender());
        entity.setNotes(rtd.getNotes());     
        entity.setProcedureName(rtd.getProcedureName());
        if(rtd.getItem()!=null){
           entity.setItemCode(rtd.getItem().getItemCode());
           entity.setRate(rtd.getItem().getRate());           
           entity.setCostRate(rtd.getItem().getCostRate());
        }
        entity.setStatus(rtd.getStatus());  
        return entity;
    }
    
     public static Procedure map(ProcedureData rtd){
        Procedure entity = new Procedure();
        entity.setGender(rtd.getGender());
        entity.setNotes(rtd.getNotes());
        entity.setStatus(rtd.getStatus()); 
        entity.setProcedureName(rtd.getProcedureName());
        return entity;
    }
}
