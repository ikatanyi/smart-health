/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.medicaltemplate.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Simon.waweru
 */
@Repository
public interface MedicalTemplateRepository extends JpaRepository<MedicalTemplate, Long> {

    Optional<MedicalTemplate> findByTemplateName(final String templateName);
}
