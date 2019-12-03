/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.data;

import com.machinezoo.sourceafis.FingerprintTemplate;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class PersonBiometricRecord {

    private String personId;
    private FingerprintTemplate template;
}
