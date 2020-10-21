/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.record.data.enums.ReferralType;
import io.smarthealth.clinical.record.domain.Referrals;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class ReferralData {

    @Enumerated(EnumType.STRING)
    private ReferralType referralType;
    private String staffNumber;
    private String doctorName;
    private String doctorSpeciality;
    private String referralNotes;
    private String visitNo;
    private boolean includeVisitClinalNotes;
    private String chiefComplaints;
    private String examinationNotes;
    private Long doctorServiceId;
    private String patientName;
    private String patientNumber;
    private Long referralId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDateTime referralDate;

    public static ReferralData map(Referrals r) {
        ReferralData data = new ReferralData();
        if (r.getDoctor() != null) {
            data.setStaffNumber(r.getDoctor().getStaffNumber());
        }
        
        data.setDoctorName(r.getDoctorName());
        data.setDoctorSpeciality(r.getDoctorSpeciality());
        data.setReferralNotes(r.getReferralNotes());
        data.setReferralType(r.getReferralType());
        data.setVisitNo(r.getVisit().getVisitNumber());
        data.setIncludeVisitClinalNotes(r.isIncludeVisitClinalNotes());
        data.setChiefComplaints(r.getChiefComplaints());
        data.setExaminationNotes(r.getExaminationNotes());
        data.setPatientName(r.getPatient().getFullName());
        data.setPatientNumber(r.getPatient().getPatientNumber());
        LocalDateTime ldt = LocalDateTime.ofInstant(r.getCreatedOn(), ZoneOffset.systemDefault());
        data.setReferralDate(ldt);
        data.setReferralId(r.getId());
        return data;
    }

    public static Referrals map(ReferralData data) {
        Referrals r = new Referrals();
        r.setDoctorSpeciality(data.getDoctorSpeciality());
        r.setReferralNotes(data.getReferralNotes());
        r.setReferralType(data.getReferralType());
        r.setChiefComplaints(data.getChiefComplaints());
        r.setExaminationNotes(data.getExaminationNotes());
        r.setIncludeVisitClinalNotes(data.includeVisitClinalNotes);
        return r;
    }
}
