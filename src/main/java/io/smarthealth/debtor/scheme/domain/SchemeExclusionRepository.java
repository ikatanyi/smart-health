/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import io.smarthealth.debtor.payer.domain.Scheme;

/**
 *
 * @author Kelsas
 */
public interface SchemeExclusionRepository extends JpaRepository<SchemeExclusions, Long>, JpaSpecificationExecutor<SchemeExclusions> {

    Page<SchemeExclusions> findByScheme(Scheme scheme, Pageable pageable);
}
