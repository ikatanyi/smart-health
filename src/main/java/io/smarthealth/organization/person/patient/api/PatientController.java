package io.smarthealth.organization.person.patient.api;

import io.smarthealth.infrastructure.common.ApiResponse;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.person.data.AddressData;
import io.smarthealth.organization.person.data.ContactData;
import io.smarthealth.organization.person.data.PersonIdentifierData;
import io.smarthealth.organization.person.data.PersonNextOfKinData;
import io.smarthealth.organization.person.data.PortraitData;
import io.smarthealth.organization.person.domain.PersonAddress;
import io.smarthealth.organization.person.domain.PersonContact;
import io.smarthealth.organization.person.domain.PersonNextOfKin;
import io.smarthealth.organization.person.domain.Portrait;
import io.smarthealth.organization.person.patient.data.AllergyTypeData;
import io.smarthealth.organization.person.patient.data.PatientAllergiesData;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.domain.Allergy;
import io.smarthealth.organization.person.patient.domain.AllergyType;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientIdentificationType;
import io.smarthealth.organization.person.patient.domain.PatientIdentifier;
import io.smarthealth.organization.person.patient.service.AllergiesService;
import io.smarthealth.organization.person.patient.service.PatientIdentificationTypeService;
import io.smarthealth.organization.person.patient.service.PatientIdentifierService;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.organization.person.service.PersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Kelsas
 */
@RestController
@RequestMapping("/api")
@Api
public class PatientController {
    
    @Autowired
    private PatientService patientService;
    @Autowired
    private PersonService personService;
    
    @Autowired
    private AllergiesService allergiesService;
    
    @Autowired
    private PatientIdentificationTypeService patientIdentificationTypeService;
    
    @Autowired
    ModelMapper modelMapper;
    
    @Autowired
    private PatientIdentifierService patientIdentifierService;
    
    @PostMapping("/patients")
    @PreAuthorize("hasAuthority('create_patients')")
    public @ResponseBody
    ResponseEntity<?> createPatient(@RequestPart PatientData patientData, @RequestPart(name = "file", required = false) MultipartFile file) {
        
        Patient patient = this.patientService.createPatient(patientData, file);
        
        PatientData savedpatientData = patientService.convertToPatientData(patient);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/patients/{id}")
                .buildAndExpand(patient.getPatientNumber()).toUri();
        
        return ResponseEntity.created(location).body(ApiResponse.successMessage("Patient successfuly created", HttpStatus.CREATED, savedpatientData));
    }
    
    @GetMapping("/patients/{id}")
    @PreAuthorize("hasAuthority('view_patients')")
    public @ResponseBody
    ResponseEntity<PatientData> findPatientByPatientNumber(@PathVariable("id") final String patientNumber) {
        final Optional<PatientData> patient = this.patientService.fetchPatientByPatientNumber(patientNumber);
        if (patient.isPresent()) {
            return ResponseEntity.ok(patient.get());
        } else {
            throw APIException.notFound("Patient Number {0} not found.", patientNumber);
        }
    }
    
    @GetMapping("/patients")
    @PreAuthorize("hasAuthority('view_patients')")
    public ResponseEntity<?> fetchAllPatients(
            //@RequestParam(required = false) MultiValueMap<String, String> queryParams,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "results", required = false) Integer size,
            @RequestParam(value = "term", required = false) final String term,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            UriComponentsBuilder uriBuilder) {
        //Pageable pageable = Pageable.unpaged();
        Pageable pageable = null;
//        if (page == null) {
//            page = 0;
//        }
//        if (size == null) {
//            size = 10;
//        }
//        if (page == null && size == null) {
//            pageable = PageRequest.of(0, 200, Sort.by("id").descending());
//        }

        if (page != null && size != null) {
            pageable = PageRequest.of(page, size, Sort.by("id").descending());
        } else {
            pageable = Pageable.unpaged();
        }
        // pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<PatientData> pageResult = patientService.fetchAllPatients(term, dateRange, pageable).map(p -> patientService.convertToPatientData(p));
        Pager<List<PatientData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(pageResult.getContent());
        PageDetails details = new PageDetails();
        details.setPage(pageResult.getNumber());
        details.setPerPage(pageResult.getSize());
        details.setTotalElements(pageResult.getTotalElements());
        details.setTotalPage(pageResult.getTotalPages());
        details.setReportName("Patient Register");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
    }
    
    @PutMapping("/patients/{patientNumber}")
    @PreAuthorize("hasAuthority('edit_patients')")
    public @ResponseBody
    ResponseEntity<PatientData> updatePatient(@PathVariable("patientNumber") final String patientNumber,
            @RequestBody final PatientData patientData) {
        final Patient patient;
        if (this.patientService.patientExists(patientNumber)) {
            patient = patientService.findPatientOrThrow(patientNumber);

            //patient.setIsAlive(patientData.getIsAlive()==null?false: true);
            patient.setAllergyStatus(patientData.getAllergyStatus());
            patient.setBloodType(patientData.getBloodType());
            patient.setGender(patientData.getGender());
            patient.setGivenName(patientData.getGivenName());
            patient.setMaritalStatus(patientData.getMaritalStatus().name());
            patient.setMiddleName(patientData.getMiddleName());
            //patient.setPatientNumber(patientData.getPatientNumber());
            patient.setStatus(patientData.getStatus());
            patient.setSurname(patientData.getSurname());
            patient.setTitle(patientData.getTitle());
            patient.setDateOfBirth(patientData.getDateOfBirth());
            patient.setPrimaryContact(patientData.getPrimaryContact());
            
            this.patientService.updatePatient(patientNumber, patient);
        } else {
            //entDa
            throw APIException.notFound("Patient {0} not found.", patientNumber);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/patients/{id}")
                .buildAndExpand(patient.getPatientNumber()).toUri();
        
        return ResponseEntity.created(location).body(patientService.convertToPatientData(patient));
    }
    
    @GetMapping("/identifier/{identifier}/patient/{no}")
    @PreAuthorize("hasAuthority('view_patients')")
    public @ResponseBody
    ResponseEntity<PatientData> getPatientByIdentifier(@PathVariable("identifier") /*Identifier type*/ final String patientNumber, @PathVariable("no") final String patientNo) {
        final Optional<PatientData> patient = this.patientService.fetchPatientByPatientNumber(patientNumber);
        if (patient.isPresent()) {
            return ResponseEntity.ok(patient.get());
        } else {
            throw APIException.notFound("Patient Number {0} not found.", patientNumber);
        }
    }
    
    @PutMapping("/patients/{patientid}/contacts/{contactid}")
    @PreAuthorize("hasAuthority('edit_patients')")
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
    @PreAuthorize("hasAuthority('edit_patients')")
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
    
    @PostMapping("/patients/{patientNumber}/next-of-kin")
    @PreAuthorize("hasAuthority('edit_patients')")
    public ResponseEntity<?> createPatientNextOfKin(
            @Valid @RequestBody PersonNextOfKinData data,
            @PathVariable("patientNumber") final String patientNumber) {
        Patient patient = patientService.findPatientOrThrow(patientNumber);
        PersonNextOfKin nextOfKin = new PersonNextOfKin();
        nextOfKin.setName(data.getName());
        nextOfKin.setPerson(patient);
        nextOfKin.setPrimaryContact(data.getPrimaryContact());
        nextOfKin.setRelationship(data.getRelationship());
        nextOfKin.setSpecialNote(data.getSpecialNote());
        nextOfKin = patientService.createNextOfKin(nextOfKin);
        Pager<PersonNextOfKinData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Next of kin created");
        pagers.setContent(PersonNextOfKinData.map(nextOfKin));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
    
    @PutMapping("/patients/{nextOfKinId}/next-of-kin")
    @PreAuthorize("hasAuthority('edit_patients')")
    public ResponseEntity<?> editPatientNextOfKin(
            @Valid @RequestBody PersonNextOfKinData data,
            @PathVariable("nextOfKinId") final Long nextOfKinId) {
        PersonNextOfKin nextOfKin = patientService.findOrThrowNextOfKinById(nextOfKinId);
        nextOfKin.setName(data.getName());
        nextOfKin.setPrimaryContact(data.getPrimaryContact());
        nextOfKin.setRelationship(data.getRelationship());
        nextOfKin.setSpecialNote(data.getSpecialNote());
        nextOfKin = patientService.createNextOfKin(nextOfKin);
        Pager<PersonNextOfKinData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Next of kin updated");
        pagers.setContent(PersonNextOfKinData.map(nextOfKin));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
    
    @PostMapping("/patients/{id}/image")
    @PreAuthorize("hasAuthority('create_patients')")
    @ApiOperation(value = "Update a patient's image details", response = Portrait.class)
    public @ResponseBody
    ResponseEntity<PortraitData> postPatientImage(@PathVariable("id") final String patientNumber, @RequestParam final MultipartFile image) {
        Patient patient = patientService.findPatientOrThrow(patientNumber);
        
        try {
            
            Portrait portrait = this.patientService.createPortrait(patient, image);
            URI location = fromCurrentRequest().buildAndExpand(portrait.getId()).toUri();
            PortraitData data = modelMapper.map(portrait, PortraitData.class);
            return ResponseEntity.created(location).body(data);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw APIException.internalError("Error saving patient's image ", ex.getMessage());
        }
        
    }
    
    @PostMapping("/patient_identification_type")
    @PreAuthorize("hasAuthority('create_patients')")
    public @ResponseBody
    ResponseEntity<?> createPatientIdtype(@RequestBody @Valid final PatientIdentificationType patientIdentificationType) {
        
        PatientIdentificationType patientIdtype = this.patientIdentificationTypeService.creatIdentificationType(patientIdentificationType);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/patient_identification_type/{id}")
                .buildAndExpand(patientIdtype.getId()).toUri();
        
        return ResponseEntity.created(location).body(ApiResponse.successMessage("Identity type was successfully created", HttpStatus.CREATED, patientIdtype));
    }

    /*
    @GetMapping("/patient_identification_type")
    public ResponseEntity<List<PatientIdentificationType>> fetchAllPatientIdTypes(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

//        return new ResponseEntity<List<PatientIdentificationType>>(patientIdentificationTypeService.fetchAllPatientIdTypes(), HttpStatus.OK);
        return new ResponseEntity<>(patientIdentificationTypeService.fetchAllPatientIdTypes(), HttpStatus.OK);
    }
     */
    @GetMapping("/patient_identification_type")
    @PreAuthorize("hasAuthority('view_patients')")
    public ResponseEntity<List<PatientIdentificationType>> fetchAllPatientIdTypes(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

//        return new ResponseEntity<List<PatientIdentificationType>>(patientIdentificationTypeService.fetchAllPatientIdTypes(), HttpStatus.OK);
        return new ResponseEntity<>(patientIdentificationTypeService.fetchAllPatientIdTypes(), HttpStatus.OK);
    }
    
    @GetMapping("/patient_identification_type/{id}")
    @PreAuthorize("hasAuthority('view_patients')")
    public PatientIdentificationType fetchAllPatientIdTypes(@PathVariable("id") final String patientIdType) {
        return patientIdentificationTypeService.fetchIdType(Long.valueOf(patientIdType));
    }

    /* Functions pertaining patient allergies */
    @PostMapping("/allergy")
    @PreAuthorize("hasAuthority('create_patients')")
    public @ResponseBody
    ResponseEntity<?> createPatientAllergy(@RequestBody @Valid final PatientAllergiesData patientAllergiesData) {
        Patient patient = patientService.findPatientOrThrow(patientAllergiesData.getPatientNumber());
        Allergy toSave = allergiesService.convertAllergyDataToEntity(patientAllergiesData);
        AllergyType allergyType = allergiesService.findAllergyTypeByCode(patientAllergiesData.getAllergyType());
        toSave.setPatient(patient);
        toSave.setAllergyType(allergyType);
        Allergy allergy = allergiesService.createPatientAllergy(toSave);
        PatientAllergiesData savedData = allergiesService.convertPatientAllergiesToData(allergy);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/allergy/{id}")
                .buildAndExpand(allergy.getId()).toUri();
        return ResponseEntity.created(location).body(savedData);
    }
    
    @PostMapping("/allergy-type")
    @PreAuthorize("hasAuthority('create_patients')")
    public @ResponseBody
    ResponseEntity<?> createAllergyType(@RequestBody @Valid final AllergyTypeData allergyTypeData) {
        AllergyType allergyType = allergiesService.createAllergyType(allergiesService.convertAllergyTypeDataToEntity(allergyTypeData));
        AllergyTypeData savedData = allergiesService.convertAllergyTypEntityToData(allergyType);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/allergy-type/{id}")
                .buildAndExpand(allergyType.getId()).toUri();
        return ResponseEntity.created(location).body(savedData);
    }
    
    @GetMapping("/patient/{patientNumber}/allergy")
    @PreAuthorize("hasAuthority('view_patients')")
    public ResponseEntity<?> fetchAllPatientsAllergy(@PathVariable("patientNumber") final String patientNumber, @RequestParam MultiValueMap<String, String> queryParams, Pageable pageable) {
        Patient patient = patientService.findPatientOrThrow(patientNumber);
        Page<PatientAllergiesData> page = allergiesService.fetchPatientAllergies(patient, pageable).map(p -> allergiesService.convertPatientAllergiesToData(p));
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }
    
    @GetMapping("/allergy-type")
    @PreAuthorize("hasAuthority('view_patients')")
    public ResponseEntity<List<AllergyTypeData>> fetchAllAllergyTypes() {
        List<AllergyTypeData> data = new ArrayList<>();
        allergiesService.findAllAllergyTypes().stream().map((at) -> {
            AllergyTypeData atd = new AllergyTypeData();
            atd.setCode(at.getCode());
            atd.setName(at.getName());
            return atd;
        }).forEachOrdered((atd) -> {
            data.add(atd);
        });
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    //PDF Reports
//    @RequestMapping(value = "/patient/export-patient-data", method = RequestMethod.GET)
//    public void export(ModelAndView model, HttpServletResponse response) throws IOException, JRException, SQLException {
//        JasperPrint jasperPrint = null;
//
//        response.setContentType("application/x-download");
//        response.setHeader("Content-Disposition", String.format("attachment; filename=\"patient.pdf\""));
//
//        OutputStream out = response.getOutputStream();
//        jasperPrint = patientService.exportPatientPdfFile();
//        JasperExportManager.exportReportToPdfStream(jasperPrint, out);
//    }
    //PDF Reports
    @RequestMapping(value = "/patient/patientFile", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_patients')")
    public void exportPatientFile(HttpServletResponse response) throws JRException, SQLException, IOException {
        String contentType = null;
        patientService.exportPatientPdfFile(response);
    }

    /* Functions pertaining patient identifier*/
    @PostMapping("/patients/{patientNumber}/identifiers")
    @PreAuthorize("hasAuthority('edit_patients')")
    public ResponseEntity<?> addPatientIdentifier(
            @Valid @RequestBody PersonIdentifierData data,
            @PathVariable("patientNumber") final String patientNumber) {
        Patient patient = patientService.findPatientOrThrow(patientNumber);
        
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setType(patientIdentificationTypeService.fetchIdType(data.getIdType()));
        patientIdentifier.setValue(data.getIdentificationValue());
        patientIdentifier.setPatient(patient);
        
        PatientIdentifier savedPi = patientIdentifierService.createPatientIdentifier(patientIdentifier);
        
        Pager<PersonIdentifierData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Patient identification type created");
        pagers.setContent(patientIdentifierService.convertIdentifierEntityToData(savedPi));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
    
    @PutMapping("/patients/{patientNumber}/identifiers/{identifierId}")
    @PreAuthorize("hasAuthority('edit_patients')")
    public ResponseEntity<?> editPatientIdentifier(
            @Valid @RequestBody PersonIdentifierData data,
            @PathVariable("patientNumber") final String patientNumber,
            @PathVariable("identifierId") final Long identifierId
    ) {
        Patient patient = patientService.findPatientOrThrow(patientNumber);
        Optional<PatientIdentifier> pi = patientIdentifierService.fetchPatientIdentifierByPatientAndId(patient, identifierId);
        if (!pi.isPresent()) {
            throw APIException.notFound("Patient identifier notified ", "");
        }
        PatientIdentifier patientIdentifier = pi.get();
        patientIdentifier.setType(patientIdentificationTypeService.fetchIdType(data.getIdType()));
        patientIdentifier.setValue(data.getIdentificationValue());
        
        PatientIdentifier savedPi = patientIdentifierService.createPatientIdentifier(patientIdentifier);
        
        Pager<PersonIdentifierData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Patient identification type updated");
        pagers.setContent(patientIdentifierService.convertIdentifierEntityToData(savedPi));
        
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }
    
}
