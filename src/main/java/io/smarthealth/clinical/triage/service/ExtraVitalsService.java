package io.smarthealth.clinical.triage.service;

import io.smarthealth.clinical.triage.data.ExtraVitalFieldData;
import io.smarthealth.clinical.triage.domain.ExtraVitalField;
import io.smarthealth.clinical.triage.domain.ExtraVitalFieldRepository;
import io.smarthealth.infrastructure.exception.APIException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExtraVitalsService {
    private final ExtraVitalFieldRepository extraVitalFieldRepository;


    @Transactional
    public ExtraVitalField saveVitalField(ExtraVitalFieldData fieldData) {
        ExtraVitalField e = ExtraVitalFieldData.map(fieldData);
        return extraVitalFieldRepository.save(e);
    }

    public ExtraVitalField findVitalFieldByName(String name) {
        return extraVitalFieldRepository.findByName(name).orElseThrow(() -> APIException.notFound("Vital Field Identified by {0} not found ", name));
    }

    public List<ExtraVitalField> findAllVitalFields() {
        return extraVitalFieldRepository.findAll();
    }

}
