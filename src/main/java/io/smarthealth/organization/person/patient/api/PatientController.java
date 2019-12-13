package io.smarthealth.organization.person.patient.api;

import io.smarthealth.infrastructure.common.APIResponse;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.person.data.AddressData;
import io.smarthealth.organization.person.data.ContactData;
import io.smarthealth.organization.person.data.PortraitData;
import io.smarthealth.organization.person.domain.PersonAddress;
import io.smarthealth.organization.person.domain.PersonContact;
import io.smarthealth.organization.person.domain.Portrait;
import io.smarthealth.organization.person.patient.data.AllergyTypeData;
import io.smarthealth.organization.person.patient.data.PatientAllergiesData;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.domain.Allergy;
import io.smarthealth.organization.person.patient.domain.AllergyType;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientIdentificationType;
import io.smarthealth.organization.person.patient.service.AllergiesService;
import io.smarthealth.organization.person.patient.service.PatientIdentificationTypeService;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.organization.person.service.PersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private AllergiesService allergiesService;

    @Autowired
    private PatientIdentificationTypeService patientIdentificationTypeService;

    @Autowired
    ModelMapper modelMapper;
    
    @Autowired
    SequenceService sequenceService;


    @PostMapping("/patients")
    public @ResponseBody
    ResponseEntity<?> createPatient(@RequestBody @Valid final PatientData patientData) {
        LocalDate dateOfBirth = LocalDate.now().minusYears(Long.valueOf(patientData.getAge()));
        patientData.setDateOfBirth(dateOfBirth);
        patientData.setPatientNumber(sequenceService.nextNumber(SequenceType.PatientNumber));
        Patient patient = this.patientService.createPatient(patientData);

        PatientData savedpatientData = patientService.convertToPatientData(patient);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/patients/{id}")
                .buildAndExpand(patient.getPatientNumber()).toUri();

        return ResponseEntity.created(location).body(APIResponse.successMessage("Patient successfuly created", HttpStatus.CREATED, savedpatientData));
    }

    @GetMapping("/patients/{id}")
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
    public ResponseEntity<?> fetchAllPatients(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        int pageNo = 1;
        int size = 10;
        if (queryParams.getFirst("page") != null) {
            pageNo = Integer.valueOf(queryParams.getFirst("page"));
        }
        if (queryParams.getFirst("results") != null) {
            size = Integer.valueOf(queryParams.getFirst("results"));
        }
        pageNo = pageNo - 1;
        pageable = PageRequest.of(pageNo, size, Sort.by("id").descending());
        Page<PatientData> page = patientService.fetchAllPatients(queryParams, pageable).map(p -> patientService.convertToPatientData(p));
        Pager<List<PatientData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(page.getContent());
        PageDetails details = new PageDetails();
        details.setPage(page.getNumber());
        details.setPerPage(page.getSize());
        details.setTotalElements(page.getTotalElements());
        details.setTotalPage(page.getTotalPages());
        details.setReportName("Patient Register");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
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
            //entDa
            throw APIException.notFound("Patient {0} not found.", patientNumber);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/patients/{id}")
                .buildAndExpand(patient.getPatientNumber()).toUri();

        return ResponseEntity.created(location).body(patientService.convertToPatientData(patient));
    }

    @GetMapping("/identifier/{identifier}/patient/{no}")
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

    @PostMapping("/patient_identification_type")
    public @ResponseBody
    ResponseEntity<?> createPatientIdtype(@RequestBody @Valid final PatientIdentificationType patientIdentificationType) {

        PatientIdentificationType patientIdtype = this.patientIdentificationTypeService.creatIdentificationType(patientIdentificationType);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/patient_identification_type/{id}")
                .buildAndExpand(patientIdtype.getId()).toUri();

        return ResponseEntity.created(location).body(APIResponse.successMessage("Identity type was successfully created", HttpStatus.CREATED, patientIdtype));
    }

    /*
    @GetMapping("/patient_identification_type")
    public ResponseEntity<List<PatientIdentificationType>> fetchAllPatientIdTypes(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

//        return new ResponseEntity<List<PatientIdentificationType>>(patientIdentificationTypeService.fetchAllPatientIdTypes(), HttpStatus.OK);
        return new ResponseEntity<>(patientIdentificationTypeService.fetchAllPatientIdTypes(), HttpStatus.OK);
    }
     */
    @GetMapping("/patient_identification_type")
    public ResponseEntity<List<PatientIdentificationType>> fetchAllPatientIdTypes(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

//        return new ResponseEntity<List<PatientIdentificationType>>(patientIdentificationTypeService.fetchAllPatientIdTypes(), HttpStatus.OK);
        return new ResponseEntity<>(patientIdentificationTypeService.fetchAllPatientIdTypes(), HttpStatus.OK);
    }

    @GetMapping("/patient_identification_type/{id}")
    public PatientIdentificationType fetchAllPatientIdTypes(@PathVariable("id") final String patientIdType) {
        return patientIdentificationTypeService.fetchIdType(Long.valueOf(patientIdType));
    }

    /* Functions pertaining patient allergies */
    @PostMapping("/allergy")
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
    public @ResponseBody
    ResponseEntity<?> createAllergyType(@RequestBody @Valid final AllergyTypeData allergyTypeData) {
        AllergyType allergyType = allergiesService.createAllergyType(allergiesService.convertAllergyTypeDataToEntity(allergyTypeData));
        AllergyTypeData savedData = allergiesService.convertAllergyTypEntityToData(allergyType);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/allergy-type/{id}")
                .buildAndExpand(allergyType.getId()).toUri();
        return ResponseEntity.created(location).body(savedData);
    }

    @GetMapping("/patient/{patientNumber}/allergy")
    public ResponseEntity<?> fetchAllPatientsAllergy(@PathVariable("patientNumber") final String patientNumber, @RequestParam MultiValueMap<String, String> queryParams, Pageable pageable) {
        Patient patient = patientService.findPatientOrThrow(patientNumber);
        Page<PatientAllergiesData> page = allergiesService.fetchPatientAllergies(patient, pageable).map(p -> allergiesService.convertPatientAllergiesToData(p));
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    @GetMapping("/allergy-type")
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
