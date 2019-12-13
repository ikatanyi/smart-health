/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.app.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 * @author Simon.Waweru
 */
public interface BankBranchRepository extends JpaRepository<BankBranch, Long> {

    Optional<BankBranch> findByBranchNameAndMainBank(final String branchName, final MainBank mainBank);
    
    Page<BankBranch> findByMainBank(final MainBank mainBank, final Pageable pageable);
}
