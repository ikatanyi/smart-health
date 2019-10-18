/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.item.domain;

/**
 *
 * @author Kelsas
 */
public class PriceBook {
    public enum Type{
        Sales,
        Purchases
    }
    private String name;
    private Type type;
    private String itemRate; // Markup or markdown  the items by percentage || 
}
