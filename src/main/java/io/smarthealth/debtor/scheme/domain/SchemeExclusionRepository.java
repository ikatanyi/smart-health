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
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface SchemeExclusionRepository extends JpaRepository<SchemeExclusions, Long>, JpaSpecificationExecutor<SchemeExclusions> {

    Page<SchemeExclusions> findByScheme(Scheme scheme, Pageable pageable);

    @Query("SELECT e FROM SchemeExclusions e WHERE e.item.id=:itemId AND e.scheme.id =:schemeId")
    Optional<SchemeExclusions> findExclusions(@Param("itemId") Long ItemId, @Param("schemeId") Long schemeId);
}
