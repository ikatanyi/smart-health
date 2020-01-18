/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.member.data;

import io.smarthealth.debtor.member.domain.PayerMember;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author simz
 */
@Data
public class PayerMemberData {
    
    @ApiModelProperty(hidden = true, required = false)
    private Long memberId;
    private String policyNo;
    private String idNo;
    private String contactNo;
    private String memberName;
    private String relation;
    private LocalDate dob;
    private boolean status;
    
    public static PayerMemberData map(PayerMember member) {
        PayerMemberData data = new PayerMemberData();
        data.setPolicyNo(member.getPolicyNo());
        data.setIdNo(member.getIdNo());
        data.setContactNo(member.getContactNo());
        data.setMemberName(member.getMemberName());
        data.setRelation(member.getRelation());
        data.setDob(member.getDob());
        data.setMemberId(member.getId());
        data.setStatus(member.isStatus());
        return data;
    }
    
    public static PayerMember map(PayerMemberData data) {
        PayerMember member = new PayerMember();
        member.setPolicyNo(data.getPolicyNo());
        member.setIdNo(data.getIdNo());
        member.setContactNo(data.contactNo);
        member.setMemberName(data.getMemberName());
        member.setRelation(data.getRelation());
        member.setDob(data.getDob());
        member.setStatus(data.isStatus());
        return member;
    }
    
}
