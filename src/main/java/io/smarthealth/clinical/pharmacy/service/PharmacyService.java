/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.pharmacy.service;

import io.smarthealth.clinical.pharmacy.data.PatientDrugsData;
import io.smarthealth.clinical.pharmacy.domain.PatientDrugs;
import io.smarthealth.clinical.pharmacy.domain.PatientDrugsRepository;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.record.domain.Prescription;
import io.smarthealth.clinical.record.domain.PrescriptionRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
public class PharmacyService {

    @Autowired
    PatientDrugsRepository patientDrugsRepository;

    @Autowired
    PrescriptionRepository prescriptionRepository;

    @Autowired
    VisitRepository visitRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public List<PatientDrugsData> savePatientDrugs(List<PatientDrugsData> patientdrugsData) {
        try {
            List<PatientDrugsData> savedDrugsList = new ArrayList();
            modelMapper.getConfiguration()
                    .setMatchingStrategy(MatchingStrategies.STRICT);
            for (PatientDrugsData patientdrugsdata : patientdrugsData) {
                PatientDrugs pd = PatientDrugsData.map(patientdrugsdata);
                Prescription presc = prescriptionRepository.findById(patientdrugsdata.getPrescriptionId()).orElseThrow(() -> APIException.notFound("No prescription identified by {0}", patientdrugsdata.getPrescriptionId()));

                pd.setPrescription(presc);
                pd.setVisit(presc.getVisit());
                pd.setPatient(presc.getVisit().getPatient());
                PatientDrugs saveddrug = patientDrugsRepository.save(pd);
                savedDrugsList.add(modelMapper.map(saveddrug, PatientDrugsData.class));

                if (!Objects.equals(saveddrug.getDurationUnits(), saveddrug.getIssuedQuantity())) {
                    System.out.println("saveddrug.getDurationUnits()  "+saveddrug.getDurationUnits() );
                    System.out.println("saveddrug.getIssuedQuantity()  "+saveddrug.getIssuedQuantity());
//                    presc.setIssuedQuantity(presc.getIssuedQuantity() + saveddrug.getIssuedQuantity());
                    presc.setFulfillerStatus(FullFillerStatusType.PartiallyFullfilled);
                } else {
                    presc.setFulfillerStatus(FullFillerStatusType.Fulfilled);
                }
                prescriptionRepository.save(presc);
            }
            return savedDrugsList;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error occured while creating while sving patient drugs ", e.getMessage());
        }
    }

    @Transactional
    public List<PatientDrugsData> getByVisitIdAndPatientId(String visitNumber, String patientNumber) {
        Visit visit = visitRepository.findByVisitNumber(visitNumber).orElse(null);
        Patient patient = patientRepository.findByPatientNumber(patientNumber).orElse(null);
        return convertPatientDrugsToDataList(patientDrugsRepository.findByVisitOrPatient(visit, patient));//.map(p -> convertPatientDrugsToData(p)).orElseThrow(() -> APIException.notFound("Patient Drug identified by {0} not found.", id));
    }

    @Transactional
    public PatientDrugsData getById(Long id) {
        return patientDrugsRepository.findById(id).map(p -> convertPatientDrugsToData(p)).orElseThrow(() -> APIException.notFound("Patient Drug identified by {0} not found.", id));
    }

    public boolean deletePatientDrug(Long id) {
        try {
            patientRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error deleting Patient drugs with id " + id, e.getMessage());
        }
    }

    public PatientDrugsData convertPatientDrugsToData(PatientDrugs patientDrugs) {
        return modelMapper.map(patientDrugs, PatientDrugsData.class);
    }

    public PatientDrugs convertDataToPatientDrugs(PatientDrugsData patientDrugsData) {
        return modelMapper.map(patientDrugsData, PatientDrugs.class);
    }

    public List<PatientDrugsData> convertPatientDrugsToDataList(List<PatientDrugs> patientDrugs) {
        Type listType = new TypeToken<List<PatientDrugsData>>() {
        }.getType();
        return modelMapper.map(patientDrugs, listType);
    }

    public List<PatientDrugs> convertDataToPatientDrugsList(List<PatientDrugsData> patientDrugsData) {
        Type listType = new TypeToken<List<PatientDrugs>>() {
        }.getType();
        return modelMapper.map(patientDrugsData, listType);
    }

}
