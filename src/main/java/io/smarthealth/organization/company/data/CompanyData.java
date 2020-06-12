/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.company.data;

import io.smarthealth.administration.app.data.AddressData;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyData implements Serializable {

    private String id;
    private Long logoId;
    private String name;
    private String location;
    private String taxId;
    private String companyId; //represents facility id 
    private String fiscalYear;
    private String language;
    private String timeZone;
    private String dateFormat;
    private Long currencyId;
    private String currency;
    private String contactName;
    private String contactEmail;
    private AddressData address; 

    @PrePersist
    public void autofill() {
        String ids = UUID.randomUUID().toString();
        this.setId(ids);
    }
}
