/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.member.domain;

import io.smarthealth.debtor.payer.domain.Scheme;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author simz
 */
@Repository
public interface PayerMemberRepository extends JpaRepository<PayerMember, Long>, JpaSpecificationExecutor<PayerMember> {
    
    Page<PayerMember> findByScheme(Scheme scheme, Pageable pageable);
    
    Optional<PayerMember> findByPolicyNo(String policyNo);
}
