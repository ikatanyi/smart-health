package io.smarthealth.organization.person.patient.api;

import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.person.data.AddressData;
import io.smarthealth.organization.person.data.ContactData;
import io.smarthealth.organization.person.data.PortraitData;
import io.smarthealth.organization.person.domain.PersonAddress;
import io.smarthealth.organization.person.domain.PersonContact;
import io.smarthealth.organization.person.domain.Portrait;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.organization.person.service.PersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

/**
 *
 * @author Kelsas
 */
@RestController
@RequestMapping("/api")
@Api(value = "Patient Controller", description = "Operations pertaining to patient entity")
public class PatientController {
    
    @Value("${upload.image.max-size:524288}")
    Long maxSize;
    @Autowired
    private PatientService patientService;
    @Autowired
    private PersonService personService;
    
    @Autowired
    private VisitService visitService;
    
    @Autowired
    ModelMapper modelMapper;
    
    @PostMapping("/patients")
    public @ResponseBody
    ResponseEntity<?> createPatient(@RequestBody @Valid final PatientData patientData) {
        LocalDate dateOfBirth = LocalDate.now().minusDays(Long.valueOf(patientData.getAge()));
        System.out.println("dateOfBirth " + dateOfBirth);
        patientData.setDateOfBirth(dateOfBirth);
        patientData.setPatientNumber(patientService.generatePatientNumber());
        Patient patient = this.patientService.createPatient(patientData);
        
        PatientData savedpatientData = patientService.convertToPatientData(patient);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/patients/{id}")
                .buildAndExpand(patient.getPatientNumber()).toUri();
        
        return ResponseEntity.created(location).body(savedpatientData);
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
    public ResponseEntity<List<PatientData>> fetchAllPatients(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        
        Page<PatientData> page = patientService.fetchAllPatients(pageable).map(p -> patientService.convertToPatientData(p));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @PutMapping("/patients/{id}")
    public @ResponseBody
    ResponseEntity<PatientData> updatePatient(@PathVariable("id") final String patientNumber,
            @RequestBody final PatientData patientData) {
        final Patient patient;
        if (this.patientService.patientExists(patientNumber)) {
            patient = patientService.findPatientOrThrow(patientNumber);
            
            patient.setAlive(patientData.isAlive());
            patient.setAllergyStatus(patientData.getAllergyStatus());
            patient.setBloodType(patientData.getBloodType());
            patient.setGender(patientData.getGender().name());
            patient.setGivenName(patientData.getGivenName());
            patient.setMaritalStatus(patientData.getMaritalStatus().name());
            patient.setMiddleName(patientData.getMiddleName());
            patient.setPatientNumber(patientData.getPatientNumber());
            patient.setStatus(patientData.getStatus());
            patient.setSurname(patientData.getSurname());
            patient.setTitle(patientData.getTitle());
            patient.setDateOfBirth(patientData.getDateOfBirth());
            
            this.patientService.updatePatient(patientNumber, patient);
        } else {
            throw APIException.notFound("Patient {0} not found.", patientNumber);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/patients/{id}")
                .buildAndExpand(patient.getPatientNumber()).toUri();
        
        return ResponseEntity.created(location).body(patientService.convertToPatientData(patient));
    }
    
    @PutMapping("/patients/{patientid}/contacts/{contactid}")
    @ApiOperation(value = "Update a patient's contact details", response = PatientData.class)
    public @ResponseBody
    ResponseEntity<PatientData> updatePatientContacts(
            @PathVariable("patientid") final String patientNumber,
            @PathVariable("contactid") final Long contactid,
            @RequestBody final ContactData contactData) {
        final PersonContact contact;
        Patient patient = patientService.findPatientOrThrow(patientNumber);
        if (this.personService.contactExists(contactid)) {
            contact = personService.fetchContactById(contactid);
            contact.setEmail(contactData.getEmail());
            contact.setMobile(contactData.getMobile());
            contact.setTelephone(contactData.getTelephone());
            this.personService.updatePersonContact(contact);
        } else {
            throw APIException.notFound("Contact {0} not found.", contactid);
        }
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/patients/{id}")
                .buildAndExpand(patient.getPatientNumber()).toUri();
        return ResponseEntity.created(location).body(patientService.convertToPatientData(patient));
    }
    
    @PutMapping("/patients/{patientid}/address/{addressid}")
    @ApiOperation(value = "Update a patient's address details", response = PatientData.class)
    public @ResponseBody
    ResponseEntity<PatientData> updatePatientAddress(
            @PathVariable("patientid") final String patientNumber,
            @PathVariable("addressid") final Long addressid,
            @RequestBody final AddressData addressData) {
        final PersonAddress address;
        Patient patient = patientService.findPatientOrThrow(patientNumber);
        if (this.personService.addressExists(addressid)) {
            address = personService.fetchAddressById(addressid);
            address.setCountry(addressData.getCountry());
            address.setCounty(addressData.getCounty());
            address.setLine1(addressData.getLine1());
            address.setLine2(addressData.getLine2());
            address.setPostalCode(addressData.getPostalCode());
            address.setTown(addressData.getTown());
            this.personService.updatePersonAddress(address);
        } else {
            throw APIException.notFound("Address {0} not found.", addressid);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/patients/{id}")
                .buildAndExpand(patient.getPatientNumber()).toUri();
        
        return ResponseEntity.created(location).body(patientService.convertToPatientData(patient));
    }
    
    @PostMapping("/patients/{id}/image")
    @ApiOperation(value = "Update a patient's image details", response = Portrait.class)
    public @ResponseBody
    ResponseEntity<PortraitData> postPatientImage(@PathVariable("id") final String patientNumber,
            @RequestParam final MultipartFile image) {
        System.out.println("image " + image.getName());
        if (image == null) {
            throw APIException.badRequest("Image not found");
        }
        
        Patient patient = patientService.findPatientOrThrow(patientNumber);
        this.throwIfInvalidSize(image.getSize());
        this.throwIfInvalidContentType(image.getContentType());
        
        try {
            //delete if any existing
            this.patientService.deletePortrait(patientNumber);
            Portrait portrait = this.patientService.createPortrait(patient, image);
            URI location = fromCurrentRequest().buildAndExpand(portrait.getId()).toUri();
            PortraitData data = modelMapper.map(portrait, PortraitData.class);
            return ResponseEntity.created(location).body(data);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw APIException.internalError("Error saving patient's image ", ex.getMessage());
        }
        
    }
    
    private void throwIfInvalidSize(final Long size) {
        
        if (size > maxSize) {
            throw APIException.badRequest("Image can''t exceed size of {0}", maxSize);
        }
    }
    
    private void throwIfInvalidContentType(final String contentType) {
        if (!contentType.contains(MediaType.IMAGE_JPEG_VALUE)
                && !contentType.contains(MediaType.IMAGE_PNG_VALUE)) {
            throw APIException.badRequest("Only content type {0} and {1} allowed", MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);
        }
    }
    
}
