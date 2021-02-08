/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.CardData;

import lombok.Data;

/**
 *
 * @author Ikatanyi
 */
@Data
public class A1{
    public int card_validitystatus;
    public int card_retmasscounter;
    public String card_issuername;
    public String card_issuedate;
    public int card_retcounter;
    public String card_serialnumber;
}
