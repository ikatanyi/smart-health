/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.service;

import com.machinezoo.sourceafis.FingerprintTemplate;
import io.smarthealth.infrastructure.common.BiometricUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.person.data.PersonBiometricRecord;
import io.smarthealth.organization.person.domain.Biometrics;
import io.smarthealth.organization.person.domain.BiometricsRepository;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.Waweru
 */
@Service
public class BiometricService {

    @Autowired
    BiometricsRepository biometricsRepository;

    private List<Biometrics> listOfRecords;

    public Biometrics addBiometric(Biometrics biometric) {
        //verify if finger is already registered
        String success = verifyPerson(biometric.getData());
        if (success != null) {
            throw APIException.conflict("The fingerprint provided is already registered", "");
        }
        return biometricsRepository.save(biometric);
    }

    public String verifyPatient(String fingerprint) {
        System.out.println("\n fingerprint \n " + fingerprint);
        if (fingerprint == null || fingerprint.equals("")) {
            return null;
        }
        FingerprintTemplate probe = BiometricUtil.toVerifyFingerprintTemplate(fingerprint);
        //convert this
        Iterable<PersonBiometricRecord> candidates = laodCachedFingerprints();
        PersonBiometricRecord matched = BiometricUtil.find(probe, candidates);

        return matched != null ? matched.getPersonId() : null;
    }

    public String verifyPerson(String fingerprint) {
        if (fingerprint == null || fingerprint.equals("")) {
            return null;
        }
        FingerprintTemplate probe = BiometricUtil.toVerifyFingerprintTemplate(fingerprint);
        //convert this
        Iterable<PersonBiometricRecord> candidates = laodCachedFingerprints();
        PersonBiometricRecord matched = BiometricUtil.find(probe, candidates);

        return matched != null ? matched.getPersonId() : null;
    }

    private Iterable<PersonBiometricRecord> laodCachedFingerprints() {
        listOfRecords = biometricsRepository.findAll();
        List<PersonBiometricRecord> records = new LinkedList<>();

        listOfRecords.stream().map((biometric) -> {
            PersonBiometricRecord record = new PersonBiometricRecord();
            record.setPersonId(biometric.getPerson().getId().toString());
            record.setTemplate(BiometricUtil.jsonToFingerPrint(biometric.getTemplate()));
            return record;
        }).forEachOrdered((record) -> {
            records.add(record);
        });
        return records;
    }

}
