/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility.ivorydata;

import io.smarthealth.organization.person.domain.enumeration.Gender;
import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PatientData {

    private String pvEntityNo;
    private String vFname;
    private String vMname;
    private String vLname;
    private Date dDOB;
    private String fvEntityTypeNo;
    private String vFileNo;
    private Date dDor;
    private String currentPatientNo;
    @Enumerated(EnumType.STRING)
    private Gender gender;
}
