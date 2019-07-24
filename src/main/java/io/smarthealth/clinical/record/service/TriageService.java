package io.smarthealth.clinical.record.service;

import io.smarthealth.clinical.record.domain.TriageRepository;
import io.smarthealth.clinical.record.domain.VitalsRecord;
import io.smarthealth.clinical.record.data.VitalRecordData;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.utility.APIException;
import io.smarthealth.infrastructure.utility.ContentPage;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import java.util.ArrayList;
import java.util.Optional;
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
    public Long addVitalRecords(String visitNumber, VitalRecordData triage) {
        Visit visit = findVisitOrThrow(visitNumber);
        if (!StringUtils.equalsIgnoreCase(visit.getPatient().getPatientNumber(), triage.getPatientNumber())) {
            throw APIException.badRequest("Invalid Patient Number! mismatch in Patient Visit's patient number");
        }

        VitalsRecord vr = VitalRecordData.map(triage);

        float bmi = (float) BMI.calculateBMI(triage.getHeight(), triage.getWeight());
        String category = BMI.getCategory(bmi);

        vr.setPatient(visit.getPatient());
        vr.setVisit(visit);
        vr.setBmi(bmi);
        vr.setCategory(category);

        return triageRepository.save(vr).getId();

    }

    public ContentPage<VitalRecordData> fetchVitalRecordsByVisit(String visitNumber, Pageable page) {
        final ContentPage<VitalRecordData> triagePage = new ContentPage();
        Optional<Visit> visit = visitRepository.findByVisitNumber(visitNumber);
        if (visit.isPresent()) {
            Page<VitalsRecord> triageEntities = this.triageRepository.findByVisit(visit.get(), page);

            triagePage.setTotalPages(triageEntities.getTotalPages());
            triagePage.setTotalElements(triageEntities.getTotalElements());
            if (triageEntities.getSize() > 0) {
                final ArrayList<VitalRecordData> triagelist = new ArrayList<>(triageEntities.getSize());
                triagePage.setContents(triagelist);
                triageEntities.forEach((vt) -> triagelist.add(VitalRecordData.map(vt)));
            }
        }
        return triagePage;

    }

    public ContentPage<VitalRecordData> fetchVitalRecords(String patientNumber, Pageable page) {

        Page<VitalsRecord> triageEntities;
        if (patientNumber != null) {
            Patient patient = patientRepository.findByPatientNumber(patientNumber).orElse(null);
            triageEntities = triageRepository.findByPatient(patient, page);
        } else {
            triageEntities = this.triageRepository.findAll(page);
        }

        final ContentPage<VitalRecordData> triagePage = new ContentPage();
        triagePage.setTotalPages(triageEntities.getTotalPages());
        triagePage.setTotalElements(triageEntities.getTotalElements());
        if (triageEntities.getSize() > 0) {
            final ArrayList<VitalRecordData> triagelist = new ArrayList<>(triageEntities.getSize());
            triagePage.setContents(triagelist);
            triageEntities.forEach((vt) -> triagelist.add(VitalRecordData.map(vt)));
        }

        return triagePage;

    }

    public VitalRecordData getVitalRecordsById(String visitNumber, Long id) {
        Optional<VitalsRecord> entity = this.triageRepository.findById(id);

        return entity.map(VitalRecordData::map).orElse(null);
    }

    private Visit findVisitOrThrow(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Patient Session with Visit Number : {0} not found.", visitNumber));
    }

}
