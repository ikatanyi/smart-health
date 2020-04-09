/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.approval.data;

import io.smarthealth.approval.data.enums.ApprovalModule;
import io.smarthealth.approval.domain.ApprovalConfig;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class ApprovalConfigData {

    private int noOfApproveres;
    private int minNoOfApprovers;
    @Enumerated(EnumType.STRING)
    private ApprovalModule moduleName;

    public static ApprovalConfigData map(ApprovalConfig config) {
        ApprovalConfigData data = new ApprovalConfigData();
        data.setMinNoOfApprovers(config.getMinNoOfApprovers());
        data.setModuleName(config.getApprovalModule());
        data.setNoOfApproveres(config.getNoOfApproveres());
        return data;
    }

    public static ApprovalConfig map(ApprovalConfigData data) {
        ApprovalConfig config = new ApprovalConfig();
        config.setApprovalModule(data.getModuleName());
        config.setMinNoOfApprovers(data.getMinNoOfApprovers());
        config.setNoOfApproveres(data.getNoOfApproveres());
        return config;
    }

}
