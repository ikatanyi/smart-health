package io.smarthealth.clinical.lab.service;

import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.billing.domain.PatientBill;
import io.smarthealth.billing.domain.PatientBillItem;
import io.smarthealth.billing.domain.enumeration.BillStatus;
import io.smarthealth.billing.service.PatientBillService;
import io.smarthealth.clinical.lab.data.AnalyteData;
import io.smarthealth.clinical.lab.data.ContainerData;
import io.smarthealth.clinical.lab.data.DisciplineData;
import io.smarthealth.clinical.lab.data.SpecimenData;
import io.smarthealth.clinical.lab.data.LabTestTypeData;
import io.smarthealth.clinical.lab.data.PatientTestData;
import io.smarthealth.clinical.lab.data.PatientTestRegisterData;
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
import io.smarthealth.clinical.lab.domain.Results;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.clinical.lab.data.ResultsData;
import io.smarthealth.clinical.lab.domain.PatientTestRegister;
import io.smarthealth.clinical.lab.domain.PatientTestRegisterRepository;
import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.time.LocalDate;

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
    private final SpecimenRepository specimenRepository;

    @Autowired
    private final DoctorsRequestRepository doctorRequestRepository;

    @Autowired
    PatientTestRegisterRepository patientRegRepository;

    @Autowired
    private final SequenceService seqService;

    private PatientService patientservice;
    private PatientBillService billService;
    private ItemService itemService;
    private final VisitService visitService;

    public LabService(AnalyteRepository analyteRepository, ContainerRepository containerRepository, DisciplineRepository disciplineRepository, LabTestTypeRepository ttypeRepository, LabTestRepository PtestsRepository, VisitService visitService, SpecimenRepository specimenRepository, DoctorsRequestRepository doctorRequestRepository, SequenceService seqService) {
        this.analyteRepository = analyteRepository;
        this.containerRepository = containerRepository;
        this.disciplineRepository = disciplineRepository;
        this.ttypeRepository = ttypeRepository;
        this.PtestsRepository = PtestsRepository;
        this.visitService = visitService;
        this.specimenRepository = specimenRepository;
        this.doctorRequestRepository = doctorRequestRepository;
        this.seqService = seqService;
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

    public LabTestType findTestById(Long id) {
        return ttypeRepository.findById(id).orElseThrow(() -> APIException.notFound("Test identified by id {0} not found", id)
        );
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

    public Specimen fetchSpecimenById(Long id) {
        return specimenRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Specimen identified by {0} not found.", id));//.map(p -> SpecimenData.map(p)).orElseThrow(() -> APIException.notFound("Specimen identified by {0} not found.", id));
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
    public PatientTestRegister savePatientResults(PatientTestRegisterData patientRegData) {
//        List<PatientLabTest> patienTests = new ArrayList();
//        PatientBillData data = new PatientBillData();
//        List<PatientBillItemData> billItemArray = new ArrayList();

        Visit visit = visitService.findVisitEntityOrThrow(patientRegData.getVisitNumber());
        PatientTestRegister patientTestReg = PatientTestRegisterData.map(patientRegData);
        patientTestReg.setVisit(visit);
        patientTestReg.setPatient(visit.getPatient());

        Optional<DoctorRequest> request = doctorRequestRepository.findById(Long.parseLong(patientRegData.getRequestId() != null ? patientRegData.getRequestId() : "0"));
        if (request.isPresent()) {
            patientTestReg.setRequest(request.get());
        }

        if (patientRegData.getLabTestNumber() == null) {
            patientTestReg.setLabTestNumber(seqService.nextNumber(SequenceType.LabTestNumber));
        }

        patientRegData.getTestData().stream().map((patienttestdata) -> {
            PatientLabTest patienttestsEntity = PatientTestData.map(patienttestdata);
            if (patienttestdata.getSpecimenId() != null) {
                Specimen spec = fetchSpecimenById(patienttestdata.getSpecimenId());
                patienttestsEntity.setSpecimen(spec);
            }
            return patienttestsEntity;
        }).forEachOrdered((PatientLabTest patienttestsEntity) -> {
            patientTestReg.addLabTest(patienttestsEntity);
        });

        if (patientRegData.getBillData() == null) {
            PatientBill bill = new PatientBill();
            bill.setAmount(0.0);
            bill.setBalance(0.0);
            bill.setBillLines(new ArrayList());
            bill.setBillNumber(seqService.nextNumber(SequenceType.BillNumber));
            bill.setPatient(visit.getPatient());
            bill.setStatus(BillStatus.Draft);
            bill.setVisit(visit);
            patientTestReg.setBill(bill);
        }

        PatientTestRegister patientTestsList = patientRegRepository.save(patientTestReg);
        return patientTestsList;
    }

    public Page<PatientTestData> fetchAllPatientTests(final Visit visit, LabTestState status, Pageable pgbl) {
        Page<PatientTestData> ptests = PtestsRepository.findByPatientAndVisitAndStatus(visit.getPatient(), visit, status, pgbl).map(p -> PatientTestData.map(p));
        return ptests;
    }

    public PatientLabTest UpdatePatientTest(PatientTestData testData, Pageable pgbl) {
        List<Results> results = new ArrayList();
        PatientLabTest test = this.fetchPatientTestsById(testData.getId());

        PatientLabTest test2 = PatientTestData.map(testData);

        if (testData.getState() == LabTestState.Accepted) {
            Page<AnalyteData> analytedata = this.fetchAnalytesByAgeAndGender(test.getTesttype().getServiceCode(), testData.getPatientNumber(), pgbl);
            for (AnalyteData anlytdata : analytedata) {
                Results rsltData = new Results();
                rsltData.setCategory(anlytdata.getCategory());
                rsltData.setLowerRange(anlytdata.getLowerRange());
                rsltData.setTestCode(anlytdata.getTestCode());
                rsltData.setTestName(anlytdata.getTestName());
                rsltData.setTestType(String.valueOf(anlytdata.getTestTypeId()));
                rsltData.setUnits(anlytdata.getUnits());
                rsltData.setUpperRange(anlytdata.getUpperRange());
                results.add(rsltData);
            }
            for (ResultsData analyte : testData.getResultData()) {
                Results rslt = ResultsData.map(analyte);
                results.add(rslt);
            }
            test2.getPatientTestRegister().getRequest().setFulfillerStatus("PartiallyFulfilled");

            //Billing
            if (test2.getPatientTestRegister().getBill() != null) {
                PatientBillItem billItem = new PatientBillItem();
                Optional<Item> item = itemService.findByItemCode(testData.getTestCode());
                if (item.isPresent()) {
                    billItem.setAmount(item.get().getCostRate());
                    billItem.setBalance(item.get().getCostRate());
                    billItem.setBillingDate(LocalDate.now());
                    billItem.setItem(item.get());
                    billItem.setPatientBill(test2.getPatientTestRegister().getBill());
                    billItem.setQuantity(1.0);
                    billItem.setServicePoint(ServicePointType.Laboratory.toString());
//                    billItem.setServicePointId();
                    billItem.setStatus(BillStatus.Interim);
//                    billItem.setTransactionNo(transactionNo);
                    test2.getPatientTestRegister().getBill().getBillLines().add(billItem);
                }
            }
        }
        if (testData.getState() == LabTestState.Completed) {
            test2.getPatientTestRegister().getRequest().setFulfillerStatus("Fulfilled");//DoctorRequest.FullFillerStatusType.Fulfilled);
        }
        if (testData.getState() == LabTestState.Cancelled) {
            test2.getPatientTestRegister().getRequest().setFulfillerStatus("Cancelled");//DoctorRequest.FullFillerStatusType.Fulfilled);
        }

        test2.setResults(results);
        return PtestsRepository.save(test2);
    }

    public PatientLabTest fetchPatientTestsById(Long id) {
        return PtestsRepository.findById(id).orElseThrow(() -> APIException.notFound("Patient Test identified by {0} not found.", id));
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

    public Page<AnalyteData> fetchAnalytesByAgeAndGender(String testCode, String patientNumber, Pageable pgbl) {
        LabTestType ttype = this.fetchTestTypeByCode(testCode);
        Patient patient = patientservice.findPatientOrThrow(patientNumber);
        return analyteRepository.findAnalytesByGenderAndAge(ttype, patient.getGender(), patient.getAge(), pgbl).map(p -> convertAnalyteToData(p));
    }

    public Analyte fetchAnalyteById(Long id) {
        return analyteRepository.findById(id).orElseThrow(() -> APIException.notFound("Analyte identified by {0} not found.", id));
    }

    public void deleteAnalyteById(Long id) {
        analyteRepository.deleteById(id);
    }

}
