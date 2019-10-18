/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.sequence;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
public class SequenceFormat extends Identifiable{
    @Enumerated(EnumType.STRING)
    private SequenceType idType;
    
    @Column(length = 10)
    private String prefix;
    
    @Column(length = 10)
    private String suffix;
   
    private int maxLength;
}
