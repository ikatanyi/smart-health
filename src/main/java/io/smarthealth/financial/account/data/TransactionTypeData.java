/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.data;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class TransactionTypeData {

    private String code;
    @NotEmpty
    private String name;
    private String description;
}
