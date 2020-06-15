/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.payer.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.smarthealth.administration.app.data.AddressDat;
import io.smarthealth.administration.app.data.ContactData;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import io.smarthealth.debtor.scheme.domain.enumeration.DiscountType;
import io.smarthealth.debtor.scheme.domain.enumeration.PolicyCover;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Data
public class BatchPayerData {
    @JsonProperty(value = "payerType")
    private String payerType;
    @JsonProperty(value = "payerName")
    private String payerName;
    @JsonProperty(value = "legalName")
    private String legalName;
    @JsonProperty(value = "taxNumber")
    private String taxNumber;
    @JsonProperty(value = "website")
    private String website;    
    @JsonProperty(value = "insurance")
    private boolean insurance;
    
    //scheme
    @JsonProperty(value = "schemeCode")
    private String schemeCode;
    @JsonProperty(value = "schemeName")
    @ApiModelProperty(required = true)
    private String schemeName;
    @JsonProperty(value = "cover")
    @Enumerated(EnumType.STRING)
    private PolicyCover cover;
    @JsonProperty(value = "discountMethod")
    private String discountMethod;
    @JsonProperty(value = "discountValue")
    private Double discountValue;
    @JsonProperty(value = "coPayType")
    @Enumerated(EnumType.STRING)
    private String coPayType;
    @JsonProperty(value = "coPayValue")
    private Double coPayValue;
    @JsonProperty(value = "status")
    private Boolean status;
    @JsonProperty(value = "smartEnabled")
    private Boolean smartEnabled;
}
