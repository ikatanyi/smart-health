/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.pharmacy.domain;

import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author kent
 */
public interface DispensedDrugsInterface {
    
    public String getItemId();

    public LocalDate getDispensedDate();

    public Double getPurchased();

    public String getDrug();

    public Double getQty();

    public Double getPrice();

    public Double getCost();
    
    public String getOtherReference();
}
