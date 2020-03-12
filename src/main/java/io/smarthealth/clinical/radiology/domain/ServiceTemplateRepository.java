/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface ServiceTemplateRepository extends JpaRepository<ServiceTemplate, Long>{
//    Optional<ServiceTemplate> findTemplateById(final Long id);
}
