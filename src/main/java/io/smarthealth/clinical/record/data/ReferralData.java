/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import io.smarthealth.clinical.record.data.enums.ReferralType;
import io.smarthealth.clinical.record.domain.Referrals;
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
    
    public static ReferralData map(Referrals r) {
        ReferralData data = new ReferralData();
        if (r.getDoctor() != null) {
            data.setStaffNumber(r.getDoctor().getStaffNumber());
            data.setDoctorName(r.getDoctor().getFullName());
        }
        data.setDoctorSpeciality(r.getDoctorSpeciality());
        data.setReferralNotes(r.getReferralNotes());
        data.setReferralType(r.getReferralType());
        data.setVisitNo(r.getVisit().getVisitNumber());
        data.setIncludeVisitClinalNotes(r.isIncludeVisitClinalNotes());
        data.setChiefComplaints(r.getChiefComplaints());
        data.setExaminationNotes(r.getExaminationNotes());
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
