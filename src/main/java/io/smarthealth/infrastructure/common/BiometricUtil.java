/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.common;

import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import io.smarthealth.organization.person.data.PersonBiometricRecord;

import java.util.Base64;

/**
 *
 * @author Kelsas
 */
public class BiometricUtil {

    public static String toCacheFingerprintJson(String fingerImage) {
        byte[] image = Base64.getDecoder().decode(fingerImage);
        FingerprintTemplate template = new FingerprintTemplate()
                .dpi(500)
                .create(image);
        String json = template.serialize();
        return json;
    }

    public static FingerprintTemplate toVerifyFingerprintTemplate(String fingerImage) {
        byte[] image = Base64.getDecoder().decode(fingerImage);
        FingerprintTemplate template = new FingerprintTemplate()
                .dpi(500)
                .create(image);
        return template;
    }

    public static FingerprintTemplate jsonToFingerPrint(String json) {
        FingerprintTemplate template = new FingerprintTemplate()
                .deserialize(json);
        return template;
    }

    public static PersonBiometricRecord find(FingerprintTemplate probe, Iterable<PersonBiometricRecord> candidates) {
        FingerprintMatcher matcher = new FingerprintMatcher()
                .index(probe);
        PersonBiometricRecord match = null;
        double high = 0;
        for (PersonBiometricRecord candidate : candidates) {
            double score = matcher.match(candidate.getTemplate());
            if (score > high) {
                high = score;
                match = candidate;
            }
        }
        double threshold = 40;
        return high >= threshold ? match : null;
    }
}
