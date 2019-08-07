/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
public class AccountBalance extends Identifiable {

    @OneToOne
    private Account account;
    @ManyToOne
    private FiscalYear fiscalYear;
    private Double monthOne;
    private Double monthTwo;
    private Double monthThree;
    private Double monthFour;
    private Double monthFive;
    private Double monthSix;
    private Double monthSeven;
    private Double monthEight;
    private Double monthNine;
    private Double monthTen;
    private Double monthEleven;
    private Double monthTwelve;
    
    public Double getBalance(){
        return monthOne
                +monthTwo+monthThree;
    }
}
