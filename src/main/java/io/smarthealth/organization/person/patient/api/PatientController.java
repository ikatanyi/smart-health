package io.smarthealth.organization.person.patient.api;

import io.smarthealth.auth.data.UserData;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.person.data.AddressData;
import io.smarthealth.organization.person.data.ContactData;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Kelsas
 */
@RestController
@RequestMapping("/api")
public class PatientController {
    
    @Value("${upload.image.max-size:524288}")
    Long maxSize;
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private VisitService visitService;
    
    @Autowired
    ModelMapper modelMapper;
    
    @PostMapping("/patients")
    public @ResponseBody
    ResponseEntity<?> createPatient(@RequestBody @Valid final PatientData patientData) {
        Patient patient = this.patientService.createPatient(patientData);
        
        modelMapper.map(patient, patientData);
        patientData.setPatientId(patient.getId());
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(patient.getPatientNumber()).toUri();
        
        return ResponseEntity.created(location).body(patientData);
    }
    
    @GetMapping("/patients/{id}")
    public @ResponseBody
    ResponseEntity<PatientData> findPatient(@PathVariable("id") final String patientNumber) {
        final Optional<PatientData> patient = this.patientService.fetchPatientByPatientNumber(patientNumber);
        if (patient.isPresent()) {
            return ResponseEntity.ok(patient.get());
        } else {
            throw APIException.notFound("Patient Number {0} not found.", patientNumber);
        }
    }
    
    @GetMapping("/patients")
    public ResponseEntity<List<PatientData>> getAllUsers(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        
        Page<PatientData> page = patientService.fetchAllPatients(pageable).map(p -> convertToPatientData(p));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    private PatientData convertToPatientData(Patient patient) {
        PatientData patientData = modelMapper.map(patient, PatientData.class);
        if (!patient.getAddresses().isEmpty()) {
            List<AddressData> addresses = new ArrayList<>();
            
            patient.getAddresses().forEach((address) -> {
                AddressData addressData = modelMapper.map(address, AddressData.class);
                addresses.add(addressData);
            });
            patientData.setAddressDetails(addresses);
        }
        if (!patient.getContacts().isEmpty()) {
            List<ContactData> contacts = new ArrayList<>();
            patient.getContacts().forEach((contact) -> {
                ContactData contactData = modelMapper.map(contact, ContactData.class);
                contacts.add(contactData);
            });
            patientData.setContactDetails(contacts);
        }
        return patientData;
    }
}
