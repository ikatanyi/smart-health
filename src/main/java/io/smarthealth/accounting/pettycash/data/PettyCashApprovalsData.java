/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.data;

import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PettyCashApprovalsData {

    private String staffNumber;
    private String staffName;
    
    @Enumerated(EnumType.STRING)
    private PettyCashStatus approvalStatus;

    private String approvalComments;
}
