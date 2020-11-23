package io.smarthealth.debtor.scheme.data;

import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.domain.Scheme.SchemeType;
import io.smarthealth.debtor.scheme.domain.enumeration.PolicyCover;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class SchemeData {

    private Long payerId;

    private String schemeCode;
    @ApiModelProperty(required = true)
    private String schemeName;
    @Enumerated(EnumType.STRING)
    private PolicyCover cover;
    //private String category;

    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private SchemeType type;
    private Boolean active;
    private String telNo;
    private String mobileNo;
    private String emailAddress;
    private String line1;
    private String line2;
    private SchemConfigData configData;

    @ApiModelProperty(hidden = true, required = false)
    private Long schemeId;
    @ApiModelProperty(hidden = true, required = false)
    private String payerName;    
    

    public static SchemeData map(Scheme i) {
        SchemeData d = new SchemeData();
//        d.setCategory(i.getCategory());
        d.setSchemeCode(i.getSchemeCode());
        d.setCover(i.getCover());
        d.setEmailAddress(i.getEmailAddress());
        d.setLine1(i.getLine1());
        d.setLine2(i.getLine2());
        d.setMobileNo(i.getMobileNo());
        d.setPayerId(i.getPayer().getId());
        d.setPayerName(i.getPayer().getPayerName());
        d.setSchemeName(i.getSchemeName());
        d.setTelNo(i.getTelNo());
        d.setSchemeId(i.getId());
        d.setSchemeCode(i.getSchemeCode());
        
        return d;
    }

    public static Scheme map(SchemeData d) {
        Scheme i = new Scheme();
//        i.setCategory(d.getCategory());
        i.setSchemeCode(d.getSchemeCode());
        i.setCover(d.getCover());
        i.setEmailAddress(d.getEmailAddress());
        i.setLine1(d.getLine1());
        i.setLine2(d.getLine2());
        i.setMobileNo(d.getMobileNo());
        i.setSchemeName(d.getSchemeName());
        i.setTelNo(d.getTelNo());
        i.setSchemeCode(d.getSchemeCode());
        return i;
    }
}
