/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.bank.domain;

import io.smarthealth.organization.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author simon.waweru
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Page<BankAccount> findByOrganization(final Organization o, final Pageable pageable);
}
