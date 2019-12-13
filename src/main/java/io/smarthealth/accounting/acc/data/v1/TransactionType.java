package io.smarthealth.accounting.acc.data.v1;

import io.smarthealth.accounting.acc.validation.contraints.ValidIdentifiers;

import javax.validation.constraints.NotEmpty;

public class TransactionType {

    @ValidIdentifiers
    private String code;
    @NotEmpty
    private String name;
    private String description;

    public TransactionType() {
        super();
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
