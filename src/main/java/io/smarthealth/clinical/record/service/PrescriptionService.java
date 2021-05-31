/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.service;

import io.smarthealth.accounting.pricelist.domain.PriceBook;
import io.smarthealth.accounting.pricelist.domain.PriceList;
import io.smarthealth.accounting.pricelist.service.PricelistService;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.domain.Prescription;
import io.smarthealth.clinical.record.domain.PrescriptionRepository;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.domain.PaymentDetailsRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.organization.person.patient.service.PatientService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Simon.Waweru
 */
@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientService patientService;
    private final ServicePointService servicePointService;
    private final PricelistService pricelistService;
    private final PaymentDetailsRepository paymentDetailsRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, PatientService patientService,
                               ServicePointService servicePointService, PricelistService pricelistService,
                               PaymentDetailsRepository paymentDetailsRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientService = patientService;
        this.servicePointService = servicePointService;
        this.pricelistService = pricelistService;
        this.paymentDetailsRepository = paymentDetailsRepository;
    }

    public Optional<Prescription> fetchPrescriptionById(final Long id) {
        return prescriptionRepository.findById(id);
    }

    public List<Prescription> createPrescription(final List<Prescription> prescription) {
        List<Prescription> prescriptionSaved = prescriptionRepository.saveAll(prescription);
        for (Prescription p : prescriptionSaved) {
            System.out.println("p.getVisit().getPaymentMethod() "+p.getVisit().getPaymentMethod());
            if (p.getVisit().getPaymentMethod().equals(PaymentMethod.Insurance)) {
                //reduce the temp limit
                PaymentDetails paymentDetails = p.getVisit().getPaymentDetails();
                Double requestAmount = 0.0;
                PriceBook priceBook = p.getVisit().getPaymentDetails().getPayer().getPriceBook();
                ServicePoint servicePoint = servicePointService.getServicePointByType(ServicePointType.Pharmacy);

                Page<PriceList> pd = pricelistService.getPricelistByLocation(servicePoint.getId(), priceBook.getId(),
                        p.getItem().getId(),
                        Pageable.unpaged());

                requestAmount = pd.getContent().get(0).getSellingRate().doubleValue();
                paymentDetails.setTempRunningLimit(paymentDetails.getTempRunningLimit() - requestAmount);
                paymentDetailsRepository.saveAndFlush(paymentDetails);
            }
        }
        return prescription;
    }

    public List<Prescription> fetchPrescriptionByNumber(final String orderNumber, final Visit visit) {
        return prescriptionRepository.findByOrderNumberOrVisit(orderNumber, visit);
    }

    public Page<Prescription> fetchAllPrescriptionsByVisit(final Visit visit, final Pageable pageable) {
        return prescriptionRepository.findByVisit(visit, pageable);
    }

    public Page<Prescription> fetchAllPrescriptionsByVisitAndDischarge(final Visit visit, Boolean discharged, final Pageable pageable) {
        return prescriptionRepository.findByVisitAndOnDischarge(visit, discharged, pageable);
    }
}
