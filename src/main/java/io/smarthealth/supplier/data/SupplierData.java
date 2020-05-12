package io.smarthealth.supplier.data;

import io.smarthealth.administration.app.data.AddressData;
import io.smarthealth.administration.app.data.BankEmbeddedData;
import io.smarthealth.administration.app.data.ContactData;
import io.smarthealth.supplier.domain.enumeration.SupplierType;    
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
 
/**
 *
 * @author Kelsas
 */   
@Data
public class SupplierData {

    private Long id;
    private SupplierType supplierType;
    private String supplierName;
    private String legalName;
    private String taxNumber;
    private String website;

    private Long currencyId;
    private String currency;

    private Long pricebookId;
    private String pricebook;

    private Long paymentTermsId;
    private String paymentTerms;

    private  Long creditAccountId;
    private String creditAccount;
     private String creditAccountNo;
    private BankEmbeddedData bank;
    private AddressData addresses;
    private ContactData contact;
    private String status;
    //for reporting
    @ApiModelProperty(required=false, hidden=true)
    private String county;
    @ApiModelProperty(required=false, hidden=true)
    private String country;
    @ApiModelProperty(required=false, hidden=true)
    private String postalCode;
    @ApiModelProperty(required=false, hidden=true)
    private String address;
    @ApiModelProperty(required=false, hidden=true)
    private String email;
    @ApiModelProperty(required=false, hidden=true)
    private String phone;    
    @ApiModelProperty(required=false, hidden=true)
    private String mobile;


//     private List<AddressData> addresses;
//    private List<ContactData> contacts;
   
    private String notes;

}
