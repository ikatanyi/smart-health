/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.laboratory.data;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class StatusRequest {

    public static enum Status {
        Collected,
        Entered,
        Validated,
        Paid
    }
    //collected, Entered, Paid
    @NotNull
    private Status status;
    private String doneBy;
    private String specimen;
}
