/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.wardprocedure.domain.repository;

import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.wardprocedure.domain.NursingNotes;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface NursingNotesRepository extends JpaRepository<NursingNotes, Long> {

    List<NursingNotes> findByAdmission(final Admission admission);
}
