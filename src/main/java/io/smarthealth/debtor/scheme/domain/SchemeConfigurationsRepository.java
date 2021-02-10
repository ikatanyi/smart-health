/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.domain;

import io.smarthealth.debtor.payer.domain.Scheme;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author simz
 */
public interface SchemeConfigurationsRepository extends JpaRepository<SchemeConfigurations, Long>,JpaSpecificationExecutor<SchemeConfigurations> {

    Optional<SchemeConfigurations> findByScheme(Scheme scheme);

    @Query(value = "SELECT c FROM SchemeConfigurations c WHERE c.scheme.id =: schemeId")
    Optional<SchemeConfigurations> findSchemeConfigurationsBySchemeId(Long schemeId);
}
