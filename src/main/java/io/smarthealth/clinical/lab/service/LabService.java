package io.smarthealth.clinical.lab.service;

import io.smarthealth.clinical.lab.data.AnalyteData;
import io.smarthealth.clinical.lab.data.ContainerData;
import io.smarthealth.clinical.lab.data.LabTestTypeData;
import io.smarthealth.clinical.lab.data.PatientTestRegisterData;
import io.smarthealth.clinical.lab.data.SpecimenData;
import io.smarthealth.clinical.lab.domain.Analyte;
import io.smarthealth.clinical.lab.domain.AnalyteRepository;
import io.smarthealth.clinical.lab.domain.Container;
import io.smarthealth.clinical.lab.domain.Discipline;
import io.smarthealth.clinical.lab.domain.DisciplineRepository;
import io.smarthealth.clinical.lab.domain.LabTestType;
import io.smarthealth.clinical.lab.domain.PatientLabTestSpecimen;
import io.smarthealth.clinical.lab.domain.Specimen;
import io.smarthealth.clinical.lab.domain.SpecimenRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.clinical.lab.domain.LabTestRepository;
import io.smarthealth.clinical.lab.domain.LabTestTypeRepository;
import io.smarthealth.clinical.lab.domain.PatientLabTest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.lab.domain.PatientLabTestSpecimenRepo;
import io.smarthealth.clinical.lab.domain.LabRegister;
import io.smarthealth.clinical.lab.domain.Results;
import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.sequence.UuidGenerator;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;
import io.smarthealth.clinical.lab.domain.LabRegisterRepository;
import io.smarthealth.clinical.lab.domain.specification.PatientTestSpecification;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class LabService {

    private final AnalyteRepository analyteRepository;

    private final ModelMapper modelMapper;

    private final DisciplineRepository disciplineRepository;

    private final LabTestTypeRepository ttypeRepository;

    private final LabTestRepository PtestsRepository;

    private final SpecimenRepository specimenRepository;

    private final DoctorsRequestRepository doctorRequestRepository;

    private final LabRegisterRepository patientRegRepository;

//    private final SequenceNumberGenerator seqService;
    private final EmployeeService employeeService;

//    private final PatientService patientservice;
    private final ItemService itemService;
    
    private final VisitService visitService;

    private final LabTestTypeRepository labTestTypeRepository;

    private final PatientLabTestSpecimenRepo patientLabTestSpecimenRepo;

    private final SequenceNumberService sequenceNumberService;

    @Transactional
    public Long createTestType(LabTestTypeData testtypeData) {
        try {
            //find item by code
            Item item = itemService.findItemWithNoFoundDetection(testtypeData.getCode());

            LabTestType testtype = LabTestTypeData.map(testtypeData);
            testtype.setItemService(item);
            List<Specimen> specimens = new ArrayList<>();
            testtypeData.getSpecimenId().stream().map((sId) -> specimenRepository.findById(sId).get()).forEachOrdered((s) -> {
                specimens.add(s);
            });
            testtype.setSpecimen(specimens);

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

    public LabTestType findTestById(Long id) {
        return ttypeRepository.findById(id).orElseThrow(() -> APIException.notFound("Test identified by id {0} not found", id)
        );
    }

    public Optional<LabTestType> fetchTestTypeById(Long testtypeId) {
        return ttypeRepository.findById(testtypeId);//.orElseThrow(() -> APIException.notFound("LabTestType identified by {0} not found", testtypeId));
    }

    public List<AnalyteData> saveAnalytes(List<AnalyteData> analyteData, LabTestType labTest) {
        List<AnalyteData> analyteArray = new ArrayList();
        List<Analyte> analytes = new ArrayList();
        for (AnalyteData analytedata : analyteData) {
            Analyte analyte = convertDataToAnalyte(analytedata);
            analyte.setTestType(labTest);
            analytes.add(analyte);
        }

        List<Analyte> savedAnalytes = analyteRepository.saveAll(analytes);

        for (Analyte analyt : analytes) {
            analyteArray.add(convertAnalyteToData(analyt));
        }
        return analyteArray;

    }

//    public LabTestType fetchTestTypeByCode(String testTypeCode) {
//        LabTestType ttype = ttypeRepository.findByServiceCode(testTypeCode).orElseThrow(() -> APIException.notFound("TestType identified by code {0} not found", testTypeCode));
//        return ttype;
//    }
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
    

    

    /*
    a. Create a new PatientResults
    b. Read all PatientResults 
    c. Read PatientResults by Id
    c. Update PatientResults
     */
    @Transactional
    public LabRegister savePatientResults(PatientTestRegisterData patientRegData, final String visitNo, final Long requestId) {
        LabRegister labRegister = PatientTestRegisterData.map(patientRegData);
        Visit visit = visitService.findVisitEntityOrThrow(visitNo);
        labRegister.setVisit(visit);
        labRegister.setPatient(visit.getPatient());
        Optional<Employee> emp = employeeService.findEmployeeByStaffNumber(patientRegData.getRequestedBy());
        if (emp.isPresent()) {
            labRegister.setRequestedBy(emp.get());
        }

        if (patientRegData.getAccessionNo() == null || patientRegData.getAccessionNo().equals("")) {
//            String accessionNo = seqService.nextNumber(SequenceType.LabTestNumber);
            labRegister.setAccessNo(UuidGenerator.newUuid());
        }
        List<DoctorRequest> req = new ArrayList();
        //PatientTestRegister savedPatientTestRegister = patientRegRepository.save(patientTestReg);
        if (!patientRegData.getItemData().isEmpty()) {
            List<PatientLabTest> patientLabTest = new ArrayList<>();
            patientRegData.getItemData().stream().map((id) -> {
                if (id.getRequestId() != null) {
                    Optional<DoctorRequest> request = doctorRequestRepository.findById(id.getRequestItemId());
                    if (request.isPresent()) {
                        labRegister.setRequest(request.get());
                        request.get().setFulfillerStatus("Fulfilled");
                        req.add(request.get());
                    }
                }

                Item i = itemService.findItemWithNoFoundDetection(id.getItemCode());
                LabTestType labTestType = findTestTypeByItemService(i).get();
                PatientLabTest pte = new PatientLabTest();
                pte.setStatus(LabTestState.valueOf(id.getStatus()));
                pte.setTestPrice(id.getItemPrice());
                pte.setQuantity(id.getQuantity());
                pte.setTestType(labTestType);
                return pte;

            }).map((pte) -> {
                //Here I am anticipating for results *I know this is a bad idea that should not be implemented under normal situations, but due to time constraints from some guys, which you will learn later (or maybe by now you know them), I had to do this!*
                //find annalytes pegged to this patient
                List<Analyte> analytes = this.filterAnnalytesByPatient(labRegister.getPatient(), pte.getTestType());
                List<Results> results = new ArrayList<>();
                for (Analyte a : analytes) {
                    Results r = new Results();
                    r.setAnalyte(a);
                    r.setLowerRange(String.valueOf(a.getLowerRange()));
                    r.setUpperRange(String.valueOf(a.getUpperRange()));
                    r.setUnit(a.getUnits());
                    r.setComments("Pending result");
                    r.setPatientLabTest(pte);
                    results.add(r);
                }
                pte.setResults(results);
                return pte;
            }).forEachOrdered((pte) -> {
                patientLabTest.add(pte);
            });
            doctorRequestRepository.saveAll(req);
            labRegister.addPatientLabTest(patientLabTest);

        }

        labRegister.setStatus(LabTestState.Scheduled);
        LabRegister savedLabRegister = save(labRegister);

        return savedLabRegister;
    }
    // work around to generating lab number for now: 2020-01-14. Note I have renamed the class name from PatientTestRegister to Lab Register to ease confusion

    private LabRegister save(LabRegister labRegister) {
        String labNo = sequenceNumberService.next(1L, Sequences.LabNumber.name());
        labRegister.setAccessNo(labNo);
        return patientRegRepository.save(labRegister);
    }

    public Page<LabRegister> fetchAllPatientTests(final String visitNo, LabTestState status, Pageable pageable) {
        Specification<LabRegister> spec = PatientTestSpecification.createSpecification(visitNo, status);
        return patientRegRepository.findAll(spec, pageable);
    }

    public LabRegister findPatientTestRegisterByAccessNoWithNotFoundDetection(final String accessNo) {
        return patientRegRepository.findByAccessNo(accessNo).orElseThrow(() -> APIException.notFound("Lab file identifed by {0} not found ", accessNo));
    }

    public List<LabRegister> findPatientTestRegisterByVisit(final Visit visit) {
        return patientRegRepository.findByVisit(visit);
    }

    public PatientLabTest fetchPatientTestsById(Long id) {
        return PtestsRepository.findById(id).orElseThrow(() -> APIException.notFound("Patient Test identified by {0} is unavailable.", id));
    }

    public void deletePatientTestsById(Long id) {
        PtestsRepository.deleteById(id);
    }

    @Transactional
    public PatientLabTestSpecimen createPatientLabTestSpecimen(PatientLabTestSpecimen testSpecimen) {
        PatientLabTestSpecimen savedPatientLabtestSpecimen = patientLabTestSpecimenRepo.save(testSpecimen);
        //update test status
        return savedPatientLabtestSpecimen;
    }

    public List<PatientLabTestSpecimen> fetchAllLabTestSpecimensByPatientTest(PatientLabTest test) {
        return patientLabTestSpecimenRepo.findByPatientLabTest(test);
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

    public Optional<LabTestType> findTestTypeByItemService(final Item item) {
        return labTestTypeRepository.findByItemService(item);
    }

    public List<AnalyteData> fetchAnalyteByTestType(LabTestType testtype) {
        return analyteRepository.findByTestType(testtype)
                .stream()
                .map(p -> convertAnalyteToData(p))
                .collect(Collectors.toList());
    }

    public Page<AnalyteData> fetchAnalytesByAgeAndGender(String testCode, String patientNumber, Pageable pgbl) {
        Item item = itemService.findItemWithNoFoundDetection(testCode);
        LabTestType ttype = this.findTestTypeByItemService(item).orElseThrow(() -> APIException.notFound("Test type identifid by {0}  not found", testCode));
        Patient patient = visitService.findPatientOrThrow(patientNumber);
        return analyteRepository.findAnalytesByGenderAndAge(ttype, patient.getGender(), patient.getAge(), pgbl).map(p -> convertAnalyteToData(p));
    }

    public List<Analyte> filterAnnalytesByPatient(final Patient patient, final LabTestType ttype) {
        String gender = patient.getGender();
        List<Analyte> list = null;
        gender = patient.getGender() == null ? "Both"
                : patient.getGender() == null && patient.getGender().equals("M") ? "Male" : "Female";
        if (ttype.getWithRef()) {
            list = analyteRepository.findAllAnalyteByPatientsAndTests(ttype, Analyte.Gender.valueOf(gender), patient.getAge());
        } else {
            list = analyteRepository.findByTestType(ttype);
        }
        System.out.println("list size " + list.size());
        return list;
    }

    public Analyte fetchAnalyteById(Long id) {
        return analyteRepository.findById(id).orElseThrow(() -> APIException.notFound("Analyte identified by {0} not found.", id));
    }

    public void deleteAnalyteById(Long id) {
        analyteRepository.deleteById(id);
    }

    public PatientLabTest savePatientLabTest(PatientLabTest labtest) {
        return PtestsRepository.save(labtest);

    }
 public void deleteTestById(Long id) {
        ttypeRepository.deleteById(id);
    }
}
