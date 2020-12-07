/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.theatre.data;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class TheatreBillItem {

    private Long itemId;
    private String itemCode;
    private Double quantity = 1D;
    private Double price = 0D;
    private Double amount = 0D;
    private Long storeId;
    private List<TheatreProvider> providers = new ArrayList<>();

}
