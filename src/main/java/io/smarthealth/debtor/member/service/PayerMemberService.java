/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.member.service;

import io.smarthealth.debtor.member.domain.PayerMember;
import io.smarthealth.debtor.member.domain.PayerMemberRepository;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.infrastructure.exception.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author simz
 */
@Service
public class PayerMemberService {

    @Autowired
    PayerMemberRepository payerMemberRepository;

    public PayerMember createNewMember(PayerMember member) {
        return payerMemberRepository.save(member);
    }

    public Page<PayerMember> fetchPayerMemberByScheme(Scheme scheme, Pageable pageable) {
        return payerMemberRepository.findByScheme(scheme, pageable);
    }

    public PayerMember fetchMemberByPolicyNo(String policyNo) {
        return payerMemberRepository.findByPolicyNo(policyNo).orElseThrow(() -> APIException.notFound("Member identified by Policy No. {0} not found", policyNo));
    }
}
