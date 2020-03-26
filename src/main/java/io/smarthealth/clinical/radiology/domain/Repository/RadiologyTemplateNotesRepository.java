/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain.Repository;

import io.smarthealth.clinical.radiology.domain.RadiologyTemplateNotes;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.waweru
 */
public interface RadiologyTemplateNotesRepository extends JpaRepository<RadiologyTemplateNotes, Long> {

}
