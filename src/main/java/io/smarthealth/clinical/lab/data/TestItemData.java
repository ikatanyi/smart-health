/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.data;

import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class TestItemData {

    private String itemName, itemCode, status;
    private Long requestId;
    private Long requestItemId;
    private double itemPrice;
    private int quantity;
}
