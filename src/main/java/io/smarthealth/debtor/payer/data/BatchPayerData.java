/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.payer.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.smarthealth.debtor.scheme.domain.enumeration.PolicyCover;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Data
public class BatchPayerData {
    private String payerType;
    private String payerName;
    private String legalName;
    private String taxNumber;
    private String website;    
    private boolean insurance;
    private String schemeCode;
    private String schemeName;
    private String cover;
    private String discountMethod;
    private Double discountValue;
    private String coPayType;
    private Double coPayValue;
    private Boolean status;
    private Boolean smartEnabled;
}
