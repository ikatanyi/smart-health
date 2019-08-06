/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.item.data;

import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class UomData {

    private String categoryName;

    private String name;
    private double rounding;
    private String symbol;
    private double rate;
    private double factor;
    private boolean active;
}
