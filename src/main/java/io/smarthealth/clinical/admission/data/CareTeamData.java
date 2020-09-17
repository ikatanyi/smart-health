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
    //void reason
    private String reason;

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
        d.setRole(ct.getCareRole());
        return d;
    }

    public static CareTeam map(CareTeamData d) {
        CareTeam e = new CareTeam();
        e.setCareRole(d.getRole());
        e.setDateAssigned(d.getDateAssigned());
        return e;
    }
}
