package io.smarthealth.clinical.admission.data;

import io.smarthealth.clinical.admission.domain.NhifRebate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kent
 */
@Data
public class NhifRebateData {
    private String patientName;
    private String patientNumber;
    private Double amount;
    private Double rate;
    private String admissionNumber;
    private Integer duration;
    private String memberNumber;
    private LocalDateTime date;
    
    public NhifRebate map(){
        NhifRebate data = new NhifRebate();
        data.setAmount(this.getAmount());
        data.setDate(this.getDate());
        data.setDuration(this.getDuration());
        data.setRate(this.getRate());
        data.setMemberNumber(this.getMemberNumber());
        return data;
    }
}
