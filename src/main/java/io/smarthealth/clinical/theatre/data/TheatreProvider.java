/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.theatre.data;

import io.smarthealth.clinical.theatre.domain.enumeration.FeeCategory;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class TheatreProvider {

    private Long medicId;
    private String medicName;
    private FeeCategory role;
}
