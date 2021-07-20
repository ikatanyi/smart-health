package io.smarthealth.integration.service;

import io.smarthealth.accounting.payment.data.CreateRemittance;
import io.smarthealth.accounting.payment.data.PayChannel;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.domain.enumeration.PayerType;
import io.smarthealth.accounting.payment.domain.enumeration.ReceiptAndPaymentMethod;
import io.smarthealth.accounting.payment.domain.enumeration.RecordType;
import io.smarthealth.accounting.payment.service.RemittanceService;
import io.smarthealth.administration.finances.domain.PaymentMethod;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyIntegration;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyIntegrationRepository;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyProvider;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.integration.config.MpesaProperties;
import io.smarthealth.integration.data.MpesaRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import io.smarthealth.integration.domain.MobileMoneyResponse;
import io.smarthealth.integration.domain.MobileMoneyResponseRepository;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.spring.web.json.Json;

/**
 * @author Kelsas
 */
@Service
@Slf4j
public class MpesaService {

    private final OAuth2RestTemplate restTemplate;
    private final MpesaProperties mpesaConfiguration;
    private final MobileMoneyResponseRepository moneyResponseRepository;
    private final RemittanceService remittanceService;
    private final MobileMoneyIntegrationRepository mobileMoneyIntegrationRepository;
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    public MpesaService(OAuth2RestTemplate restTemplate,
                        MpesaProperties mpesaConfiguration,
                        MobileMoneyResponseRepository moneyResponseRepository,
                        RemittanceService service,
                        MobileMoneyIntegrationRepository mobileMoneyIntegrationRepository,
                        PatientRepository patientRepository,
                        VisitRepository visitRepository) {
        this.restTemplate = restTemplate;
        this.mpesaConfiguration = mpesaConfiguration;
        this.moneyResponseRepository = moneyResponseRepository;
        this.remittanceService = service;
        this.mobileMoneyIntegrationRepository = mobileMoneyIntegrationRepository;
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
    }

    @Transactional
    public String initiateStkPush(String phoneNumber, BigDecimal amount) {

        MpesaRequest request = new MpesaRequest();

        request.setBusinessShortCode(mpesaConfiguration.getShortCode());
        request.setPassword(mpesaConfiguration.getPassword());
        request.setTimestamp(mpesaConfiguration.getTimestamp());
        request.setTransactionType(mpesaConfiguration.getTransactionType());
        request.setAmount(amount.toPlainString());
        request.setPartyA(phoneNumber);
        request.setPartyB(mpesaConfiguration.getShortCode());
        request.setPhoneNumber(phoneNumber);
        request.setCallBackURL(mpesaConfiguration.getCallbackUrl());
        request.setAccountReference("PT-00800-20");
        request.setTransactionDesc("Medical Bill");

        String url = mpesaConfiguration.getBaseUri() + "/stkpush/v1/processrequest";

        log.info("Response: {}", restTemplate.postForEntity(url, request, String.class));

        return request.toString();
    }


    @Transactional
    public MobileMoneyResponse saveMobileMoneyResponse(String response) {
        String transactionType;
        String transID;
        String transTime;
        String transAmount;
        String businessShortCode;
        String billRefNumber;
        String invoiceNumber;
        String orgAccountBalance;
        String phoneNo;
        String firstName;
        String middleName;
        String lastName;
        MobileMoneyResponse responseObj = new MobileMoneyResponse();
        try {
            JSONObject j = new JSONObject(response);
            transactionType = j.getString("TransactionType");
            transID = j.getString("TransID");
            transTime = j.getString("TransTime");
            transAmount = j.getString("TransAmount");
            businessShortCode = j.getString("BusinessShortCode");
            billRefNumber = j.getString("BillRefNumber");
            invoiceNumber = j.getString("InvoiceNumber");
            orgAccountBalance = j.getString("OrgAccountBalance");
            phoneNo = j.getString("MSISDN");
            firstName = j.getString("FirstName");
            middleName = j.getString("MiddleName");
            lastName = j.getString("LastName");

            responseObj.setBillRefNumber(billRefNumber);
            responseObj.setBusinessShortCode(businessShortCode);
            responseObj.setFirstName(firstName);
            responseObj.setInvoiceNumber(invoiceNumber);
            responseObj.setMiddleName(middleName);
            responseObj.setOrgAccountBalance(orgAccountBalance);
            responseObj.setPhoneNo(phoneNo);
            responseObj.setTransAmount(transAmount);
            responseObj.setTransID(transID);
            responseObj.setTransTime(transTime);
            responseObj.setTransactionType(transactionType);
            responseObj.setPatientBillEffected(Boolean.FALSE);
            responseObj.setProvider(MobileMoneyProvider.Safaricom);

            MobileMoneyResponse sr = moneyResponseRepository.save(responseObj);

            //Receipt automatically
            //BillRefNumber should be patientnumber
            try {
                //find latest visit by patient
                Optional<Patient> patient = patientRepository.findByPatientNumber(billRefNumber);
                Visit visit = null;
                if (patient.isPresent()) {
                    //find latest visit by patient number
                    Optional<Visit> visitOp = visitRepository.findTopByPatient(patient.get());
                    if (visitOp.isPresent()) {
                        visit = visitOp.get();
                    } else {
                        return sr;
                    }
                } else {
                    //find visit by bill ref number
                    Optional<Visit> visitOptional = visitRepository.findByVisitNumber(billRefNumber);
                    if (visitOptional.isPresent()) {
                        visit = visitOptional.get();
                    } else {
                        return sr;
                    }
                }

                if (visit == null) {
                    return sr;
                }

                CreateRemittance data = new CreateRemittance();
                data.setAmount(BigDecimal.valueOf(Double.valueOf(sr.getTransAmount())));
                data.setCurrency("KES");
                data.setDate(LocalDate.now());
                data.setNotes("Mpesa auto bill offset");
                data.setBankCharge(BigDecimal.ZERO);
                data.setPayerType(PayerType.Patient);

                //if safaricom
                MobileMoneyIntegration mmi = mobileMoneyIntegrationRepository.findByMobileMoneyName(MobileMoneyProvider.Safaricom).get();

                PayChannel channel = new PayChannel();
                channel.setType(PayChannel.Type.Mobile);
                channel.setAccountId(mmi.getCashAccount().getId());
                channel.setAccountName(mmi.getCashAccount().getName());
                channel.setAccountNumber(mmi.getCashAccount().getIdentifier());

                data.setPaymentChannel(channel);

                data.setRecordType(RecordType.Payment);
                data.setReceivedFrom(firstName.concat(" ").concat(lastName));
                data.setPaymentMethod(ReceiptAndPaymentMethod.Mobile_Money);
                data.setReferenceNumber(transID);
                data.setVisitNumber(visit.getVisitNumber());
                data.setPayerName(firstName.concat(" ").concat(lastName));

                Receipt remittance = remittanceService.createRemittance(data);

                sr.setPatientBillEffected(Boolean.TRUE);

                sr = moneyResponseRepository.save(sr);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return sr;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error occurred while processing request");
        }
    }


}
