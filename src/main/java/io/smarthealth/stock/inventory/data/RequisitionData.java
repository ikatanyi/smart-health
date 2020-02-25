package io.smarthealth.stock.inventory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.inventory.domain.Requisition;
import io.smarthealth.stock.inventory.domain.enumeration.RequisitionStatus;
import io.smarthealth.stock.inventory.domain.enumeration.RequisitionType;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
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
    private Long storeId;
    private String store;
    
    private Long requestingStoreId;
    private String requestingStore;
    
    private String requestionNo;
    
    private Integer totalItemRequested;
    
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
            data.setStoreId(requisition.getStore().getId());
            data.setStore(requisition.getStore().getStoreName());
        }
        if(requisition.getRequestingStore()!=null){
            data.setRequestingStore(requisition.getRequestingStore().getStoreName());
            data.setRequestingStoreId(requisition.getRequestingStore().getId());
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
        data.setTotalItemRequested(requisition.getRequistionLines().size());
        return data; 
    }
}
