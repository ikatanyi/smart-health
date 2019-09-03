/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.data;

import lombok.Value;

/**
 *
 * @author Kelsas
 */
@Value
public class ActivityAccount {

    private Integer financialActivityId;
    private String accountIdentifier;
}
