/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.NursingNotesData;
import io.smarthealth.clinical.admission.domain.NursingNotes;
import io.smarthealth.clinical.admission.service.NursingNotesService;
import io.smarthealth.infrastructure.utility.ListData;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Simon.waweru
 */
@Api
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NursingNotesController {

    private final NursingNotesService nursingNotesService;

    //create
    @PostMapping("/nursing-notes")
//    @PreAuthorize("hasAuthority('create_nursing_notes')")
    public ResponseEntity<?> createNursingNote(@Valid @RequestBody NursingNotesData nursingNotesData) {
        NursingNotes nursingNotes = nursingNotesService.createNursingNotes(nursingNotesData);

        Pager<NursingNotesData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Nursing note successfully submitted");
        pagers.setContent(NursingNotesData.map(nursingNotes));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    //read
    @GetMapping("/nursing-notes/{id}")
//    @PreAuthorize("hasAuthority('view_nursing_notes')")
    public ResponseEntity<?> findNursingNoteById(
            @PathVariable("id") final Long id
    ) {

        NursingNotes nursingNotes = nursingNotesService.fetchNursingNoteById(id);

        Pager<NursingNotesData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Nursing note data");
        pagers.setContent(NursingNotesData.map(nursingNotes));

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    //read all 
    @GetMapping("/nursing-notes/admission/{admissionNumber}")
//    @PreAuthorize("hasAuthority('view_nursing_notes')")
    public ResponseEntity<?> findNursingNotesByAdmissionNumber(
            @PathVariable("admissionNumber") final String admissionNumber) {

        List<NursingNotesData> list = nursingNotesService.fetchNursingNotesByAdmissionNumber(admissionNumber).stream().map(t -> NursingNotesData.map(t)).collect(Collectors.toList());

        ListData<NursingNotesData> listData = new ListData();
        listData.setCode("200");
        listData.setMessage("Success");
        listData.setContent(list);

        return ResponseEntity.status(HttpStatus.OK).body(listData);
    }

    //update
    @PutMapping("/nursing-notes/{id}")
//    @PreAuthorize("hasAuthority('update_nursing_notes')")
    public ResponseEntity<?> updateNursingNote(
            @PathVariable("id") final Long id,
            @Valid @RequestBody NursingNotesData nursingNotesData) {
        NursingNotes nursingNotes = nursingNotesService.updateNursingNote(id, nursingNotesData);

        Pager<NursingNotesData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Nursing note successfully updated");
        pagers.setContent(NursingNotesData.map(nursingNotes));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    //delete
    @DeleteMapping("/nursing-notes/{id}")
//    @PreAuthorize("hasAuthority('update_nursing_notes')")
    public ResponseEntity<?> removeNursingNote(@PathVariable("id") final Long id) {
        nursingNotesService.removeNursingNote(id);

        Pager<NursingNotesData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Nursing note successfully deleted");

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

}
