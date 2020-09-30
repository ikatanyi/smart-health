/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.wardprocedure.data;

import io.smarthealth.clinical.wardprocedure.domain.ConsentForm;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class ConsentFormData {

    @ApiModelProperty(hidden = true, required = false)
    private Long id;

    @NotNull(message = "Visit Number Is Required")
    private String visitNumber;
    private String notes;
    private String consentType;

    public ConsentForm fromData() {
        ConsentForm e = new ConsentForm();
        e.setConsentType(this.getConsentType());
        if (notes != null) {
            e.setNotes(this.getNotes().getBytes());
        }
        return e;
    }
}
