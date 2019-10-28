/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.service;

import io.smarthealth.clinical.lab.data.AnalyteData;
import io.smarthealth.clinical.lab.data.ContainerData;
import io.smarthealth.clinical.lab.data.DisciplineData;
import io.smarthealth.clinical.lab.data.LabTestData;
import io.smarthealth.clinical.lab.data.SpecimenData;
import io.smarthealth.clinical.lab.data.LabTestTypeData;
import io.smarthealth.clinical.lab.domain.Analyte;
import io.smarthealth.clinical.lab.domain.AnalyteRepository;
import io.smarthealth.clinical.lab.domain.Container;
import io.smarthealth.clinical.lab.domain.ContainerRepository;
import io.smarthealth.clinical.lab.domain.Discipline;
import io.smarthealth.clinical.lab.domain.DisciplineRepository;
import io.smarthealth.clinical.lab.domain.LabTest;
import io.smarthealth.clinical.lab.domain.Specimen;
import io.smarthealth.clinical.lab.domain.SpecimenRepository;
import io.smarthealth.clinical.lab.domain.TestTypeRepository;
import io.smarthealth.clinical.lab.domain.Testtype;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.clinical.lab.domain.LabTestRepository;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
public class LabService {

    @Autowired
    AnalyteRepository AnalyteRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ContainerRepository containerRepository;

    @Autowired
    DisciplineRepository disciplineRepository;

    @Autowired
    TestTypeRepository ttypeRepository;

    @Autowired
    LabTestRepository PtestsRepository;

    @Autowired
    VisitRepository visitRepository;

    @Autowired
    SpecimenRepository specimenRepository;

    @Transactional
    public Long createTestType(LabTestTypeData testtypeData) {
        try {
            System.out.println("Line 76");
            Testtype testtype = LabTestTypeData.map(testtypeData);
            System.out.println("Line 78");
            for (Long id : testtypeData.getSpecimenId()) {
                Optional<Specimen> specimen = specimenRepository.findById(id);
                if (specimen.isPresent()) {
                    specimen.get().getTestType().add(testtype);
                    testtype.getSpecimens().add(specimen.get());
                }
            }
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
    public String saveTestType(Testtype testtype) {
        try {
            ttypeRepository.save(testtype);
            return testtype.getServiceCode();
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error occured while creating testType ", e.getMessage());
        }
    }

    public Testtype fetchTestTypeById(Long testtypeId) {
        return ttypeRepository.findById(testtypeId).orElseThrow(() -> APIException.notFound("TestType identified by {0} not found", testtypeId));
    }

    public List<AnalyteData> saveAnalytes(ArrayList<Analyte> analyte) {
        List<AnalyteData> analyteArray = new ArrayList();
        List<Analyte> analytes = AnalyteRepository.saveAll(analyte);
        for (Analyte analyt : analytes) {
            analyteArray.add(convertToAnalyteData(analyt));
        }
        return analyteArray;
    }

    public Testtype fetchTestTypeByCode(String testTypeCode) {
        Testtype ttype = ttypeRepository.findByServiceCode(testTypeCode).orElseThrow(() -> APIException.notFound("TestType identified by code {0} not found", testTypeCode));
        return ttype;
    }

    public Page<Testtype> fetchAllTestTypes(Pageable pageable) {
        List<LabTestTypeData> testtypeDataList = new ArrayList<>();

        Page<Testtype> testtypes = ttypeRepository.findAll(pageable);
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

    public LabTestTypeData convertToTestTypeData(Testtype testtype) {
        LabTestTypeData testtypeData = modelMapper.map(testtype, LabTestTypeData.class);
        return testtypeData;
    }

    public AnalyteData convertToAnalyteData(Analyte analyte) {
        AnalyteData analyteData = modelMapper.map(analyte, AnalyteData.class);
        return analyteData;
    }

    /*
    a. Create a new Specimens
    b. Read all Specimens 
    c. Read Specimens by Id
    c. Update Specimens
     */
    @Transactional
    public List<SpecimenData> createSpecimens(List<SpecimenData> specimenData) {
        Type listType = new TypeToken<List<Specimen>>() {
        }.getType();
        List<Specimen> specs = new ArrayList();
        List<SpecimenData> specs2 = new ArrayList();
//        List<Specimen> specs = modelMapper.map(specimenData, new TypeToken<List<Specimen>>() {}.getType());
        for (SpecimenData specData : specimenData) {
            Specimen spec1 = SpecimenData.map(specData);
//            Optional<Container> container = containerRepository.findById(specData.getContainerId());
//            if (container.isPresent()) {
//                container.get().setSpecimen(spec1);
//                spec1.setContainer(container.get());
//            }
            specs.add(spec1);
        }
//        Optional<Testtype> ttype = ttypeRepository.findById(specimenId);
//        if (ttype.isPresent()) {
//            for (Specimen spec : specs) {
//                spec.setTestType(ttype.get());
//            }
//        }
        List<Specimen> specimens = specimenRepository.saveAll(specs);
        for (Specimen spec : specimens) {
            Optional<Container> container = containerRepository.findById(spec.getContainerId());
            if (container.isPresent()) {
                SpecimenData spec1 = SpecimenData.map(spec);
                spec1.setContainer(modelMapper.map(container.get(), ContainerData.class));
                specs2.add(spec1);
            }
        }

        return specs2;
    }

    public Page<SpecimenData> fetchAllSpecimens(Pageable pgbl) {
        return specimenRepository.findAll(pgbl).map(p -> convertSpecimenToData(p));
    }

    public SpecimenData fetchSpecimenById(Long id) {
        return specimenRepository.findById(id).map(p -> convertSpecimenToData(p)).orElseThrow(() -> APIException.notFound("Specimen identified by {0} not found.", id));
    }

    public void deleteById(Long id) {
        specimenRepository.deleteById(id);
    }

    public SpecimenData convertSpecimenToData(Specimen specimen) {
        SpecimenData specimenData = modelMapper.map(specimen, SpecimenData.class);
        Optional<Container> container = containerRepository.findById(specimen.getContainerId());
        if (container.isPresent())
            specimenData.setContainer(modelMapper.map(container.get(), ContainerData.class));
        return specimenData;
    }

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

    public void deleteDisciplineById(Long id) {
        disciplineRepository.deleteById(id);
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
    public LabTestData savePatientResults(LabTestData testResults) {
        Visit visit = visitRepository.findByVisitNumber(testResults.getVisitNumber())
                .orElseThrow(() -> APIException.notFound("Patient Session with Visit Number : {0} not found.", testResults.getVisitNumber()));

        if (!StringUtils.equalsIgnoreCase(visit.getPatient().getPatientNumber(), testResults.getPatientNumber())) {
            throw APIException.badRequest("Invalid Patient Number! mismatch in Patient Visit's patient number");
        }

        LabTest patienttestsEntity = convertDataToPatientTestsData(testResults);
        patienttestsEntity.setVisit(visit);
        patienttestsEntity.setPatient(visit.getPatient());
        LabTest patientTests = PtestsRepository.save(patienttestsEntity);
        return convertPatientTestToData(patientTests);
    }

    public Page<LabTestData> fetchAllPatientTests(String patientNumber, String visitNumber, String status, Pageable pgbl) {
        Page<LabTestData> ptests = PtestsRepository.findByPatientNumberAndVisitNumberAndStatus(visitNumber, patientNumber, status, pgbl).map(p -> convertPatientTestToData(p));
        return ptests;
    }

    public Optional<LabTestData> fetchPatientTestsById(Long id) {
        return PtestsRepository.findById(id).map(p -> convertPatientTestToData(p));
    }

    public void deletePatientTestsById(Long id) {
        PtestsRepository.deleteById(id);
    }

    public LabTestData convertPatientTestToData(LabTest patientTests) {
        LabTestData patientsdata = modelMapper.map(patientTests, LabTestData.class);
        return patientsdata;
    }

    public LabTest convertDataToPatientTestsData(LabTestData patientTestsData) {
        LabTest patienttests = modelMapper.map(patientTestsData, LabTest.class);
        return patienttests;
    }

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
        return modelMapper.map(containerList, new TypeToken<List<DisciplineData>>() {
        }.getType());
    }

    public Page<ContainerData> fetchAllContainers(Pageable pgbl) {
        return containerRepository.findAll(pgbl).map(p -> convertContainerToData(p));
    }

    public ContainerData fetchContainerById(Long id) {
        return containerRepository.findById(id).map(p -> convertContainerToData(p)).orElseThrow(() -> APIException.notFound("Container identified by {0} not found.", id));
    }

    public void deleteContainerById(Long id) {
        containerRepository.deleteById(id);
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
        return AnalyteRepository.save(analyte);
    }

    public Page<Analyte> fetchAllAnalytes(Pageable pgbl) {
        return AnalyteRepository.findAll(pgbl);
    }

    public Page<AnalyteData> fetchAnalyteByTestType(Testtype testtype, Pageable pgbl) {
        return AnalyteRepository.findByTestType(testtype, pgbl).map(p -> convertAnalyteToData(p));
    }

    public Analyte fetchAnalyteById(Long id) {
        return AnalyteRepository.findById(id).orElseThrow(() -> APIException.notFound("Analyte identified by {0} not found.", id));
    }

    public void deleteAnalyteById(Long id) {
        AnalyteRepository.deleteById(id);
    }

    public AnalyteData convertAnalyteToData(Analyte analyte) {
        AnalyteData analyteData = modelMapper.map(analyte, AnalyteData.class);
        return analyteData;
    }

}
