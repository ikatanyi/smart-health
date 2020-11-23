/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Simon.waweru
 */
@Repository
public interface ReferralsRepository extends JpaRepository<Referrals, Long>, JpaSpecificationExecutor<Referrals> {

    Optional<Referrals> findByVisit(final Visit visit);

    Optional<Referrals> findFirstByVisitOrderByIdDesc(final Visit visit);

}
