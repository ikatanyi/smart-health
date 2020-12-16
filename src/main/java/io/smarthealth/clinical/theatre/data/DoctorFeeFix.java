package io.smarthealth.clinical.theatre.data;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DoctorFeeFix {
    private String itemCode;
    private String surgeonStaffNumber;
    private String anaestheticStaffNumber;
    private String visitNumber;
    private LocalDate billingDate = LocalDate.now();
}
