/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
public class TransactionType extends Identifiable {

    private String code; 
    private String name;
    private String description;
}
