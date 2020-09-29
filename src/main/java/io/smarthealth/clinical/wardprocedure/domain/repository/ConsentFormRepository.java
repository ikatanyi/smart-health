/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.wardprocedure.domain.repository;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.wardprocedure.domain.ConsentForm;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Simon.waweru
 */
@Repository
public interface ConsentFormRepository extends JpaRepository<ConsentForm, Long> {

    List<ConsentForm> findByVisitAndConsentType(final Visit visit, final String consentType);

    List<ConsentForm> findByVisit(final Visit visit);

}
