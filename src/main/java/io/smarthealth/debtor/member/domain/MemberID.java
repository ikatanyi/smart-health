/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.member.domain;

import io.smarthealth.debtor.payer.domain.Scheme;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author simz
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class MemberID implements Serializable {
    
    private Long scheme;
    private String policyNo;
}
