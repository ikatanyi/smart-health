package io.smarthealth.integration.service;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smarthealth.integration.data.ClaimFileData;
import io.smarthealth.integration.metadata.CardData.*;
import io.smarthealth.integration.metadata.PatientData.Claim;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class IntegrationService {
    private final WebClient webClient;
    private final FacilityService facilityService;
    private final PatientService patientService;

    public Mono<Claim> create(ClaimFileData data) {
        Patient patient = patientService.findPatientOrThrow(data.getMemberNumber());
        Facility facility = facilityService.loggedFacility();
        CardData cardData = findByPatientId(data.getMemberNumber());
        Claim claim = data.map(cardData);
        claim.getPatient().setDateOfBBirth(patient.getDateOfBirth().format(DateTimeFormatter.ISO_DATE));
        claim.getPatient().setFirstName(patient.getGivenName());
        claim.getPatient().setSurname(patient.getSurname());
        claim.getPatient().setMiddleName(patient.getMiddleName());
        claim.getProvider().setGroupPracticeName(facility.getFacilityName());
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                .path("/submitinvoice/")
                .queryParam("patientid", data.getMemberNumber())
                .queryParam("Globalid", cardData.getMedicalAid().getGlobalId())
                .build())
                .body(Mono.just(claim), Claim.class)
                .retrieve()
                .bodyToMono(Claim.class)
                .timeout(Duration.ofMillis(10_000));
    }

    
    public CardData findByPatientId(String patientid) {
        ObjectMapper mapper = new ObjectMapper();
       TypedMap response = webClient.get()
                .uri("/getmemberprofile?patientid=" + patientid)
//                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(TypedMap.class)
               .blockFirst();
       Map<String, Object> admissionInfo = (LinkedHashMap)response.get("AdmissionInformation");
       return mapper.convertValue(admissionInfo, CardData.class);

    }
    
    private static class TypedMap extends HashMap<String, Object>{}

//    public Mono<Employee> update(Employee e) {
//        return webClient.put()
//                .uri("/employees/" + e.getId())
//                .body(Mono.just(e), Employee.class)
//                .retrieve()
//                .bodyToMono(Employee.class);
//    }
//
//    public Mono<Void> delete(Integer id) {
//        return webClient.delete()
//                .uri("/employees/" + id)
//                .retrieve()
//                .bodyToMono(Void.class);
//    }
}
