/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author Simon.Waweru
 */
@Entity
@Data
public class Disease extends Auditable {

    @java.lang.SuppressWarnings(value = "all")
    public Disease() {
        this.codeType = CodeType.ICD10;
    }

    private enum CodeType {
        ICD10,
        SNOMED
    }

    @Column(nullable = false)
    private String code;
    @Column(nullable = false, unique = true)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CodeType codeType;
}