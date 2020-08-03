package io.smarthealth.clinical.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class AdmissionData {

    //patient details
    private Long id;
    private String admissionNumber; //this should be same as visit number
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime admissionDate;
    private String patientNumber;
    private String patientName;

    private VisitEnum.PaymentMethod paymentMethod;
    private Long wardId;
    private String wardName;
    private Long roomId;
    private String roomName;
    private Long bedId;
    private String bedName;
    private BedTypeData bedType;
    private Boolean discharged;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dischargeDate;
    private String dischargedBy;
    private VisitEnum.Status status;
    private List<CareTeamData> careTeam = new ArrayList<>();
}
