package io.smarthealth.clinical.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.admission.domain.CareTeam;
import io.smarthealth.clinical.admission.domain.CareTeamRole;
import io.smarthealth.infrastructure.lang.Constants;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class CareTeamData {
    @ApiModelProperty(hidden = true)
    private Long id;
    @ApiModelProperty(hidden = true)
    private String patientName;
    @ApiModelProperty(hidden = true)
    private String patientNumber;
    private String admissionNumber;
    private Long medicId;

    @ApiModelProperty(hidden = true)
    private String medicName;
    @ApiModelProperty(example = "Admitting,Nursing,Referring")
    @Enumerated(EnumType.STRING)
    private CareTeamRole role;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dateAssigned;
    private Boolean voided;
    //void reason
    private String reason;
    private Boolean isActive = Boolean.TRUE;

    public static CareTeamData map(CareTeam ct) {
        CareTeamData d = new CareTeamData();
        if (ct.getAdmission() != null) {
            d.setAdmissionNumber(ct.getAdmission().getAdmissionNo());
        }
        d.setDateAssigned(ct.getDateAssigned());
        d.setMedicId(ct.getMedic().getId());
        d.setMedicName(ct.getMedic().getFullName());
        d.setPatientName(ct.getPatient().getFullName());
        d.setPatientNumber(ct.getPatient().getPatientNumber());
        d.setVoided(ct.getVoided());
        d.setReason(ct.getReason());
        d.setRole(ct.getCareRole());
        d.setId(ct.getId());
        d.setIsActive(ct.getIsActive());
        return d;
    }

    public static CareTeam map(CareTeamData d) {
        CareTeam e = new CareTeam();
        e.setCareRole(d.getRole());
        e.setDateAssigned(d.getDateAssigned());
        e.setVoided(d.getVoided());
        e.setReason(d.getReason());
        e.setVoided(Boolean.FALSE);
        return e;
    }
}
