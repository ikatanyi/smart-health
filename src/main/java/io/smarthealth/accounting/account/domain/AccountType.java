/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.accounting.account.domain.enumeration.AccountCategory;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_type")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountType extends Identifiable {

    @Enumerated(EnumType.STRING)
    private AccountCategory glAccountType;
    private String type;
    private String description;
}
