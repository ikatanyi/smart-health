/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.data;

import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PersonIdentifierData {

    private Long idType;
    private Long id;
    private String identificationValue;
    private String identificationType;
    private Boolean validated = false;
}
