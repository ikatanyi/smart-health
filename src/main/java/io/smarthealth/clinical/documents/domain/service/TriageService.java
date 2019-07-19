package io.smarthealth.clinical.documents.domain.service;

import io.smarthealth.clinical.documents.domain.PatientDiagnosis;
import io.smarthealth.clinical.documents.domain.PatientDiagnosisRepository;
import io.smarthealth.clinical.documents.domain.TriageRepository;
import io.smarthealth.clinical.documents.domain.VitalsRecord;
import io.smarthealth.clinical.documents.domain.api.Diagnosis;
import io.smarthealth.clinical.documents.domain.api.Triage;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.utility.APIException;
import io.smarthealth.infrastructure.utility.ContentPage;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class TriageService {

    @Autowired
    private TriageRepository triageRepository;

    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private PatientRepository patientRepository;

//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private IDService idService;
//
//    @Autowired
//    private AdminServices adminServices;
//VITALS
    public Long addVitalRecords(String visitNumber, Triage triage) {
        Visit visit = findVisitOrThrow(visitNumber);
        if (!StringUtils.equalsIgnoreCase(visit.getPatient().getPatientNumber(), triage.getPatientNumber())) {
            throw APIException.badRequest("Invalid Patient Number! mismatch in Patient Visit's patient number");
        }

        VitalsRecord vr = Triage.map(triage);

        float bmi = (float) BMI.calculateBMI(triage.getHeight(), triage.getWeight());
        String category = BMI.getCategory(bmi);

        vr.setPatient(visit.getPatient());
        vr.setVisit(visit);
        vr.setBmi(bmi);
        vr.setCategory(category);

        return triageRepository.save(vr).getId();

    }

    public ContentPage<Triage> fetchVitalRecordsByVisit(String visitNumber, Pageable page) {
        final ContentPage<Triage> triagePage = new ContentPage();
        Optional<Visit> visit = visitRepository.findByVisitNumber(visitNumber);
        if (visit.isPresent()) {
            Page<VitalsRecord> triageEntities = this.triageRepository.findByVisit(visit.get(), page);

            triagePage.setTotalPages(triageEntities.getTotalPages());
            triagePage.setTotalElements(triageEntities.getTotalElements());
            if (triageEntities.getSize() > 0) {
                final ArrayList<Triage> triagelist = new ArrayList<>(triageEntities.getSize());
                triagePage.setContents(triagelist);
                triageEntities.forEach((vt) -> triagelist.add(Triage.map(vt)));
            }
        }
        return triagePage;

    }

    public ContentPage<Triage> fetchVitalRecords(String patientNumber, Pageable page) {

        Page<VitalsRecord> triageEntities;
        if (patientNumber != null) {
            Patient patient = patientRepository.findByPatientNumber(patientNumber).orElse(null);
            triageEntities = triageRepository.findByPatient(patient, page);
        } else {
            triageEntities = this.triageRepository.findAll(page);
        }

        final ContentPage<Triage> triagePage = new ContentPage();
        triagePage.setTotalPages(triageEntities.getTotalPages());
        triagePage.setTotalElements(triageEntities.getTotalElements());
        if (triageEntities.getSize() > 0) {
            final ArrayList<Triage> triagelist = new ArrayList<>(triageEntities.getSize());
            triagePage.setContents(triagelist);
            triageEntities.forEach((vt) -> triagelist.add(Triage.map(vt)));
        }

        return triagePage;

    }

    public Triage getVitalRecordsById(String visitNumber, Long id) {
        Optional<VitalsRecord> entity = this.triageRepository.findById(id);

        return entity.map(Triage::map).orElse(null);
    }

    private Visit findVisitOrThrow(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Patient Session with Visit Number : {0} not found.", visitNumber));
    }

}
