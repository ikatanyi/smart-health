/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.data;

import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PatientIdentifierDTO {

    private String type;
    private String value;
    private Boolean validated;
}
