/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.accounting.data;

import io.smarthealth.financial.accounting.domain.Account;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class AccountDTO {

    private String code;
    @Enumerated(EnumType.STRING)
    private Account.Type type;
    private String name;
}
