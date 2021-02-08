/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.CardData;

import lombok.Data;

/**
 *
 * @author kent
 */
@Data
public class NonMemMap{
    public String card_claimingreason;
    public boolean card_retcompleted;
    public int otp_enabled;
    public boolean switch_integrated;
    public String otp_code;
    public boolean formatfixed;
    public boolean card_claimingpossible;
    public int registered_memcount;
    public boolean card_reterrors;
    public String patient_hospitalnumber;
    public boolean virtualcard;
    public boolean fp_extended;
    public String supervisor_password;
    public String medicalaid_scheme;
    public int code_file_version;
    public boolean card_claimfilesloading;
    public int fp_activemember;
    public boolean btupdate_completed;
}
