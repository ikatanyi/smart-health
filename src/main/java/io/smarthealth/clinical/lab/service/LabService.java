package io.smarthealth.clinical.lab.service;

import io.smarthealth.billing.service.BillingService;
import io.smarthealth.clinical.lab.data.AnalyteData;
import io.smarthealth.clinical.lab.data.ContainerData;
import io.smarthealth.clinical.lab.data.DisciplineData;
import io.smarthealth.clinical.lab.data.LabTestTypeData;
import io.smarthealth.clinical.lab.data.PatientTestRegisterData;
import io.smarthealth.clinical.lab.data.SpecimenData;
import io.smarthealth.clinical.lab.data.TestItemData;
import io.smarthealth.clinical.lab.domain.Analyte;
import io.smarthealth.clinical.lab.domain.AnalyteRepository;
import io.smarthealth.clinical.lab.domain.Container;
import io.smarthealth.clinical.lab.domain.ContainerRepository;
import io.smarthealth.clinical.lab.domain.Discipline;
import io.smarthealth.clinical.lab.domain.DisciplineRepository;
import io.smarthealth.clinical.lab.domain.LabTestRepository;
import io.smarthealth.clinical.lab.domain.LabTestType;
import io.smarthealth.clinical.lab.domain.LabTestTypeRepository;
import io.smarthealth.clinical.lab.domain.PatientLabTest;
import io.smarthealth.clinical.lab.domain.PatientLabTestSpecimen;
import io.smarthealth.clinical.lab.domain.PatientLabTestSpecimenRepo;
import io.smarthealth.clinical.lab.domain.PatientTestRegister;
import io.smarthealth.clinical.lab.domain.PatientTestRegisterRepository;
import io.smarthealth.clinical.lab.domain.Results;
import io.smarthealth.clinical.lab.domain.Specimen;
import io.smarthealth.clinical.lab.domain.SpecimenRepository;
import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.record.domain.specification.PatientTestSpecifica;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
public class LabService {

    private final AnalyteRepository analyteRepository;

    private final ModelMapper modelMapper;

    private final ContainerRepository containerRepository;

    private final DisciplineRepository disciplineRepository;

    private final LabTestTypeRepository ttypeRepository;

    private final LabTestRepository PtestsRepository;

    private final SpecimenRepository specimenRepository;

    private final DoctorsRequestRepository doctorRequestRepository;

    private final PatientTestRegisterRepository patientRegRepository;

    private final SequenceService seqService;

    private final EmployeeService employeeService;

    private final PatientService patientservice;
    private final ItemService itemService;
    private final VisitService visitService;
    private final BillingService billingService;

    private final LabTestTypeRepository labTestTypeRepository;

    private final PatientLabTestSpecimenRepo patientLabTestSpecimenRepo;

    public LabService(AnalyteRepository analyteRepository, ModelMapper modelMapper, ContainerRepository containerRepository, DisciplineRepository disciplineRepository, LabTestTypeRepository ttypeRepository, LabTestRepository PtestsRepository, SpecimenRepository specimenRepository, DoctorsRequestRepository doctorRequestRepository, PatientTestRegisterRepository patientRegRepository, SequenceService seqService, EmployeeService employeeService, PatientService patientservice, ItemService itemService, VisitService visitService, BillingService billingService, LabTestTypeRepository labTestTypeRepository, PatientLabTestSpecimenRepo patientLabTestSpecimenRepo) {
        this.analyteRepository = analyteRepository;
        this.modelMapper = modelMapper;
        this.containerRepository = containerRepository;
        this.disciplineRepository = disciplineRepository;
        this.ttypeRepository = ttypeRepository;
        this.PtestsRepository = PtestsRepository;
        this.specimenRepository = specimenRepository;
        this.doctorRequestRepository = doctorRequestRepository;
        this.patientRegRepository = patientRegRepository;
        this.seqService = seqService;
        this.employeeService = employeeService;
        this.patientservice = patientservice;
        this.itemService = itemService;
        this.visitService = visitService;
        this.billingService = billingService;
        this.labTestTypeRepository = labTestTypeRepository;
        this.patientLabTestSpecimenRepo = patientLabTestSpecimenRepo;
    }

    @Transactional
    public Long createTestType(LabTestTypeData testtypeData) {
        try {
            //find item by code
            Item item = itemService.findItemWithNoFoundDetection(testtypeData.getCode());

            LabTestType testtype = LabTestTypeData.map(testtypeData);
            testtype.setItemService(item);
            List<Specimen> specimens = new ArrayList<>();
//            testtypeData.getSpecimenId()
//                    .stream()
//                    .map((id) -> specimenRepository.findById(id))
//                    .filter((specimen) -> (specimen.isPresent()))
//                    .forEachOrdered((specimen) -> {
//                        testtype.addSpecimen(specimen.get());
//                    });
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

//    @Transactional
//    public String saveTestType(LabTestType testtype) {
//        try {
//            ttypeRepository.save(testtype);
//            return testtype.getServiceCode();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw APIException.internalError("Error occured while creating testType ", e.getMessage());
//        }
//    }
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

        java.lang.reflect.Type listType = new TypeToken<List<Discipline>>() {
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
    public PatientTestRegister savePatientResults(PatientTestRegisterData patientRegData, final String visitNo, final Long requestId) {
        PatientTestRegister patientTestReg = PatientTestRegisterData.map(patientRegData);
        if (visitNo != null) {
            Visit visit = visitService.findVisitEntityOrThrow(visitNo);
            patientTestReg.setVisit(visit);
            patientTestReg.setPatient(visit.getPatient());
        } else {
            throw APIException.badRequest("A fully fledged visit session MUST be available", "");
        }
        Optional<Employee> emp = employeeService.findEmployeeByStaffNumber(patientRegData.getRequestedBy());
        if (emp.isPresent()) {
            patientTestReg.setRequestedBy(emp.get());
        }

        if (requestId != null) {
            Optional<DoctorRequest> request = doctorRequestRepository.findById(requestId);
            if (request.isPresent()) {
                patientTestReg.setRequest(request.get());
            }

        }
        if (patientRegData.getAccessionNo() == null || patientRegData.getAccessionNo().equals("")) {
            String accessionNo = seqService.nextNumber(SequenceType.LabTestNumber);
            patientTestReg.setAccessNo(accessionNo);
        }

        //PatientTestRegister savedPatientTestRegister = patientRegRepository.save(patientTestReg);
        if (!patientRegData.getItemData().isEmpty()) {
            List<PatientLabTest> patientLabTest = new ArrayList<>();
            for (TestItemData id : patientRegData.getItemData()) {
                Item i = itemService.findItemWithNoFoundDetection(id.getItemCode());
                LabTestType labTestType = findTestTypeByItemService(i).get();
                PatientLabTest pte = new PatientLabTest();
                pte.setStatus(LabTestState.valueOf(id.getStatus()));
                pte.setTestPrice(id.getItemPrice());
                pte.setQuantity(id.getQuantity());
                pte.setTestType(labTestType);
                //Here I am anticipating for results *I know this is a bad idea that should not be implemented under normal situations, but due to time constraints from some guys, which you will learn later (or maybe by now you know them), I had to do this!*
                //find annalytes pegged to this patient
                List<Analyte> analytes = this.filterAnnalytesByPatient(patientTestReg.getPatient(), pte.getTestType());
                List<Results> results = new ArrayList<>();

                for (Analyte a : analytes) {
                    Results r = new Results();
                    r.setAnalyte(a);
                    r.setComments("Pending result");
                    r.setPatientLabTest(pte);
                    results.add(r);
                }
                pte.setResults(results);

                patientLabTest.add(pte);
            }
            patientTestReg.addPatientLabTest(patientLabTest);

        }

//        patientRegData.getTestData().stream().map((patienttestdata) -> {
//            PatientLabTest patienttestsEntity = PatientLabTestData.map(patienttestdata);
//            if (patienttestdata.getSpecimenId() != null) {
//                Specimen spec = fetchSpecimenById(patienttestdata.getSpecimenId());
//                patienttestsEntity.setSpecimen(spec);
//            }
//            return patienttestsEntity;
//        }).forEachOrdered((PatientLabTest patienttestsEntity) -> {
//            patientTestReg.addLabTest(patienttestsEntity);
//        });

        /*
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
         */
        patientTestReg.setStatus(LabTestState.Scheduled);
        PatientTestRegister updatedPatientTestRegister = patientRegRepository.save(patientTestReg);
        return updatedPatientTestRegister;
    }

//    
//    @Transactional
//    public PatientTestRegister savePatientResults(PatientTestRegisterData patientRegData, final String visitNo) {
//        PatientTestRegister patientTestReg = PatientTestRegisterData.map(patientRegData);
//        if (visitNo != null) {
//            Visit visit = visitService.findVisitEntityOrThrow(visitNo);
//            patientTestReg.setVisit(visit);
//            patientTestReg.setPatient(visit.getPatient());
//        }
//        Optional<Employee> emp = employeeService.findEmployeeByStaffNumber(patientRegData.getRequestedBy());
//        if (emp.isPresent()) {
//            patientTestReg.setRequestedBy(emp.get());
//        }
//        /*
//        Optional<DoctorRequest> request = doctorRequestRepository.findById(Long.parseLong(patientRegData.getRequestId() != null ? patientRegData.getRequestId() : "0"));
//        if (request.isPresent()) {
//            patientTestReg.setRequest(request.get());
//        }
//         */
//        if (patientRegData.getAccessionNo() == null) {
//            patientTestReg.setAccessNo(seqService.nextNumber(SequenceType.LabTestNumber));
//        }
//
//        //PatientTestRegister savedPatientTestRegister = patientRegRepository.save(patientTestReg);
//        if (!patientRegData.getPatientLabTestData().isEmpty()) {
//            List<PatientLabTest> patientLabTest = new ArrayList<>();
//            for (PatientLabTestData pt : patientRegData.getPatientLabTestData()) {
//                PatientLabTest pte = PatientLabTestData.map(pt);
////                patientTestReg.addPatientLabTest(pte);
//                //System.out.println("pte " + pte.toString());
//                List<Specimen> specimens = new ArrayList<>();
//                if (!pt.getPatientLabTestSpecimen().isEmpty()) {
//                    for (PatientLabTestSpecimen pts : pt.getPatientLabTestSpecimen()) {
//                        Optional<Specimen> s = specimenRepository.findById(pts.getSpecimenId());
//                        if (s.isPresent()) {
//                            specimens.add(s.get());
//                        }
//                    }
//                    pte.setSpecimen(specimens);
//
//                }
//
//                patientLabTest.add(pte);
//            }
////            patientTestReg.setPatientLabTest(patientLabTest);
//            patientTestReg.addPatientLabTest(patientLabTest);
//        }
//
////        patientRegData.getTestData().stream().map((patienttestdata) -> {
////            PatientLabTest patienttestsEntity = PatientLabTestData.map(patienttestdata);
////            if (patienttestdata.getSpecimenId() != null) {
////                Specimen spec = fetchSpecimenById(patienttestdata.getSpecimenId());
////                patienttestsEntity.setSpecimen(spec);
////            }
////            return patienttestsEntity;
////        }).forEachOrdered((PatientLabTest patienttestsEntity) -> {
////            patientTestReg.addLabTest(patienttestsEntity);
////        });
//
//        /*
//        if (patientRegData.getBillData() == null) {
//            PatientBill bill = new PatientBill();
//            bill.setAmount(0.0);
//            bill.setBalance(0.0);
//            bill.setBillLines(new ArrayList());
//            bill.setBillNumber(seqService.nextNumber(SequenceType.BillNumber));
//            bill.setPatient(visit.getPatient());
//            bill.setStatus(BillStatus.Draft);
//            bill.setVisit(visit);
//            patientTestReg.setBill(bill);
//        }
//         */
//        PatientTestRegister updatedPatientTestRegister = patientRegRepository.save(patientTestReg);
//        return updatedPatientTestRegister;
//    }
//    public Page<PatientLabTestData> fetchAllPatientTests(final Visit visit, LabTestState status, Pageable pgbl) {
//        Page<PatientLabTestData> ptests = PtestsRepository.fetchPatientLabTests(visit.getPatient(), visit, status, pgbl).map(p -> PatientLabTestData.map(p));
//        return ptests;
//    }
    public Page<PatientTestRegister> fetchAllPatientTests(final String visitNo, LabTestState status, Pageable pageable) {
        Specification<PatientTestRegister> spec = PatientTestSpecifica.createSpecification(visitNo, status);
        return patientRegRepository.findAll(spec, pageable);
    }

    public PatientTestRegister findPatientTestRegisterByAccessNoWithNotFoundDetection(final String accessNo) {
        return patientRegRepository.findByAccessNo(accessNo).orElseThrow(() -> APIException.notFound("Lab file identifed by {0} not found ", accessNo));
    }

    public List<PatientTestRegister> findPatientTestRegisterByVisit(final Visit visit) {
        return patientRegRepository.findByVisit(visit);
    }

    /*
    public PatientLabTest UpdatePatientTest(PatientLabTestData testData, Pageable pgbl) {
        List<Results> results = new ArrayList();
        PatientLabTest test = this.fetchPatientTestsById(testData.getId());

        PatientLabTest test2 = PatientLabTestData.map(testData);

        if (testData.getStatus()== LabTestState.Accepted) {
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
     */
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
    @Transactional
    public List<ContainerData> createContainers(List<ContainerData> containerData) {
        java.lang.reflect.Type listType = new TypeToken<List<Container>>() {
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

    public Optional<LabTestType> findTestTypeByItemService(final Item item) {
        return labTestTypeRepository.findByItemService(item);
    }

    public Page<AnalyteData> fetchAnalyteByTestType(LabTestType testtype, Pageable pgbl) {
        return analyteRepository.findByTestType(testtype, pgbl).map(p -> convertAnalyteToData(p));
    }

    public Page<AnalyteData> fetchAnalytesByAgeAndGender(String testCode, String patientNumber, Pageable pgbl) {
        Item item = itemService.findItemWithNoFoundDetection(testCode);
        LabTestType ttype = this.findTestTypeByItemService(item).orElseThrow(() -> APIException.notFound("Test type identifid by {0}  not found", testCode));
        Patient patient = patientservice.findPatientOrThrow(patientNumber);
        return analyteRepository.findAnalytesByGenderAndAge(ttype, patient.getGender(), patient.getAge(), pgbl).map(p -> convertAnalyteToData(p));
    }

    public List<Analyte> filterAnnalytesByPatient(final Patient patient, final LabTestType ttype) {
        String gender = patient.getGender();
        if (patient.getGender() == null) {
            gender = "Both";
        }
        if (patient.getGender().equals("M")) {
            gender = "Male";
        }
        if (patient.getGender().equals("F")) {
            gender = "Female";
        }

        List<Analyte> list = analyteRepository.findAllAnalyteByPatientsAndTests(ttype, Analyte.Gender.valueOf(gender), patient.getAge()); 
        return list;
    }

    public Analyte fetchAnalyteById(Long id) {
        return analyteRepository.findById(id).orElseThrow(() -> APIException.notFound("Analyte identified by {0} not found.", id));
    }

    public void deleteAnalyteById(Long id) {
        analyteRepository.deleteById(id);
    }

}
