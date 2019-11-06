/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.service;

import io.smarthealth.clinical.lab.data.AnalyteData;
import io.smarthealth.clinical.lab.data.ContainerData;
import io.smarthealth.clinical.lab.data.DisciplineData;
import io.smarthealth.clinical.lab.data.SpecimenData;
import io.smarthealth.clinical.lab.data.LabTestTypeData;
import io.smarthealth.clinical.lab.data.PatientTestData;
import io.smarthealth.clinical.lab.domain.Analyte;
import io.smarthealth.clinical.lab.domain.AnalyteRepository;
import io.smarthealth.clinical.lab.domain.Container;
import io.smarthealth.clinical.lab.domain.ContainerRepository;
import io.smarthealth.clinical.lab.domain.Discipline;
import io.smarthealth.clinical.lab.domain.DisciplineRepository;
import io.smarthealth.clinical.lab.domain.Specimen;
import io.smarthealth.clinical.lab.domain.SpecimenRepository;
import io.smarthealth.clinical.lab.domain.LabTestType;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.clinical.lab.domain.LabTestRepository;
import io.smarthealth.clinical.lab.domain.LabTestTypeRepository;
import io.smarthealth.clinical.lab.domain.PatientLabTest;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.infrastructure.sequence.SequenceRepository;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
public class LabService {

    @Autowired
    private final AnalyteRepository analyteRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private final ContainerRepository containerRepository;

    @Autowired
    private final DisciplineRepository disciplineRepository;

    @Autowired
    private final LabTestTypeRepository ttypeRepository;

    @Autowired
    private final LabTestRepository PtestsRepository;

    @Autowired
    private final VisitRepository visitRepository;

    @Autowired
    private final SpecimenRepository specimenRepository;

    @Autowired
    private final DoctorsRequestRepository doctorRequestRepository;

    @Autowired
    private final SequenceRepository seqRepository;

    public LabService(AnalyteRepository analyteRepository, ContainerRepository containerRepository, DisciplineRepository disciplineRepository, LabTestTypeRepository ttypeRepository, LabTestRepository PtestsRepository, VisitRepository visitRepository, SpecimenRepository specimenRepository, DoctorsRequestRepository doctorRequestRepository, SequenceRepository seqRepository) {
        this.analyteRepository = analyteRepository;
        this.containerRepository = containerRepository;
        this.disciplineRepository = disciplineRepository;
        this.ttypeRepository = ttypeRepository;
        this.PtestsRepository = PtestsRepository;
        this.visitRepository = visitRepository;
        this.specimenRepository = specimenRepository;
        this.doctorRequestRepository = doctorRequestRepository;
        this.seqRepository = seqRepository;
    }

    @Transactional
    public Long createTestType(LabTestTypeData testtypeData) {
        try {
            System.out.println("Line 76");

            LabTestType testtype = LabTestTypeData.map(testtypeData);
            System.out.println("Line 78");
            testtypeData.getSpecimenId()
                    .stream()
                    .map((id) -> specimenRepository.findById(id))
                    .filter((specimen) -> (specimen.isPresent()))
                    .forEachOrdered((specimen) -> {
                        testtype.addSpecimen(specimen.get());
                    });
            Optional<Discipline> discipline = disciplineRepository.findById(testtypeData.getDisciplineId());
            if (discipline.isPresent()) {
                testtype.setDiscipline(discipline.get());
            }
            ttypeRepository.save(testtype);
            return testtype.getId();
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error occured while creating testType ", e.getMessage());
        }
    }

    @Transactional
    public Optional<LabTestTypeData> getById(Long id) {
        try {
            return ttypeRepository.findById(id).map(d -> convertToTestTypeData(d));
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("TestType identified by {0} not found ", e.getMessage());
        }
    }

    @Transactional
    public String saveTestType(LabTestType testtype) {
        try {
            ttypeRepository.save(testtype);
            return testtype.getServiceCode();
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error occured while creating testType ", e.getMessage());
        }
    }

    public Optional<LabTestType> fetchTestTypeById(Long testtypeId) {
        return ttypeRepository.findById(testtypeId);//.orElseThrow(() -> APIException.notFound("LabTestType identified by {0} not found", testtypeId));
    }

    public List<AnalyteData> saveAnalytes(List<AnalyteData> analyteData) {
        List<AnalyteData> analyteArray = new ArrayList();
        List<Analyte> analytes = new ArrayList();
        for (AnalyteData analytedata : analyteData) {
            Analyte analyte = convertDataToAnalyte(analytedata);
            Optional<LabTestType> ttype = ttypeRepository.findById(analytedata.getTestTypeId());

            if (ttype.isPresent()) {
                analyte.setTestType(ttype.get());
            }
            analytes.add(analyte);
        }

        List<Analyte> savedAnalytes = analyteRepository.saveAll(analytes);

        for (Analyte analyt : analytes) {
            analyteArray.add(convertAnalyteToData(analyt));
        }
        return analyteArray;

    }

    public LabTestType fetchTestTypeByCode(String testTypeCode) {
        LabTestType ttype = ttypeRepository.findByServiceCode(testTypeCode).orElseThrow(() -> APIException.notFound("TestType identified by code {0} not found", testTypeCode));
        return ttype;
    }

    public Page<LabTestType> fetchAllTestTypes(Pageable pageable) {
        List<LabTestTypeData> testtypeDataList = new ArrayList<>();

        Page<LabTestType> testtypes = ttypeRepository.findAll(pageable);
        return testtypes;
    }

    public boolean deleteTest(LabTestTypeData testtype) {
        try {
            ttypeRepository.deleteById(testtype.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error deleting TestType id " + testtype.getId(), e.getMessage());
        }
    }

    public LabTestTypeData convertToTestTypeData(LabTestType testtype) {
        LabTestTypeData testtypeData = modelMapper.map(testtype, LabTestTypeData.class);
        return testtypeData;
    }

    public AnalyteData convertAnalyteToData(Analyte analyte) {
        AnalyteData analyteData = modelMapper.map(analyte, AnalyteData.class);
        return analyteData;
    }

    public Analyte convertDataToAnalyte(AnalyteData analyteData) {
        Analyte analyte = modelMapper.map(analyteData, Analyte.class);
        return analyte;
    }

    /*
    a. Create a new Specimens
    b. Read all Specimens 
    c. Read Specimens by Id
    c. Update Specimens
     */
    @Transactional
    public List<SpecimenData> createSpecimens(List<SpecimenData> specimenDatalist) {

        List<Specimen> specs = new ArrayList();
        for (SpecimenData specData : specimenDatalist) {
            Specimen specimen = SpecimenData.map(specData);
            Optional<Container> cont = containerRepository.findById(specData.getContainerId());

            if (cont.isPresent()) {
                specimen.setContainer(cont.get());
            }
            specs.add(specimen);
        }

        List<Specimen> saved = specimenRepository.saveAll(specs);

        List<SpecimenData> savedlist = new ArrayList<>();
        saved.forEach((s) -> {
            savedlist.add(SpecimenData.map(s));
        });
        return savedlist;
    }

    public Page<SpecimenData> fetchAllSpecimens(Pageable pgbl) {
        return specimenRepository.findAll(pgbl).map(p -> SpecimenData.map(p));
    }

    public SpecimenData fetchSpecimenById(Long id) {
        return specimenRepository.findById(id).map(p -> SpecimenData.map(p)).orElseThrow(() -> APIException.notFound("Specimen identified by {0} not found.", id));
    }

    public void deleteTestById(Long id) {
        ttypeRepository.deleteById(id);
    }

    public void deleteContainerById(Long id) {
        containerRepository.deleteById(id);
    }

    public void deleteDisciplineById(Long id) {
        disciplineRepository.deleteById(id);
    }

//    public SpecimenData convertSpecimenToData(Specimen specimen) {
//        SpecimenData specimenData = modelMapper.map(specimen, SpecimenData.class);
//        Optional<Container> container = containerRepository.findById(specimen.getContainerId());
//        if (container.isPresent()) {
//            specimenData.setContainer(modelMapper.map(container.get(), ContainerData.class));
//        }
//        return specimenData;
//    }
    public Specimen convertDataToSpecimen(SpecimenData specimenData) {
        Specimen specimen = modelMapper.map(specimenData, Specimen.class);

        return specimen;
    }

    /*
    a. Create a new department
    b. Read all departments 
    c. Read department by Id
    c. Update department
     */
    @Transactional
    public List<DisciplineData> createDisciplines(List<DisciplineData> disciplineData) {
        Type listType = new TypeToken<List<Discipline>>() {
        }.getType();
        List<Discipline> disciplines = modelMapper.map(disciplineData, listType);

        List<Discipline> disciplineList = disciplineRepository.saveAll(disciplines);
        return modelMapper.map(disciplineList, new TypeToken<List<DisciplineData>>() {
        }.getType());
    }

    public Page<DisciplineData> fetchAllDisciplines(Pageable pgbl) {
        return disciplineRepository.findAll(pgbl).map(p -> convertDisciplineToData(p));
    }

    public DisciplineData fetchDisciplineById(Long id) {
        return disciplineRepository.findById(id).map(p -> convertDisciplineToData(p)).orElseThrow(() -> APIException.notFound("Discipline identified by {0} not found.", id));
    }

    public DisciplineData convertDisciplineToData(Discipline discipline) {
        DisciplineData disciplineData = modelMapper.map(discipline, DisciplineData.class);
        return disciplineData;
    }

    public Discipline convertDataToSpecimen(DisciplineData disciplineData) {
        Discipline discipline = modelMapper.map(disciplineData, Discipline.class);
        return discipline;
    }

    /*
    a. Create a new PatientResults
    b. Read all PatientResults 
    c. Read PatientResults by Id
    c. Update PatientResults
     */
    @Transactional
    public PatientTestData savePatientResults(PatientTestData patienttestdata) {
        String labNumber = String.valueOf(seqRepository.nextSequence("LabTestNumber", "1"));
        Visit visit = visitRepository.findByVisitNumber(patienttestdata.getVisitNumber())
                .orElseThrow(() -> APIException.notFound("Patient Session with Visit Number : {0} not found.", patienttestdata.getVisitNumber()));
        PatientLabTest patienttestsEntity = PatientTestData.map(patienttestdata);

        Optional<DoctorRequest> docReq = doctorRequestRepository.findById(patienttestdata.getRequestId());

        if (docReq.isPresent()) {
            
        }        
        Optional<LabTestType> ttype = ttypeRepository.findByServiceCode(patienttestdata.getTestCode());

        if (ttype.isPresent()) {
            patienttestsEntity.setTesttype(ttype.get());
        }

        patienttestsEntity.setLabTestNumber(labNumber);
        patienttestsEntity.setVisit(visit);
        patienttestsEntity.setPatient(visit.getPatient());
        PatientLabTest patientTests = PtestsRepository.save(patienttestsEntity);
        return PatientTestData.map(patientTests);
    }

    public Page<PatientTestData> fetchAllPatientTests(String patientNumber, String visitNumber, String status, Pageable pgbl) {
        Page<PatientTestData> ptests = PtestsRepository.findByPatientNumberAndVisitNumberAndStatus(visitNumber, patientNumber, status, pgbl).map(p -> PatientTestData.map(p));
        return ptests;
    }

    public Optional<PatientTestData> fetchPatientTestsById(Long id) {
        return PtestsRepository.findById(id).map(p -> PatientTestData.map(p));
    }

    public void deletePatientTestsById(Long id) {
        PtestsRepository.deleteById(id);
    }

//    public PatientTestData convertPatientTestToData(LabTest patientTests) {
//        PatientTestData patientsdata = modelMapper.map(patientTests, PatientTestData.class);
//        return patientsdata;
//    }
//
//    public LabTest convertDataToPatientTestsData(PatientTestData patientTestsData) {
//        LabTest patienttests = modelMapper.map(patientTestsData, LabTest.class);
//        return patienttests;
//    }

    /*
    a. Create a new Containers
    b. Read all Containers 
    c. Read Containers by Id
    c. Update Containers
     */
    @Transactional
    public List<ContainerData> createContainers(List<ContainerData> containerData) {
        Type listType = new TypeToken<List<Container>>() {
        }.getType();
        List<Container> containers = modelMapper.map(containerData, listType);

        List<Container> containerList = containerRepository.saveAll(containers);
        return modelMapper.map(containerList, new TypeToken<List<ContainerData>>() {
        }.getType());
    }

    public Page<ContainerData> fetchAllContainers(Pageable pgbl) {
        return containerRepository.findAll(pgbl).map(p -> convertContainerToData(p));
    }

    public ContainerData fetchContainerById(Long id) {
        return containerRepository.findById(id).map(p -> convertContainerToData(p)).orElseThrow(() -> APIException.notFound("Container identified by {0} not found.", id));
    }

    public ContainerData convertContainerToData(Container container) {
        ContainerData containerData = modelMapper.map(container, ContainerData.class);
        return containerData;
    }

    public Container convertDataToContainer(ContainerData containerData) {
        Container container = modelMapper.map(containerData, Container.class);
        return container;
    }

    @Transactional
    public Analyte createAnalyte(Analyte analyte) {
        return analyteRepository.save(analyte);
    }

    public Page<Analyte> fetchAllAnalytes(Pageable pgbl) {
        return analyteRepository.findAll(pgbl);
    }

    public Page<AnalyteData> fetchAnalyteByTestType(LabTestType testtype, Pageable pgbl) {
        return analyteRepository.findByTestType(testtype, pgbl).map(p -> convertAnalyteToData(p));
    }

    public Analyte fetchAnalyteById(Long id) {
        return analyteRepository.findById(id).orElseThrow(() -> APIException.notFound("Analyte identified by {0} not found.", id));
    }

    public void deleteAnalyteById(Long id) {
        analyteRepository.deleteById(id);
    }

}
