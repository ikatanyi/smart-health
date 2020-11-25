/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.service;

import io.smarthealth.administration.config.domain.GlobalConfiguration;
import io.smarthealth.administration.config.domain.GlobalConfigurationRepository;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.domain.PatientQueueRepository;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.organization.person.data.AddressDatas;
import io.smarthealth.organization.person.data.ContactData;
import io.smarthealth.organization.person.data.PersonNextOfKinData;
import io.smarthealth.organization.person.data.PortraitData;
import io.smarthealth.organization.person.domain.*;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.data.PersonIdentifierData;
import io.smarthealth.organization.person.patient.data.enums.PatientStatus;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientIdentifier;
import io.smarthealth.organization.person.patient.domain.PatientIdentifierRepository;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import io.smarthealth.organization.person.patient.domain.specification.PatientSpecification;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.io.*;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
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
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final PersonNextOfKinRepository personNextOfKinRepository;

    private final ServicePointService servicePointService;
    private final FacilityService facilityService;
//    private final VisitService visitService;

    private final SequenceNumberService sequenceNumberService;

    private final ModelMapper modelMapper;

    private final PortraitRepository portraitRepository;

    private final PatientIdentifierService patientIdentifierService;

    private File patientImageDirRoot;

    private final GlobalConfigurationRepository globalConfigurationRepository;

    @Value("${upload.image.max-size:524288}")
    Long maxSize;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    public Page<Patient> fetchAllPatients(final String term, final String dateRange, final Pageable pageable) {
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Specification<Patient> spec = PatientSpecification.createSpecification(range, term);
        return patientRepository.findAll(spec, pageable);
    }

    public Page<Patient> search(String keyword, Pageable page) {
        DateRange range = null;
        Specification<Patient> spec = PatientSpecification.createSpecification(null, keyword);
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
                                .map(AddressDatas::map)
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

//                    if (patientIdentifiers != null && !patientIdentifiers.isEmpty()) {
//                        List<PersonIdentifierData> ids = new ArrayList<>();
//                        for (PatientIdentifier id : patientIdentifiers) {
//                            ids.add(patientIdentifierService.convertIdentifierEntityToData(id));
//                        }
//                        patient.setIdentifiers(ids);
//                    }
                    patient.setFullName((patient.getGivenName() != null ? patient.getGivenName() : "") + " " + (patient.getMiddleName() != null ? patient.getMiddleName() : "").concat(" ").concat(patient.getSurname() != null ? patient.getSurname() : " "));
                    return patient;
                });
    }

    public void deletePortrait(String patientNumber) throws IOException {
        final Person person = findPatientOrThrow(patientNumber);
        Optional<Portrait> portrait = portraitRepository.findByPerson(person);
        if (portrait.isPresent()) {
            GlobalConfiguration config = globalConfigurationRepository.findByName("PatientPortrait").orElseThrow(() -> APIException.notFound("Patient files folder {0} not set", "PatientPortrait"));
            patientImageDirRoot = new File(config.getValue());
            System.out.println("-------------Deleting patient portrait--------------");
            File file = new File(patientImageDirRoot, portrait.get().getImageName());
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("File deleted successfully");
                } else {
                    System.out.println("Fail to delete file");
                }
            }
            this.portraitRepository.delete(portrait.get());
            //delete from directory
        }
    }

    @Transactional
    public Portrait createPortrait(Patient patient, MultipartFile file) throws IOException {
        if (file == null) {
            throw APIException.badRequest("Image not found");
        }

        this.throwIfInvalidSize(file.getSize());
        this.throwIfInvalidContentType(file.getContentType());

        File fileForPatient = patientFileOnFolder(file);
        GlobalConfiguration config = globalConfigurationRepository.findByName("PatientPortrait").orElseThrow(() -> APIException.notFound("Patient files folder {0} not set", "PatientPortrait"));
        try (
                InputStream in = file.getInputStream();
                OutputStream out = new FileOutputStream(fileForPatient)) {
            Portrait portrait = new Portrait();
            portrait.setContentType(file.getContentType());
            //portrait.setImage(file.getBytes());
            portrait.setSize(file.getSize());
            portrait.setPerson(patient);
            portrait.setImageUrl(config.getValue());
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            String imageName = patient.getPatientNumber().concat(".").concat(extension);
            portrait.setImageName(imageName);
            //delete if any existing
            deletePortrait(patient.getPatientNumber());
            File fileToSave = new File(config.getValue().concat(imageName));
            Files.copy(in, fileToSave.toPath());

            portrait = portraitRepository.save(portrait);
            return portrait;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Transactional
    public Patient createPatient(final PatientData patient, MultipartFile file) {
        if (patient.getDateOfBirth() == null) {
            try {
                LocalDate dateOfBirth = LocalDate.now().minusYears(Long.valueOf(patient.getAge()));
                patient.setDateOfBirth(dateOfBirth);
            } catch (Exception e) {
                patient.setDateOfBirth(LocalDate.now());
            }
        }

        String patientNo = patient.getPatientNumber();
        if (patient.getPatientNumber() == null) {
            patientNo = sequenceNumberService.next(1L, Sequences.Patient.name());
            throwifDuplicatePatientNumber(patientNo);
        }

        // use strict to prevent over eager matching (happens with ID fields)
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        final Patient patientEntity = modelMapper.map(patient, Patient.class);
        //patientEntity.setIsAlive(true);
        patientEntity.setPatient(true);
        patientEntity.setStatus(PatientStatus.Active);
        patientEntity.setPatientNumber(patientNo);

        final Patient savedPatient = this.patientRepository.save(patientEntity);
        if (file != null) {
            try {
                createPortrait(savedPatient, file);
            } catch (IOException ex) {
//                Logger.getLogger(PatientService.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }

        //save patients contact details
        if (patient.getContact() != null) {
            List<PersonContact> personContacts = new ArrayList<>();
            personContactRepository.saveAll(patient.getContact()
                    .stream()
                    .map(contact -> {
                        final PersonContact contactDetailEntity = ContactData.map(contact);
                        contactDetailEntity.setPerson(savedPatient);
                        personContacts.add(contactDetailEntity);

                        return contactDetailEntity;
                    })
                    .collect(Collectors.toList())
            );
            savedPatient.setContacts(personContacts);
        }
        if (patient.getNok() != null) {
            List<PersonNextOfKin> nextOfKin = new ArrayList<>();
            personNextOfKinRepository.saveAll(patient.getNok()
                    .stream()
                    .map(nk -> {
                        if (nk.getName().equals("")) {
                            return null;
                        }
                        final PersonNextOfKin nok = PersonNextOfKinData.map(nk);
                        nok.setPerson(savedPatient);
                        nextOfKin.add(nok);
                        return nok;
                    })
                    .collect(Collectors.toList())
            );
            savedPatient.setNok(nextOfKin);
        }
        //save patient address details
        if (patient.getAddress() != null) {
            List<PersonAddress> addresses = new ArrayList<>();
            personAddressRepository.saveAll(patient.getAddress()
                    .stream()
                    .map(address -> {
                        final PersonAddress addressDetailEntity = AddressDatas.map(address);
                        addresses.add(addressDetailEntity);
                        addressDetailEntity.setPerson(savedPatient);
                        return addressDetailEntity;
                    })
                    .collect(Collectors.toList())
            );
            savedPatient.setAddresses(addresses);
        }
//        //save patient identifier
//        if (patient.getIdentifiers() != null) {
//            // List<PatientIdentifier> patientIdentifiersList = new ArrayList<>();
//            List<PatientIdentifier> values = patient.getIdentifiers()
//                    .stream()
//                    .map(identity -> {
//                        if (identity.getIdType().equals("")) {
//                            return null;
//                        }
//                        if (identity.getIdType().equals("-Select-")) {
//                            return null;
//                        }
//                        final PatientIdentifier patientIdentifier = patientIdentifierService.convertIdentifierDataToEntity(identity) /*modelMapper.map(identity, PatientIdentifier.class)*/;
//                        //patientIdentifiersList.add(patientIdentifier);
//                        patientIdentifier.setPatient(savedPatient);
//                        return patientIdentifier;
//                    }).filter(Objects::nonNull)
//                    .collect(Collectors.toList());
//            patientIdentifierRepository.saveAll(values);
//            /*
//            if (!patientIdentifiersList.isEmpty()) {
//                savedPatient.setIdentifications(patientIdentifiersList);
//            }*/
//
//        }
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

    @Transactional
    public PersonNextOfKin createNextOfKin(PersonNextOfKin nok) {
        return personNextOfKinRepository.save(nok);
    }

    public PersonNextOfKin findOrThrowNextOfKinById(Long id) {
        return personNextOfKinRepository.findById(id).orElseThrow(() -> APIException.notFound("Next of kin identified by {0} not found ", id));
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
            patientData.setInpatientNumber(patient.getInpatientNumber());
            if (patient.getAddresses() != null) {
                List<AddressDatas> addresses = new ArrayList<>();

                patient.getAddresses().forEach((address) -> {
                    AddressDatas addressData = modelMapper.map(address, AddressDatas.class);
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
            if (patient.getNok() != null) {
                List<PersonNextOfKinData> nokData = new ArrayList<>();
                patient.getNok().forEach((nok) -> {
                    PersonNextOfKinData contactData = PersonNextOfKinData.map(nok);
                    nokData.add(contactData);
                });
                patientData.setNok(nokData);
            }

            final List<PatientIdentifier> patientIdentifiers = this.patientIdentifierService.fetchPatientIdentifiers(patient);

//            if (patientIdentifiers != null && !patientIdentifiers.isEmpty()) {
//                List<PersonIdentifierData> ids = new ArrayList<>();
//                for (PatientIdentifier id : patientIdentifiers) {
//                    ids.add(patientIdentifierService.convertIdentifierEntityToData(id));
//                }
//                patientData.setIdentifiers(ids);
//            }
            //fetch portrait
            Optional<Portrait> portrait = portraitRepository.findByPerson(patient);
            if (portrait.isPresent()) {
                try {
                    PortraitData data = new PortraitData();
                    data.setImageName(portrait.get().getImageName());
                    data.setImageUrl(portrait.get().getImageUrl());
                    patientData.setPortraitData(data);

                    File imgFile = new File(portrait.get().getImageUrl().concat(portrait.get().getImageName()));
                    byte[] bytes = Files.readAllBytes(imgFile.toPath());

                    data.setImage(bytes);
                } catch (Exception e) {
                    System.out.println("Error loading " + e.getMessage());
                }
            }

            patientData.setFullName((patient.getGivenName() != null ? patient.getGivenName() : "") + " " + (patient.getMiddleName() != null ? patient.getMiddleName() : "").concat(" ").concat(patient.getSurname() != null ? patient.getSurname() : " "));
//            patientData.setInpatientNumber(patient.getInpatientNumber());
            if (patient.getDateOfBirth() != null) {
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

    public List<Patient> search(String term, int offset, int limit) {
        return patientRepository.search(term, limit, offset);
    }

    public Patient savePatient(Patient p) {
        return patientRepository.save(p);
    }
}
