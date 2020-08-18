package io.smarthealth.clinical.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.infrastructure.lang.Constants;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class AdmissionData {

    //patient details
    @ApiModelProperty(hidden = true)
    private Long id;
    @ApiModelProperty(hidden = true)
    private String admissionNumber; //this should be same as visit number
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime admissionDate;
    private String patientNumber;
    @ApiModelProperty(hidden = true)
    private String patientName;

    private VisitEnum.PaymentMethod paymentMethod;
    private Long wardId;

    @ApiModelProperty(hidden = true)
    private String wardName;

    private Long roomId;

    @ApiModelProperty(hidden = true)
    private String roomName;

    private Long bedId;

    @ApiModelProperty(hidden = true)
    private String bedName;

    private Long bedTypeId;
    @ApiModelProperty(hidden = true)
    private BedTypeData bedTypeData;
    @ApiModelProperty(hidden = true)
    private Boolean discharged = Boolean.FALSE;
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dischargeDate;

    @ApiModelProperty(hidden = true)
    private String dischargedBy;
    @ApiModelProperty(example = "CheckIn,CheckOut,Admitted,Transferred, Discharged, Booked")
    private VisitEnum.Status status;
    private List<CareTeamData> careTeam = new ArrayList<>();

    public static AdmissionData map(Admission adm) {
        AdmissionData d = new AdmissionData();
        d.setAdmissionDate(adm.getAdmissionDate());
        d.setAdmissionNumber(adm.getAdmissionNo());
        d.setBedId(adm.getBed().getId());
        d.setBedName(adm.getBed().getName());
        d.setBedTypeData(adm.getBedType().toData());
        d.setBedTypeId(adm.getBedType().getId());
        d.setCareTeam(adm.getCareTeam().stream().map(c -> CareTeamData.map(c)).collect(Collectors.toList()));
        d.setDischargeDate(adm.getAdmissionDate());
        d.setDischarged(adm.getDischarged());
        d.setDischargedBy(adm.getDischargedBy());
        d.setId(adm.getId());
        d.setPatientName(adm.getPatient().getFullName());
        d.setPatientNumber(adm.getPatient().getPatientNumber());
        d.setPaymentMethod(adm.getPaymentMethod());
        if (adm.getRoom() != null) {
            d.setRoomId(adm.getRoom().getId());
            d.setRoomName(adm.getRoom().getName());
        }
        d.setStatus(adm.getStatus());
        d.setWardId(adm.getWard().getId());
        d.setWardName(adm.getWard().getName());
        return d;
    }

    public static Admission map(AdmissionData adm) {
        Admission d = new Admission();
        d.setAdmissionDate(adm.getAdmissionDate());
       // d.setCareTeam(adm.getCareTeam().stream().map(c -> CareTeamData.map(c)).collect(Collectors.toList()));
        d.setDischargeDate(adm.getAdmissionDate());
        d.setDischarged(adm.getDischarged());
        d.setDischargedBy(adm.getDischargedBy());
        d.setPaymentMethod(adm.getPaymentMethod());
        d.setStatus(adm.getStatus());
        return d;
    }
}
