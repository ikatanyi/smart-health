/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.accountnumberformat.domain;

import io.smarthealth.infrastructure.accountnumberformat.domain.AccountNumberFormatEnumerations.AccountNumberPrefixType;
import io.smarthealth.infrastructure.accountnumberformat.service.AccountNumberFormatConstants;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Kelsas
 */
@Entity
@Table(name = AccountNumberFormatConstants.ACCOUNT_NUMBER_FORMAT_TABLE_NAME,
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {AccountNumberFormatConstants.ACCOUNT_TYPE_ENUM_COLUMN_NAME}, name = AccountNumberFormatConstants.ACCOUNT_TYPE_UNIQUE_CONSTRAINT_NAME)})
public class AccountNumberFormat extends Identifiable {

    @Column(name = AccountNumberFormatConstants.ACCOUNT_TYPE_ENUM_COLUMN_NAME, nullable = false)
    private Integer accountTypeEnum;

    @Column(name = AccountNumberFormatConstants.PREFIX_TYPE_ENUM_COLUMN_NAME, nullable = true)
    private Integer prefixEnum;

    protected AccountNumberFormat() {
        //
    }

    public AccountNumberFormat(EntityAccountType entityAccountType, AccountNumberPrefixType prefixType) {
        this.accountTypeEnum = entityAccountType.getValue();
        if (prefixType != null) {
            this.prefixEnum = prefixType.getValue();
        }
    }

    public Integer getAccountTypeEnum() {
        return this.accountTypeEnum;
    }

    public EntityAccountType getAccountType() {
        return EntityAccountType.fromInt(this.accountTypeEnum);
    }

    private void setAccountTypeEnum(Integer accountTypeEnum) {
        this.accountTypeEnum = accountTypeEnum;
    }

    public void setAccountType(EntityAccountType entityAccountType) {
        setAccountTypeEnum(entityAccountType.getValue());
    }

    public Integer getPrefixEnum() {
        return this.prefixEnum;
    }

    private void setPrefixEnum(Integer prefixEnum) {
        this.prefixEnum = prefixEnum;
    }

    public void setPrefix(AccountNumberPrefixType accountNumberPrefixType) {
        setPrefixEnum(accountNumberPrefixType.getValue());
    }

}
