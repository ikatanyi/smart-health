/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author simon.waweru
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByCode(final String accountCode);
}
