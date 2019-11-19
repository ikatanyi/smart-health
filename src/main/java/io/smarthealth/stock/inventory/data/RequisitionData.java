package io.smarthealth.stock.inventory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.stock.inventory.domain.*;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.inventory.domain.enumeration.RequisitionStatus;
import io.smarthealth.stock.inventory.domain.enumeration.RequisitionType;
import io.smarthealth.stock.stores.domain.Store;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public class RequisitionData {

    private Long id;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate transactionDate;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate requiredDate;
    private Long storeid;
    private String store;
    private String requestionNo;
    private RequisitionType requisitionType;
    private RequisitionStatus requisitionStatus;
    private String requestedBy;
    //we need a status for this
    private String terms;
    private List<RequisitionItemData> requistionLines;
    
    public static RequisitionData map(Requisition requisition){
        RequisitionData data=new RequisitionData();
        data.setId(requisition.getId());
        data.setTransactionDate(requisition.getTransactionDate());
        data.setRequiredDate(requisition.getRequiredDate());
        if(requisition.getStore()!=null){
            data.setStoreid(requisition.getStore().getId());
            data.setStore(requisition.getStore().getStoreName());
        }
        data.setRequestionNo(requisition.getRequestionNumber());
        data.setRequisitionStatus(requisition.getStatus());
        data.setRequisitionType(requisition.getType());
        data.setRequestedBy(requisition.getRequestedBy());
        data.setTerms(requisition.getTerms());
        
        if(requisition.getRequistionLines()!=null){
           data.setRequistionLines(
                   requisition.getRequistionLines()
                    .stream()
                    .map(req -> RequisitionItemData.map(req))
                    .collect(Collectors.toList())
           );
        }
        return data; 
    }
}
