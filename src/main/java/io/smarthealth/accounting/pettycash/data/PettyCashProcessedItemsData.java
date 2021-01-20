package io.smarthealth.accounting.pettycash.data;

import lombok.Data;

@Data
public class PettyCashProcessedItemsData {
    private Long itemId;
    private Double amount;
    private String approvalComments;
    private Double pricePerUnit;
    private Integer quantity;
    private Integer quantityApproved;
    private String requestNo;
    private Double unitPriceApproved;
}
