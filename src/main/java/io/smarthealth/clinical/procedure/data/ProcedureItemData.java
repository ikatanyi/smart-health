/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.data;

import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class ProcedureItemData {

    private String medicId;
    private String itemCode;
    private double itemPrice;
    private int quantity;
}
