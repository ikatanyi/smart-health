/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import io.smarthealth.accounting.pettycash.domain.PettyCashApprovals;
import io.smarthealth.accounting.pettycash.domain.PettyCashApprovedItems;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PettyCashApprovalsData {

    private String approverName;
    private Long approverId;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime approvalDate;
    @Enumerated(EnumType.STRING)
    private PettyCashStatus approvalStatus;

    private String approvalComments;
    private double amount;
    List<PettyCashApprovedItemsData> approvedItems;

    public static PettyCashApprovalsData map(PettyCashApprovals entity) {
        PettyCashApprovalsData data = new PettyCashApprovalsData();
        data.setApprovalComments(entity.getApprovalComments());
        data.setApprovalStatus(entity.getApprovalStatus());
        data.setApproverId(entity.getApprovedBy().getId());
        data.setApproverName(entity.getApprovedBy().getName());
        LocalDateTime ldt = LocalDateTime.ofInstant(entity.getCreatedOn(), ZoneOffset.systemDefault());
        data.setApprovalDate(ldt);

        //data.setAmount(entity.getAmount());
        if (!entity.getApprovedItems().isEmpty()) {
            double amount = 0;
            List<PettyCashApprovedItemsData> itemDataList = new ArrayList<>();
            for (PettyCashApprovedItems item : entity.getApprovedItems()) {
                amount = amount + item.getAmount();
                PettyCashApprovedItemsData itemData = PettyCashApprovedItemsData.map(item);
                itemDataList.add(itemData);
            }
            data.setApprovedItems(itemDataList);
            data.setAmount(amount);
        }
        return data;
    }
}
