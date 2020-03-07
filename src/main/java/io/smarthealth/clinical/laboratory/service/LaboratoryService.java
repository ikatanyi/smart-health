package io.smarthealth.clinical.laboratory.service;

import io.smarthealth.clinical.laboratory.data.LabRequestData;
import io.smarthealth.clinical.laboratory.data.LabRequestTestData;
import io.smarthealth.clinical.laboratory.data.LabResultData;
import io.smarthealth.clinical.laboratory.domain.LabRequest;
import io.smarthealth.clinical.laboratory.domain.LabRequestRepository;
import io.smarthealth.clinical.laboratory.domain.LabRequestTest;
import io.smarthealth.clinical.laboratory.domain.LabRequestTestRepository;
import io.smarthealth.clinical.laboratory.domain.LabResult;
import io.smarthealth.clinical.laboratory.domain.LabResultRepository;
import io.smarthealth.clinical.laboratory.domain.LabTest;
import io.smarthealth.clinical.laboratory.domain.LabTestRepository;
import io.smarthealth.clinical.laboratory.domain.enumeration.TestStatus;
import io.smarthealth.clinical.laboratory.domain.specification.LabRequestSpecification;
import io.smarthealth.clinical.laboratory.domain.specification.LabResultSpecification;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class LaboratoryService {

    private final LabRequestRepository repository;
    private final LabTestRepository labTestRepository;
    private final VisitRepository visitRepository;
    private final LabRequestTestRepository testRepository;
    private final SequenceNumberService sequenceNumberService;
    private final LabResultRepository labResultRepository;

    @Transactional
    public LabRequest createLabRequest(LabRequestData data) {
        String labNo = sequenceNumberService.next(1L, Sequences.LabNumber.name());
        LabRequest request = toLabRequest(data);
        request.setLabNumber(labNo);
        return repository.save(request);
    }

    public LabRequest getLabRequestByNumber(String labNo) {
        return repository.findByLabNumber(labNo)
                .orElseThrow(() -> APIException.notFound("Lab Request with id {0} Not Found", labNo));
    }

    public LabRequest getLabRequestById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Lab Request with id {0} Not Found", id));
    }

    public LabRequest updateLabRequest(Long id, LabRequestData data) {
        LabRequest requests = getLabRequestById(id);

        return requests;//repository.save(requests);
    }

    public void deleteLabRequest(Long id) {
        LabRequest requests = getLabRequestById(id);
        requests.setVoided(Boolean.TRUE);
        repository.save(requests);
    }

    public Page<LabRequest> getLabRequest(String labNumber, String orderNumber, String visitNumber, String patientNumber, TestStatus status, Pageable page) {
        Specification<LabRequest> spec = LabRequestSpecification.createSpecification(labNumber, orderNumber, visitNumber, patientNumber, status);
        return repository.findAll(spec, page);
    }

//RESULTS
    public LabResult createLabResult(LabResultData data) {
        LabResult result = toLabResult(data);
        LabResult savedResult = labResultRepository.save(result);
        updateTestRequest(savedResult);
        return savedResult;
    }

    public List<LabResult> createLabResult(List<LabResultData> data) {
        List<LabResult> toSave = data
                .stream()
                .map(x -> toLabResult(x))
                .collect(Collectors.toList());
        List<LabResult> savedResults = labResultRepository.saveAll(toSave);
        savedResults.forEach(x -> updateTestRequest(x));
        return savedResults;
    }

    public LabResult getResult(Long id) {
        return labResultRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Result with Id  {0} Not Found", id));
    }

    public LabResult updateLabResult(Long id, LabResultData data) {
        LabResult result = getResult(id);
        return result;
    }

    public void voidLabResult(Long id) {
        LabResult result = getResult(id);
        result.setVoided(Boolean.TRUE);
        labResultRepository.save(result);
    }

    public List<LabResultData> getLabResultByVisitNumber(Visit visit) {
        return getResultByVisitNumber(visit)
                .stream()
                .map(x -> x.toData())
                .collect(Collectors.toList());
    }

    public List<LabResult> getResultByVisitNumber(Visit visit) {
        return labResultRepository.findByVisit(visit);
    }

    public Page<LabResult> getLabResults(String visitNumber, String patientNumber, String labNumber, Boolean walkin, String testName, String orderNumber, DateRange range, Pageable page) {
        Specification<LabResult> spec = LabResultSpecification.createSpecification(visitNumber, patientNumber, labNumber, walkin, testName, orderNumber, range);
        return labResultRepository.findAll(spec, page);
    }

    private void updateTestRequest(LabResult savedResult) {
        LabRequestTest test = savedResult.getLabRequestTest();
        test.setEntered(Boolean.TRUE);
        test.setStatus(TestStatus.Completed);
        testRepository.save(test);
    }

    private LabResult toLabResult(LabResultData data) {
        LabRequestTest labRequestTest = getLabRequestTest(data.getLabRequestTestId());
        LabResult results = new LabResult();
        results.setAnalyte(data.getAnalyte());
        results.setLabNumber(data.getLabNumber());
        results.setLabRequestTest(labRequestTest);
        results.setLowerLimit(data.getLowerLimit());
        //
        results.setPatientNo(data.getPatientNo());
        results.setReferenceValue(data.getReferenceValue());
        results.setResultValue(data.getResultValue());
        results.setResultsDate(data.getResultsDate());
        results.setUnits(data.getUnits());
        results.setUpperLimit(data.getUpperLimit());
        return results;
    }

    private LabRequest toLabRequest(LabRequestData data) {
        LabRequest request = new LabRequest();
        request.setOrderNumber(data.getOrderNumber());
        request.setIsWalkin(data.getIsWalkin());
        if (!data.getIsWalkin()) {
            Visit visit = getPatientVisit(data.getVisitNumber());
            request.setVisit(visit);
            request.setRequestedBy(data.getRequestedBy());
            request.setPatientNo(visit.getPatient().getPatientNumber());
        } else {
            request.setRequestedBy(data.getPatientName());
            request.setPatientNo(data.getPatientNo());
        }
        request.setRequestDatetime(data.getRequestDatetime());
        request.setStatus(TestStatus.AwaitingSpecimen);

        request.addPatientTest(data.getTests()
                .stream()
                .map(x -> toLabRequestTest(x))
                .collect(Collectors.toList())
        );

        return request;
    }

    private LabRequestTest toLabRequestTest(LabRequestTestData data) {
        LabTest labTest = getLabTest(data.getTestId());

        LabRequestTest test = new LabRequestTest();
        test.setCollected(Boolean.FALSE);
        test.setCollectedBy(data.getCollectedBy());
        test.setCollectionDateTime(data.getCollectionDateTime());
        test.setEntered(Boolean.FALSE);
        test.setLabTest(labTest);
        test.setPaid(Boolean.FALSE);
        test.setRequestId(data.getRequestId());
        test.setStatus(TestStatus.AwaitingSpecimen);
        return test;
    }

    private LabTest getLabTest(Long id) {
        return labTestRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Lab Test with Id {0} Not Found", id));
    }

    private Visit getPatientVisit(String visitNo) {
        return visitRepository.findByVisitNumber(visitNo)
                .orElseThrow(() -> APIException.notFound("Visit with Id {0} Not Found", visitNo));
    }

    private LabRequestTest getLabRequestTest(Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Lab Request Test with Id {0} Not Found", id));
    }

}
