/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.data;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class AccountCommand {

    @NotNull
    private Action action;
    private String comment;

    public enum Action {
        LOCK,
        UNLOCK,
        CLOSE,
        REOPEN
    }
}
