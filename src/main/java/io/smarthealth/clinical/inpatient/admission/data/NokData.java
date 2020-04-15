/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.inpatient.admission.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class NokData {

    public enum RelationType {
        FATHER, MOTHER, BROTHER, SISTER, SON, DAUGHTER, COUSIN, GRANDMOTHER, GRANDFATHER, GRANDSON, GRANDDAUGHTER, AUNT, UNCLE, HUSBAND, WIFE, SELF
    }
    private RelationType relationType;
    private String fullName;
    private String phoneNumber;
    private String email;
}
