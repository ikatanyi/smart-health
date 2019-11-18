/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.app.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.Waweru
 */
public interface BankRepository extends JpaRepository<MainBank, Long> {

    Optional<MainBank> findByBankName(final String bankName);
}
