/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.data;

import lombok.Data;
import io.smarthealth.debtor.scheme.domain.SchemeExclusions;

/**
 *
 * @author Kelsas
 */
@Data
public class SchemeExclusionData {

    private Long id;
    private Long payerId;
    private String payerName;
    private Long schemeId;
    private String schemeName;
    private Long itemId;
    private String itemCode;
    private String item;

    public static SchemeExclusionData map(SchemeExclusions schemeExclusion) {
        SchemeExclusionData data = new SchemeExclusionData();
        data.setId(schemeExclusion.getId());
        if (schemeExclusion.getItem() != null) {
            data.setItem(schemeExclusion.getItem().getItemName());
            data.setItemCode(schemeExclusion.getItem().getItemCode());
        }
        data.setItemId(schemeExclusion.getItem().getId());
        if (schemeExclusion.getScheme() != null) {
            data.setSchemeId(schemeExclusion.getScheme().getId());
            data.setSchemeName(schemeExclusion.getScheme().getSchemeName());

            data.setPayerId(schemeExclusion.getScheme().getPayer().getId());
            data.setPayerName(schemeExclusion.getScheme().getPayer().getPayerName());
        }
        return data;
    }
}
