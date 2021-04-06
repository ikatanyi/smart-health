package io.smarthealth.clinical.laboratory.service;

import io.smarthealth.clinical.laboratory.data.AnalyteData;
import io.smarthealth.clinical.laboratory.data.LabDisciplineData;
import io.smarthealth.clinical.laboratory.data.LabSpecimenData;
import io.smarthealth.clinical.laboratory.data.LabTestData;
import io.smarthealth.clinical.laboratory.domain.*;
import io.smarthealth.clinical.laboratory.domain.specification.LabTestSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import io.smarthealth.stock.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class LabConfigurationService {

    private final AnalyteRepository analyteRepository;
    private final LabSpecimenRepository specimenRepository;
    private final LabDisciplineRepository displineRepository;
    private final ItemRepository itemRepository;
    private final LabTestRepository repository;
    private final ItemService itemService;
    private final LabEquipmentRepository labEquipmentRepository;

    public LabTest createTest(LabTestData data) {
        LabTest toSave = toLabTest(data);
        return repository.save(toSave);
    }

    public List<LabTest> createTest(List<LabTestData> lists) {
        List<LabTest> toSave = lists
                .stream()
                .map(x -> toLabTest(x))
                .collect(Collectors.toList());
        return repository.saveAll(toSave);
    }

    public void fixTestsImportedForIvory(List<LabTestData> lists) {

        for (LabTestData data : lists) {
            LabTest labTest = getTestByName(data.getTestName());
            if (data.getItemCode() != null) {
                labTest.setCode(data.getItemCode());
            }
            labTest.setHasReferenceValue(data.getHasReferenceValue());
            repository.save(labTest);
        }

    }

    public List<LabTest> searchLabTest(String keyword) {
        return repository.searchLabTest(keyword);
    }

    public LabTest getTestByName(String testName) {
        return repository.findByTestName(testName)
                .orElseThrow(() -> APIException.notFound("Lab Test with name {0} not found.", testName));
    }

    public LabTest getTestById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Lab Test with id {0} not found.", id));
    }

    @Transactional
    private void clearAnalyte(Long testId) {
        analyteRepository.deleteByTestId(testId);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LabTest updateTest(Long id, LabTestData data) {
        LabTest toUpdateTest = getTestById(id);
        clearAnalyte(toUpdateTest.getId());

        Item item = findByItemCodeOrThrow(data.getItemCode());
        LabDiscipline displine = data.getCategoryId() != null ? displineRepository.findById(data.getCategoryId()).orElse(null) : null;
//        toUpdateTest.setActive(data.getActive()!=null ? data.getActive() : true);
        toUpdateTest.setRequiresConsent(data.getRequiresConsent());
        toUpdateTest.setTurnAroundTime(data.getTurnAroundTime());
        toUpdateTest.setGender(data.getGender());
        toUpdateTest.setHasReferenceValue(data.getHasReferenceValue());
        toUpdateTest.setCode(data.getShortName());
        toUpdateTest.setDispline(displine);
        toUpdateTest.setTestName(data.getTestName());
        toUpdateTest.setService(item);

        //delete the 
        toUpdateTest.addAnalytes(
                data.getAnalytes()
                        .stream()
                        .map(x -> updateAnalyte(x))
                        .collect(Collectors.toList())
        );
        data.getPanelTests()
                .stream()
                .forEach(x -> {
                    toUpdateTest.getPanelTests().add(getTestById(x.getTestId()));
                });

        return repository.save(toUpdateTest);
    }

    public void voidLabTest(Long id) {
        LabTest test = getTestById(id);

        repository.delete(test);
    }

    public void deleteDispline(Long id) {
        LabDiscipline test = getDisplineOrThrow(id);
        displineRepository.delete(test);
    }

    public void deleteSpecimen(Long id) {
        LabSpecimen test = getLabSpecimenOrThrow(id);
        specimenRepository.delete(test);
    }

    public Page<LabTest> getLabTests(String query, String displine, Pageable page) {
        Specification<LabTest> spec = LabTestSpecification.createSpecification(query, displine);
        return repository.findAll(spec, page);
    }

    private Item findByItemCodeOrThrow(final String itemCode) {
        return itemRepository.findByItemCode(itemCode)
                .orElseThrow(() -> APIException.notFound("Item with code {0} not found.", itemCode));
    }

    private LabTest toLabTest(LabTestData data) {
        Item item = findByItemCodeOrThrow(data.getItemCode());
        if (repository.findByService(item).isPresent()) {
            throw APIException.badRequest("Lab Test with service {0} already exists", item.getItemName());
        }
        LabDiscipline displine = null;
        if (data.getCategoryId() != null) {
            displine = displineRepository.findById(data.getCategoryId()).orElse(null);
        } else {
            try {
                displine = displineRepository.findByDisplineName(data.getDiscplineName()).orElseThrow(() -> APIException.notFound("Discpline name identified by {0} not found ", data.getDiscplineName()));
            } catch (Exception e) {
                System.out.println("Displine not found ");
            }
        }

        LabTest labTest = new LabTest();
        labTest.setActive(Boolean.TRUE);
        labTest.setRequiresConsent(data.getRequiresConsent());
        labTest.setTurnAroundTime(data.getTurnAroundTime());
        labTest.setGender(data.getGender());
        labTest.setHasReferenceValue(data.getHasReferenceValue());
        labTest.setCode(data.getShortName());
        labTest.setDispline(displine);
        labTest.setTestName(data.getTestName());
        labTest.setService(item);
        labTest.setIsPanel(data.getIsPanel());

        labTest.addAnalytes(
                data.getAnalytes()
                        .stream()
                        .map(x -> createAnalyte(x))
                        .collect(Collectors.toList())
        );
        //this should 
        data.getPanelTests()
                .stream()
                .forEach(x -> {
                    labTest.getPanelTests().add(getTestById(x.getTestId()));
                });

        return labTest;
    }

    private Analyte createAnalyte(AnalyteData data) {
        Analyte analyte = new Analyte();
        analyte.setAnalyte(data.getAnalyte());
        analyte.setUnits(data.getUnits());
        analyte.setLowerLimit(data.getLowerLimit());
        analyte.setUpperLimit(data.getUpperLimit());
        analyte.setReferenceValue(data.getReferenceValue());
        return analyte;
    }

    private Analyte updateAnalyte(AnalyteData data) {
        Analyte analyte;
        if (data.getId() == null) {
            analyte = new Analyte();
        } else {
            analyte = analyteRepository.findById(data.getId()).orElse(new Analyte());
        }
        analyte.setAnalyte(data.getAnalyte());
        analyte.setUnits(data.getUnits());
        analyte.setLowerLimit(data.getLowerLimit());
        analyte.setUpperLimit(data.getUpperLimit());
        analyte.setReferenceValue(data.getReferenceValue());
        return analyte;
    }

    public LabSpecimen createSpecimen(LabSpecimenData data) {
        LabSpecimen specimen = new LabSpecimen();
        specimen.setSpecimen(data.getSpecimen());
        return specimenRepository.save(specimen);
    }

    public List<LabSpecimen> createSpecimen(List<LabSpecimenData> lists) {
        List<LabSpecimen> toSave = lists
                .stream()
                .map(x -> {
                    LabSpecimen specimen = new LabSpecimen();
                    specimen.setSpecimen(x.getSpecimen());
                    return specimen;
                })
                .collect(Collectors.toList());
        return specimenRepository.saveAll(toSave);
    }

    public Optional<LabSpecimen> getLabSpecimen(Long id) {
        return specimenRepository.findById(id);
    }

    public LabSpecimen getLabSpecimenOrThrow(Long id) {
        return getLabSpecimen(id)
                .orElseThrow(() -> APIException.notFound("Lab Specimen with id {0} Not Found", id));
    }

    public LabSpecimen updateSpecimen(Long id, LabSpecimenData data) {
        LabSpecimen specimen = getLabSpecimenOrThrow(id);
        specimen.setSpecimen(data.getSpecimen());
        return specimenRepository.save(specimen);
    }

    public List<LabSpecimen> findLabSpecimens() {
        return specimenRepository.findAll();
    }

    public LabDiscipline createDispline(LabDisciplineData data) {
        LabDiscipline displine = new LabDiscipline();
        displine.setDisplineName(data.getDisplineName());
        return displineRepository.save(displine);
    }

    public List<LabDiscipline> createDispline(List<LabDisciplineData> lists) {
        List<LabDiscipline> toSave = lists
                .stream()
                .map(x -> {
                    LabDiscipline displine = new LabDiscipline();
                    displine.setDisplineName(x.getDisplineName());
                    return displine;
                })
                .collect(Collectors.toList());
        return displineRepository.saveAll(toSave);
    }

    public Optional<LabDiscipline> getDispline(Long id) {
        return displineRepository.findById(id);
    }

    public LabDiscipline getDisplineOrThrow(Long id) {
        return getDispline(id)
                .orElseThrow(() -> APIException.notFound("Lab Displine with id {0} Not Found", id));
    }

    public LabDiscipline updateDispline(Long id, LabDisciplineData data) {
        LabDiscipline displine = getDisplineOrThrow(id);
        displine.setCompanyId(data.getDisplineName());
        return displineRepository.save(displine);
    }

    public List<LabDiscipline> findDisplines() {
        return displineRepository.findAll();
    }

    public LabEquipment createLabEquipment(LabEquipment equipment) {
        return labEquipmentRepository.save(equipment);
    }

    public List<LabEquipment> searchLabEquipmentByName(String name) {
        return labEquipmentRepository.findByEquipmentNameContaining(name);
    }

    public List<LabEquipment> fetchLabEquipments() {
        return labEquipmentRepository.findAll();
    }
}
