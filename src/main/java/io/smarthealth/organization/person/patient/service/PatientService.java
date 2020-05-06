/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.service;

import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.domain.PatientQueueRepository;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.organization.person.data.AddressData;
import io.smarthealth.organization.person.data.ContactData;
import io.smarthealth.organization.person.domain.*;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.data.PatientIdentifierData;
import io.smarthealth.organization.person.patient.data.enums.PatientStatus;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientIdentifier;
import io.smarthealth.organization.person.patient.domain.PatientIdentifierRepository;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import io.smarthealth.organization.person.patient.domain.specification.PatientSpecification;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PersonContactRepository personContactRepository;
    private final PersonAddressRepository personAddressRepository;
    private final PatientIdentifierRepository patientIdentifierRepository;
    private final PatientQueueRepository patientQueueRepository;
    private final VisitRepository visitRepository;

    private final ServicePointService servicePointService;
    private final FacilityService facilityService;
//    private final VisitService visitService;

    private final SequenceNumberService sequenceNumberService;

    private final ModelMapper modelMapper;

    private final PortraitRepository portraitRepository;

    private final PatientIdentifierService patientIdentifierService;

    private File patientImageDirRoot;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${patientimage.upload.dir}")
    private String uploadDir;

    public Page<Patient> fetchAllPatients(MultiValueMap<String, String> queryParams, final Pageable pageable) {
        Specification<Patient> spec = PatientSpecification.createSpecification(queryParams.getFirst("term"));
        return patientRepository.findAll(spec, pageable);
        // return patientRepository.findAll(pageable);
    }

    public Page<Patient> search(String keyword, Pageable page) {
        Specification<Patient> spec = PatientSpecification.createSpecification(keyword);
        return patientRepository.findAll(spec, page);
    }

    public Patient fetchPatientByIdentityNumber(Long patientId) {
        return patientRepository.getOne(patientId);
    }

    public Patient fetchPatientByPersonId(Long id) {
        return patientRepository.findById(id).orElseThrow(() -> APIException.notFound("Person identified by id {0} no found", id));
    }

//    public String generatePatientNumber() {
//        int nextPatient = patientRepository.maxId() + 1;
//        return String.valueOf("PAT" + nextPatient);
//    }
    public Optional<PatientData> fetchPatientByPatientNumber(final String patientNumber) {

        return patientRepository.findByPatientNumber(patientNumber)
                .map(patientEntity -> {
                    final PatientData patient = modelMapper.map(patientEntity, PatientData.class);
                    //fetch patient addresses
                    final List<PersonAddress> personAddressEntity = personAddressRepository.findByPerson(patientEntity);
                    if (personAddressEntity != null) {
                        patient.setAddress(personAddressEntity
                                .stream()
                                .map(AddressData::map)
                                .collect(Collectors.toList())
                        );
                    }

                    final List<PersonContact> contactDetailEntities = this.personContactRepository.findByPerson(patientEntity);
                    if (contactDetailEntities != null) {
                        patient.setContact(contactDetailEntities
                                .stream()
                                .map(ContactData::map)
                                .collect(Collectors.toList())
                        );
                    }

                    final List<PatientIdentifier> patientIdentifiers = this.patientIdentifierService.fetchPatientIdentifiers(patientEntity);

                    if (patientIdentifiers != null && !patientIdentifiers.isEmpty()) {
                        List<PatientIdentifierData> ids = new ArrayList<>();
                        for (PatientIdentifier id : patientIdentifiers) {
                            ids.add(patientIdentifierService.convertIdentifierEntityToData(id));
                        }
                        patient.setIdentifiers(ids);
                    }

                    patient.setFullName((patient.getGivenName() != null ? patient.getGivenName() : "") + " " + (patient.getMiddleName() != null ? patient.getMiddleName() : "").concat(" ").concat(patient.getSurname() != null ? patient.getSurname() : " "));
                    return patient;
                });
    }

    public String deletePortrait(String patientNumber) throws IOException {
        final Person person = findPatientOrThrow(patientNumber);
        Portrait portrait = portraitRepository.findByPerson(person);
        patientImageDirRoot = new File(uploadDir);
        System.out.println("-------------Deleting patient portrait--------------");
        File file = new File(patientImageDirRoot, portrait.getImageName());
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File deleted successfully");
            } else {
                System.out.println("Fail to delete file");
            }
        }
        this.portraitRepository.delete(portrait);
        //delete from directory

        return patientNumber;
    }

    @Transactional
    public Portrait createPortrait(Person person, MultipartFile file) throws IOException {
        if (file == null) {
            return null;
        }

        File fileForPatient = patientFileOnFolder(file);

        try (
                InputStream in = file.getInputStream();
                OutputStream out = new FileOutputStream(fileForPatient)) {
            FileCopyUtils.copy(in, out);
            Portrait portrait = new Portrait();
            portrait.setContentType(file.getContentType());
            portrait.setImage(file.getBytes());
            portrait.setSize(file.getSize());
            portrait.setPerson(person);
            portrait.setImageUrl(uploadDir);
            portrait.setImageName(file.getOriginalFilename());
            portraitRepository.save(portrait);
            return portrait;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Transactional
    public Patient createPatient(final PatientData patient) {
        String patientNo = sequenceNumberService.next(1L, Sequences.Patient.name());
        throwifDuplicatePatientNumber(patientNo);

        // use strict to prevent over eager matching (happens with ID fields)
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        final Patient patientEntity = modelMapper.map(patient, Patient.class);
        patientEntity.setAlive(true);
        patientEntity.setPatient(true);
        patientEntity.setStatus(PatientStatus.Active);
        patientEntity.setPatientNumber(patientNo);

        final Patient savedPatient = this.patientRepository.save(patientEntity);
        //save patients contact details
        if (patient.getContact() != null) {
            List<PersonContact> personContacts = new ArrayList<>();
            personContactRepository.saveAll(patient.getContact()
                    .stream()
                    .map(contact -> {
                        final PersonContact contactDetailEntity = ContactData.map(contact);
                        personContacts.add(contactDetailEntity);
                        contactDetailEntity.setPerson(savedPatient);
                        return contactDetailEntity;
                    })
                    .collect(Collectors.toList())
            );
            savedPatient.setContacts(personContacts);
        }
        //save patient address details
        if (patient.getAddress() != null) {
            List<PersonAddress> addresses = new ArrayList<>();
            personAddressRepository.saveAll(patient.getAddress()
                    .stream()
                    .map(address -> {
                        final PersonAddress addressDetailEntity = AddressData.map(address);
                        addresses.add(addressDetailEntity);
                        addressDetailEntity.setPerson(savedPatient);
                        return addressDetailEntity;
                    })
                    .collect(Collectors.toList())
            );
            savedPatient.setAddresses(addresses);
        }
        //save patient identifier
        if (patient.getIdentifiers() != null) {
            // List<PatientIdentifier> patientIdentifiersList = new ArrayList<>();
            List<PatientIdentifier> values = patient.getIdentifiers()
                    .stream()
                    .map(identity -> {
                        if (identity.getId_type().equals("")) {
                            return null;
                        }
                        if (identity.getId_type().equals("-Select-")) {
                            return null;
                        }
                        final PatientIdentifier patientIdentifier = patientIdentifierService.convertIdentifierDataToEntity(identity) /*modelMapper.map(identity, PatientIdentifier.class)*/;
                        //patientIdentifiersList.add(patientIdentifier);
                        patientIdentifier.setPatient(savedPatient);
                        return patientIdentifier;
                    }).filter(Objects::nonNull)
                    .collect(Collectors.toList());
            patientIdentifierRepository.saveAll(values);
            /*
            if (!patientIdentifiersList.isEmpty()) {
                savedPatient.setIdentifications(patientIdentifiersList);
            }*/

        }

        Facility facilityLogged = facilityService.loggedFacility();
        if (patient.getVisitType() != null) {
            String visitid = sequenceNumberService.next(1L, Sequences.Visit.name());
            Visit visit = new Visit();
            visit.setVisitNumber(visitid);
            visit.setPatient(savedPatient);
            visit.setScheduled(Boolean.TRUE);
            visit.setStartDatetime(LocalDateTime.now());
            visit.setStatus(VisitEnum.Status.CheckIn);
            visit.setVisitType(VisitEnum.VisitType.Outpatient);
            //generate visit number
//            visit.setVisitNumber(sequenceService.nextNumber(SequenceType.VisitNumber));

            ServicePoint servicePoint = null;
            if (patient.getVisitType().equals("OPD_VISIT")) {
                //find service point by service type
                servicePoint = servicePointService.getServicePointByType(ServicePointType.Triage);
                //send to triage
                visit.setServicePoint(servicePoint);
            } else if (patient.getVisitType().equals("EMERGENCY_VISIT")) {

            } else if (patient.getVisitType().equals("PHARMACY_VISIT")) {
                //send to pharmacy
                servicePoint = servicePointService.getServicePointByType(ServicePointType.Pharmacy);

                visit.setServicePoint(servicePoint);
            } else if (patient.getVisitType().equals("LABORATORY_VISIT")) {
                //send to laboratory
                servicePoint = servicePointService.getServicePointByType(ServicePointType.Laboratory);
                visit.setServicePoint(servicePoint);
            } else if (patient.getVisitType().equals("IPD_VISIT")) {
                //send to inpatient

            }
            visitRepository.save(visit);
//            visitService.createAVisit(visit);

            //insert into patient visit log
            PatientQueue queue = new PatientQueue();
            queue.setPatient(savedPatient);
            queue.setServicePoint(servicePoint);
            queue.setSpecialNotes(patient.getVisitType());
            queue.setStatus(true);
            queue.setUrgency(PatientQueue.QueueUrgency.Normal);
            queue.setVisit(visit);

            patientQueueRepository.save(queue);
//            patientQueueService.createPatientQueue(queue);
        }

        return savedPatient;
    }

    @Transactional
    public Patient updatePatient(String patientNumber, Patient patient) {
        try {
            return patientRepository.save(patient);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RestClientException("Error updating patient number" + patientNumber);
        }
    }

    private File patientFileOnFolder(MultipartFile f) {
        return new File(this.patientImageDirRoot, f.getOriginalFilename());
    }

    public Patient findPatientOrThrow(String patientNumber) {
        return this.patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> APIException.notFound("Patient Number {0} not found.", patientNumber));
    }
    
     public Optional<Patient> findByPatientNumber(String patientNumber) {
        return this.patientRepository.findByPatientNumber(patientNumber);
    }

    public void throwifDuplicatePatientNumber(String patientNumber) {
        if (patientRepository.existsByPatientNumber(patientNumber)) {
            throw APIException.conflict("Duplicate Patient Number {0}", patientNumber);
        }
    }

    public boolean patientExists(String patientNumber) {
        return patientRepository.existsByPatientNumber(patientNumber);
    }

    public PatientData convertToPatientData(Patient patient) {
        try {
            PatientData patientData = modelMapper.map(patient, PatientData.class);
            if (patient.getAddresses() != null) {
                List<AddressData> addresses = new ArrayList<>();

                patient.getAddresses().forEach((address) -> {
                    AddressData addressData = modelMapper.map(address, AddressData.class);
                    addresses.add(addressData);
                });
                patientData.setAddress(addresses);
            }
            if (patient.getContacts() != null) {
                List<ContactData> contacts = new ArrayList<>();
                patient.getContacts().forEach((contact) -> {
                    ContactData contactData = modelMapper.map(contact, ContactData.class);
                    contacts.add(contactData);
                });
                patientData.setContact(contacts);
            }

            final List<PatientIdentifier> patientIdentifiers = this.patientIdentifierService.fetchPatientIdentifiers(patient);

            if (patientIdentifiers != null && !patientIdentifiers.isEmpty()) {
                List<PatientIdentifierData> ids = new ArrayList<>();
                for (PatientIdentifier id : patientIdentifiers) {
                    ids.add(patientIdentifierService.convertIdentifierEntityToData(id));
                }
                patientData.setIdentifiers(ids);
            }

            patientData.setFullName((patient.getGivenName() != null ? patient.getGivenName() : "") + " " + (patient.getMiddleName() != null ? patient.getMiddleName() : "").concat(" ").concat(patient.getSurname() != null ? patient.getSurname() : " "));
            if (patient.getDateOfBirth() != null) {
                //patientData.setAge(String.valueOf(ChronoUnit.YEARS.between(patient.getDateOfBirth(), LocalDate.now())));
                patientData.setAge(patient.getAge());
            }
            return patientData;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("An error occured while converting patient data ", e.getMessage());
        }
    }

    public void exportPatientPdfFile(HttpServletResponse response) throws SQLException, JRException, IOException {
        Connection conn = jdbcTemplate.getDataSource().getConnection();

        InputStream path = resourceLoader.getResource("classpath:reports/patient/PatientList.jrxml").getInputStream();

        JasperReport jasperReport = JasperCompileManager.compileReport(path);

        // Parameters for report
        Map<String, Object> parameters = new HashMap<String, Object>();

//        JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);
//
//        return print;
    }
}
