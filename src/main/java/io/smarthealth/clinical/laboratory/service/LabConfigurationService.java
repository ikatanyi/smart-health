package io.smarthealth.clinical.laboratory.service;

import io.smarthealth.clinical.laboratory.data.AnalyteData;
import io.smarthealth.clinical.laboratory.data.LabDisciplineData;
import io.smarthealth.clinical.laboratory.data.LabSpecimenData;
import io.smarthealth.clinical.laboratory.data.LabTestData;
import io.smarthealth.clinical.laboratory.domain.Analyte;
import io.smarthealth.clinical.laboratory.domain.LabDiscipline;
import io.smarthealth.clinical.laboratory.domain.LabSpecimen;
import io.smarthealth.clinical.laboratory.domain.LabSpecimenRepository;
import io.smarthealth.clinical.laboratory.domain.LabTest;
import io.smarthealth.clinical.laboratory.domain.LabTestRepository;
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
import io.smarthealth.clinical.laboratory.domain.LabDisciplineRepository;

/**
 *
 * @author Kelsas
 */
@Service
public class LabConfigurationService {

    private final LabSpecimenRepository specimenRepository;
    private final LabDisciplineRepository displineRepository;
    private final ItemRepository itemRepository;
    private final LabTestRepository repository;

    public LabConfigurationService(LabSpecimenRepository specimenRepository, LabDisciplineRepository displineRepository, ItemRepository itemRepository, LabTestRepository repository) {
        this.specimenRepository = specimenRepository;
        this.displineRepository = displineRepository;
        this.itemRepository = itemRepository;
        this.repository = repository;
    }

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

    public LabTest updateTest(Long id, LabTestData data) {
        LabTest toUpdateTest = getTestById(id);
        Item item = findByItemCodeOrThrow(data.getItemCode());
        toUpdateTest.setActive(Boolean.TRUE);
        toUpdateTest.setCode(data.getShortName());
        toUpdateTest.setTestName(data.getTestName());
        toUpdateTest.setService(item);
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
        LabDiscipline displine=displineRepository.findById(data.getCategoryId()).orElse(null);
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

        labTest.addAnalytes(
                data.getAnalytes()
                        .stream()
                        .map(x -> createAnalyte(x))
                        .collect(Collectors.toList())
        );
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
}
