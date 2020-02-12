/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.data;

import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequests;
import io.swagger.annotations.ApiModelProperty;
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
public class PettyCashRequestsData {

    private double totalAmount;
    private String requestNo;

    @ApiModelProperty(hidden = true)
    private String requestedBy;
    private String requesterDept;

    private String narration;

    @Enumerated(EnumType.STRING)
    private PettyCashStatus status;

    @ApiModelProperty(hidden = true)
    private List<PettyCashRequestItemsData> requestItemsData;

    public static PettyCashRequestsData map(PettyCashRequests r) {
        PettyCashRequestsData data = new PettyCashRequestsData();
        data.setNarration(r.getNarration());
        data.setRequestNo(r.getRequestNo());
        data.setStatus(r.getStatus());
        data.setTotalAmount(r.getTotalAmount());
        data.setRequestedBy(r.getRequestedBy().getFullName());
        data.setRequesterDept(r.getRequestedBy().getDepartment().getName());
        if (!r.getPettyCashRequestItems().isEmpty()) {
            List<PettyCashRequestItemsData> requestItems = new ArrayList<>();
            r.getPettyCashRequestItems().forEach((ri) -> {
                PettyCashRequestItemsData d = PettyCashRequestItemsData.map(ri);
                requestItems.add(d);
            });
            data.setRequestItemsData(requestItems);
        }
        return data;
    }

    public static PettyCashRequests map(PettyCashRequestsData data) {
        PettyCashRequests r = new PettyCashRequests();
        r.setNarration(data.getNarration());
        r.setRequestNo(data.getRequestNo());
        r.setStatus(data.getStatus());
        //r.setTotalAmount(data.getTotalAmount());
        return r;
    }
}
