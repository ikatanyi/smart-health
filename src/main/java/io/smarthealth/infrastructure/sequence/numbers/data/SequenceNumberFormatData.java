/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.sequence.numbers.data;

import io.smarthealth.infrastructure.sequence.numbers.domain.EntitySequenceType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Deprecated
@Data
public class SequenceNumberFormatData {

    @Enumerated(EnumType.STRING)
    private EntitySequenceType sequenceType;

    private String prefix;

    private String suffix;
 
    private int maxLength = 9;
}
