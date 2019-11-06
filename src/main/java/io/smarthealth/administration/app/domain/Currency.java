/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.app.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
public class Currency extends Identifiable {

    private String code;
    private String symbol;
    private String name;
    private Integer decimalPlaces;
    private String format;
    private boolean active;
}
