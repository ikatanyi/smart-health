/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.config.data;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Kelsas
 */
@Getter
@Setter
public class ExternalServicesPropertiesData {
     private final String name;
    private final String value;

    public ExternalServicesPropertiesData(final String name, final String value) {
        this.name = name;
        this.value = value;
    }
}
