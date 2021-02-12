package io.smarthealth.integration.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.accounting.payment.domain.Copayment;
import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import io.smarthealth.clinical.record.service.DiagnosisService;
import io.smarthealth.integration.data.ClaimFileData;
import io.smarthealth.integration.data.ServiceData;
import io.smarthealth.integration.metadata.CardData.*;
import io.smarthealth.integration.metadata.PatientData.Claim;
import io.smarthealth.integration.metadata.PatientData.Diagnosis;
import io.smarthealth.integration.metadata.PatientData.Root;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;


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
    private final InvoiceService invoiceService;
    private final DiagnosisService diagnosisService;

    public ClientResponse create(ClaimFileData data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Patient patient = patientService.findPatientOrThrow(data.getMemberNumber());
        Facility facility = facilityService.loggedFacility();
        CardData cardData = findByPatientId(data.getMemberNumber());
        Invoice invoice = invoiceService.getInvoiceByNumberOrThrow(data.getInvoiceNumber());
        Root root = new Root();
        List<Copayment> copays = invoice.getCopays();

        List<PatientDiagnosis> diagnosis = diagnosisService.fetchAllDiagnosis(invoice.getVisit().getVisitNumber(),null,null, Pageable.unpaged()).getContent();
        String diagString = "[";
        for(PatientDiagnosis diagn:diagnosis){
            diagString.concat(diagn.getDiagnosis().getCode()).concat(",");
        }
        diagString.concat("]");
        for(Copayment copay:copays){
            ServiceData service = new ServiceData();
            service.setCodeDescription("Copay");
            service.setCode("Copay");
            service.setEncounterType("Consultation");
            service.setTotalAmount(copay.getAmount().doubleValue());
            service.getDiagnosis().setCode(diagString);
            data.getServices().add(service);
        }
        Claim claim = data.map(cardData);
        claim.getPatient().setDateOfBBirth(patient.getDateOfBirth().format(DateTimeFormatter.ISO_DATE));
        claim.getPatient().setFirstName(patient.getGivenName());
        claim.getPatient().setSurname(patient.getSurname());
        claim.getPatient().setMiddleName(patient.getMiddleName());
        claim.getProvider().setGroupPracticeName(facility.getFacilityName());
        root.setClaim(claim);


        //Object to JSON in String
        String jsonInString = mapper.writeValueAsString(root);
        ClientResponse response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                .path("/submitinvoice")
                .queryParam("patientid", data.getMemberNumber())
                .queryParam("globalid", cardData.getMedicalAid().getGlobalId())
                .build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonInString))
                .exchange()
                .block();

        return response;

    }

    public CardData findByPatientId(String patientid) {
        ObjectMapper mapper = new ObjectMapper();
        TypedMap response = webClient.get()
                .uri("/getmemberprofile?patientid=" + patientid)
                //                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(TypedMap.class)
                .blockFirst();
        Map<String, Object> admissionInfo = (LinkedHashMap) response.get("AdmissionInformation");
        return mapper.convertValue(admissionInfo, CardData.class);

    }

    private static class TypedMap extends HashMap<String, Object> {
    }

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
//    
}
