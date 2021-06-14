/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.lang.UnsafeOkHttpClient;
import io.smarthealth.notification.data.SmsMessageData;
import io.smarthealth.notification.domain.SMSConfiguration;
import io.smarthealth.notification.domain.SmsMessage;
import io.smarthealth.notification.domain.enumeration.ReceiverType;
import io.smarthealth.notification.domain.enumeration.SMSProvider;
import io.smarthealth.notification.domain.specification.TextMessageSpecification;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.notification.domain.SmsMessageRepository;
import org.springframework.scheduling.annotation.Async;
import io.smarthealth.clinical.visit.domain.Visit;

import java.time.LocalTime;
import java.util.Arrays;

/**
 * @author kent
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsMessagingService {

    private final SmsMessageRepository messageRepository;
    private final PatientService patientService;
    private final EmployeeService employeeService;
    private final VisitRepository visitRepository;
    private final SMSConfigurationService smsConfigurationService;
    private final SmartApplicationSMSProviderService smartApplicationSMSProviderService;

    @Transactional
    public SmsMessage sendBulkSMS(SmsMessageData d) {
        SmsMessage msg = d.map();
        List<String> phoneNumbers = new ArrayList<String>();

        if (d.getReceiverType().equals(ReceiverType.AllPatients)) {
//keen on this given that some hospitals has 30thousand patients --to be used safely. to stop the process unless you stop the server completely- should have some approval level - in my view

            /*   List<Patient> patients = patientService.findAllPatients();
            for (Patient p : patients) {
                if (p.getPrimaryContact() != null) {
                    phoneNumbers.add(p.getPrimaryContact());
                }
            }*/
        }
        if (d.getReceiverType().equals(ReceiverType.AllSuppliers)) {

        }
        if (d.getReceiverType().equals(ReceiverType.DailyVisitPatient)) {
            Page<Visit> visits = visitRepository.findByStartDatetimeBetween(d.getVisitDate().atStartOfDay(), d.getVisitDate().atTime(LocalTime.MAX), Pageable.unpaged());
            System.out.println("Visits found " + visits.getContent().size());
            for (Visit v : visits.getContent()) {
                if (v.getPatient().getPrimaryContact() != null) {
                    phoneNumbers.add(v.getPatient().getPrimaryContact());
                }
            }
        }
        if (d.getReceiverType().equals(ReceiverType.SpecifiedNumbers)) {
            if (d.getPhoneNumber() != null) {
                phoneNumbers = Arrays.asList(d.getPhoneNumber().split(","));
            }
        }
        sendMultipleSMS(phoneNumbers, d.getMessage());
        return messageRepository.save(msg);
    }

    @Transactional
    public SmsMessage createTextMessage(SmsMessageData msgData) {
        SmsMessage msg = msgData.map();
        if (msgData.getPhoneNumber() == null) {
            if (msgData.getReceiverType() == ReceiverType.patient) {
                Patient patient = patientService.findPatientOrThrow(msgData.getReceiverId());
                msg.setName(patient.getFullName());
                msg.setPhoneNumber(patient.getPrimaryContact());
            } else {
                Employee employee = employeeService.fetchEmployeeByNumberOrThrow(msgData.getReceiverId());
                msg.setName(employee.getFullName());
                msg.setPhoneNumber(employee.getPrimaryContact());
            }
        }
        String status = sendSMS(msg.getPhoneNumber(), msg.getMessage());
        msg.setStatus(status);
        return messageRepository.save(msg);
    }

    public List<SmsMessage> createBatchTextMessage(List<SmsMessageData> msgDataList) {
        List<SmsMessage> msgList = new ArrayList();
        for (SmsMessageData msgData : msgDataList) {
            SmsMessage msg = msgData.map();
            if (msgData.getPhoneNumber() == null) {
                if (msgData.getReceiverType() == ReceiverType.patient) {
                    Patient patient = patientService.findPatientOrThrow(msgData.getReceiverId());
                    msg.setName(patient.getFullName());
                    msg.setPhoneNumber(patient.getPrimaryContact());
                } else {
                    Employee employee = employeeService.fetchEmployeeByNumberOrThrow(msgData.getReceiverId());
                    msg.setName(employee.getFullName());
                    msg.setPhoneNumber(employee.getPrimaryContact());
                }
            }
            String status = sendSMS(msg.getPhoneNumber(), msg.getMessage());
            msg.setStatus(status);
            msgList.add(msg);
        }
        return messageRepository.saveAll(msgList);
    }

    public Optional<SmsMessage> getTextMessage(Long id) {
        return messageRepository.findById(id);
    }

    public Page<SmsMessage> getAllTextMessage(String name, String status, String phoneNumber, ReceiverType type, DateRange range, Pageable pageable) {
        Specification<SmsMessage> spec = TextMessageSpecification.createSpecification(name, status, phoneNumber, type, range);
        return messageRepository.findAll(spec, pageable);
    }

    @Async
    public void sendMultipleSMS(List<String> phoneNumbers, String msgBody) {
        List<SmsMessage> msgList = new ArrayList();
        System.out.println("Message count " + phoneNumbers.size());
        for (String p : phoneNumbers) {
            SmsMessage msg = new SmsMessage();
            try {
                String status = sendSMS(p, msgBody);
                msg.setPhoneNumber(p);
                msg.setMessage(msgBody);
                msg.setStatus(status);
                msgList.add(msg);

                messageRepository.saveAll(msgList);
            } catch (Exception e) {
                //TODO: log any error captured for each message

            }
        }
    }

    public String sendSMS(String phone, String msg) {

        String status = null;

        if (phone == null) {
            return "";
        }

        //find sms configurations;
        List<SMSConfiguration> smsConfig = smsConfigurationService.findAllByStatus("Active");
        if (smsConfig.size() > 1) {
            throw APIException.conflict("Please mark only one sms provider as active. Multiple conflict");
        }
        SMSConfiguration activeConfig = smsConfig.get(0);
        log.info(activeConfig.getProviderName()+" to send SMS ");
        try {
            if (activeConfig.getProviderName().equals(SMSProvider.Smart)) {
                status = smartApplicationSMSProviderService.sendSingleSMS(phone, msg, activeConfig);
            } else if (activeConfig.getProviderName().equals(SMSProvider.Mobitech)) {
                log.info("Mobitech to send sms {} to {}",msg, phone);

                new MobitechGateway(activeConfig.getUsername(), activeConfig.getApiKey(),
                        activeConfig.getSenderId(),
                        activeConfig.getGatewayUrl()).sendMessage(phone, msg);
                status = "true";//this status I am not sure why Ikatanyi had to put it in the first place, we shall
                // demolish
                // it later
            } else if (activeConfig.getProviderName().equals(SMSProvider.AfricasTalkingGateway)) {

            } else {
                throw APIException.notFound("Unhandled active sms provider found");
            }
            return status;//sijui hii ni yanini, I just conformed to the initial method signature
        } catch (Exception e) {
            throw APIException.internalError(e.getMessage());
        }

    }


}
