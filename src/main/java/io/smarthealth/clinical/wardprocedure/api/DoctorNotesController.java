/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.wardprocedure.api;

import io.smarthealth.clinical.wardprocedure.data.DoctorNotesData;
import io.smarthealth.clinical.wardprocedure.domain.DoctorNotes;
import io.smarthealth.clinical.wardprocedure.service.DoctorNotesService;
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
public class DoctorNotesController {

    private final DoctorNotesService doctorNotesService;

    //create
    @PostMapping("/doctor-notes")
//    @PreAuthorize("hasAuthority('create_nursing_notes')")
    public ResponseEntity<?> createDoctorNote(@Valid @RequestBody DoctorNotesData doctorNotesData) {
        DoctorNotes doctorNotes = doctorNotesService.createDoctorNotes(doctorNotesData);

        Pager<DoctorNotesData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Doctor note successfully submitted");
        pagers.setContent(DoctorNotesData.map(doctorNotes));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    //read
    @GetMapping("/doctor-notes/{id}")
//    @PreAuthorize("hasAuthority('view_nursing_notes')")
    public ResponseEntity<?> findDoctorNoteById(
            @PathVariable("id") final Long id
    ) {

        DoctorNotes doctorNotes = doctorNotesService.fetchDoctorNoteById(id);

        Pager<DoctorNotesData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Doctor note data");
        pagers.setContent(DoctorNotesData.map(doctorNotes));

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    //read all 
    @GetMapping("/doctor-notes/admission/{admissionNumber}")
//    @PreAuthorize("hasAuthority('view_nursing_notes')")
    public ResponseEntity<?> findDoctorNotesByAdmissionNumber(
            @PathVariable("admissionNumber") final String admissionNumber) {

        List<DoctorNotesData> list = doctorNotesService.fetchDoctorNotesByAdmissionNumber(admissionNumber).stream().map(t -> DoctorNotesData.map(t)).collect(Collectors.toList());

        ListData<DoctorNotesData> listData = new ListData();
        listData.setCode("200");
        listData.setMessage("Success");
        listData.setContent(list);

        return ResponseEntity.status(HttpStatus.OK).body(listData);
    }

    //update
    @PutMapping("/doctor-notes/{id}")
//    @PreAuthorize("hasAuthority('update_nursing_notes')")
    public ResponseEntity<?> updateDoctorNote(
            @PathVariable("id") final Long id,
            @Valid @RequestBody DoctorNotesData doctorNotesData) {
        DoctorNotes doctorNotes = doctorNotesService.updateDoctorNote(id, doctorNotesData);

        Pager<DoctorNotesData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Doctor note successfully updated");
        pagers.setContent(DoctorNotesData.map(doctorNotes));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    //delete
    @DeleteMapping("/doctor-notes/{id}")
//    @PreAuthorize("hasAuthority('update_nursing_notes')")
    public ResponseEntity<?> removeDoctorNote(@PathVariable("id") final Long id) {
        doctorNotesService.removeDoctorNote(id);

        Pager<DoctorNotesData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Doctor note successfully deleted");

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

}
