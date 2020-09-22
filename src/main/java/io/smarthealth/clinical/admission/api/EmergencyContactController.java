package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.EmergencyContactData;
import io.smarthealth.clinical.admission.domain.EmergencyContact;
import io.smarthealth.clinical.admission.service.EmergencyContactService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmergencyContactController {

    private final EmergencyContactService service;
     
    @GetMapping("/emergency-contact/{id}")
//    @PreAuthorize("hasAuthority('view_contact')")
    public EmergencyContact getItem(@PathVariable(value = "id") Long code) {
        EmergencyContact contact = service.getEmergencyContact(code);
        return  contact;
    }

    @GetMapping("/contact")
//    @PreAuthorize("hasAuthority('view_contact')")
    public ResponseEntity<?> getAllEmergencyContacts(
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "patientNumber", required = false) final String patientNumber,         
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<EmergencyContactData> list = service.fetchEmergencyContacts(name, patientNumber, term, pageable).map(u -> u.toData());
        
        
        Pager<List<EmergencyContactData>> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details=new PageDetails();
        details.setPage(list.getNumber()+1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Emergency Contacts");
        pagers.setPageDetails(details);
         
        return ResponseEntity.ok(pagers);
    }
    
    
     @PostMapping("/contact/{admissionId}")
//     @PreAuthorize("hasAuthority('create_contact')")
    public ResponseEntity<?> createEmergencyContact(@PathVariable("admissionId") Long id, @Valid @RequestBody EmergencyContactData contactData) {
        
        EmergencyContactData result = service.createEmergencyContact(id, contactData).toData();
        
        Pager<EmergencyContactData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Emergency Contact created successful");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    @PutMapping("/contact/{id}")
    @PreAuthorize("hasAuthority('create_contact')")
    public ResponseEntity<?> updateEmergencyContact(@PathVariable("id") Long id, @Valid @RequestBody EmergencyContactData contactData) {
        
        EmergencyContactData result = service.updateEmergencyContact(id,contactData).toData();
        
        Pager<EmergencyContactData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("EmergencyContact Updated successful");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    
    
}
