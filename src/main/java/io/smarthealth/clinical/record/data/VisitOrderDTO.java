package io.smarthealth.clinical.record.data;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class VisitOrderDTO {
    private String visitNumber;
    private LocalDateTime visitDate;
    private String patientNumber;
    private String patientName;
    private DoctorRequestData.RequestType requestType;
    private Long requestCount;
    private String formattedVisitDate;

    public VisitOrderDTO(String visitNumber, LocalDateTime visitDate, String patientNumber, String patientName, DoctorRequestData.RequestType requestType, Long requestCount) {
        this.visitNumber = visitNumber;
        this.visitDate = visitDate;
        this.patientNumber = patientNumber;
        this.patientName = patientName;
        this.requestType = requestType;
        this.requestCount = requestCount;
    }

    public String getFormattedVisitDate() {
        return visitDate!=null ? visitDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
    }
}
