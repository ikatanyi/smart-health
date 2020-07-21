/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.payer.service;

import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.service.AccountService;
import io.smarthealth.accounting.pricelist.domain.PriceBook;
import io.smarthealth.accounting.pricelist.service.PricebookService;
import io.smarthealth.administration.banks.domain.BankBranch;
import io.smarthealth.administration.banks.service.BankService;
import io.smarthealth.administration.finances.domain.PaymentTerms;
import io.smarthealth.administration.finances.service.PaymentTermsService;
import io.smarthealth.debtor.payer.data.BatchPayerData;
import io.smarthealth.debtor.payer.data.PayerStatement;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.PayerRepository;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.domain.enumeration.Type;
import io.smarthealth.debtor.payer.domain.specification.PayerSpecification;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import io.smarthealth.debtor.scheme.domain.enumeration.DiscountType;
import io.smarthealth.debtor.scheme.domain.enumeration.PolicyCover;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class PayerService {

    /*
    1. Create payer
    2. Fetch all payers
    3. Update payers details
    4. Remove/Delete payer
     */
    private final PayerRepository payerRepository;
    private final SchemeService schemeService;
    private final SequenceNumberService sequenceNumberService;
    private final AccountService accountService;
    private final BankService bankService;
    private final PaymentTermsService paymentTermsService;

    private final PricebookService pricebookService;

    @Transactional
    public Payer createPayer(Payer payer) {
        return payerRepository.save(payer);
    }

    public Page<Payer> fetchPayers(final String term, final Pageable pageable) {
        Specification<Payer> spec = PayerSpecification.createPayerSpecification(term);
        return payerRepository.findAll(spec, pageable);
    }

    public Payer updatePayer(Payer payer) {
        return payerRepository.save(payer);
    }

    public boolean removePayer(Payer payer) {
        payerRepository.deleteById(payer.getId());
        return true;
    }

    public Payer findPayerByIdWithNotFoundDetection(final Long payerId) {
        return payerRepository.findById(payerId).orElseThrow(() -> APIException.notFound("Payer identified by id {0} no available", payerId));
    }

    public void BatchUpload(List<BatchPayerData> batchPayerData) {
        int i = 0;
        for (BatchPayerData data : batchPayerData) {
            Payer payer = payerRepository.findByPayerName(data.getPayerName());
            if (payer == null) {
                payer = new Payer();
                if (data.getBankBranchCode() != null) {
                    BankBranch bankBranch = bankService.fetchBankBranchByCode(data.getBankBranchCode());
                    payer.setBankBranch(bankBranch);
                }
                if (data.getLedgerAccountCode() != null) {
                    Account debitAccount = accountService.findByAccountNumberOrThrow(data.getLedgerAccountCode());
                    payer.setDebitAccount(debitAccount);
                }
                if (data.getPaymentTermName() != null) {
                    PaymentTerms paymentTerms = paymentTermsService.getPaymentTermByNameWithFailDetection(data.getPaymentTermName());
                    payer.setPaymentTerms(paymentTerms);
                }
                if (data.getPriceBookName() != null) {
                    PriceBook priceBook = pricebookService.getPricebookByNameOrThrowError(data.getPriceBookName());
                    payer.setPriceBook(priceBook);
                }
//                if(data.getPrimaryContact()!=null){
//                    payer.set
//                }

                payer.setInsurance(data.isInsurance());
                payer.setLegalName(data.getLegalName());
                payer.setPayerName(data.getPayerName());
                payer.setPayerType(PayerTypeToEnum(data.getPayerType()));
                payer.setWebsite(data.getWebsite());
                payer.setPayerCode(data.getPayerCode());
                payer = this.createPayer(payer);
            }
            Scheme scheme = new Scheme();
            Optional<Scheme> savedScheme = schemeService.fetchSchemeBySchemeName(data.getSchemeName());
            if (!savedScheme.isPresent()) {
                scheme.setActive(Boolean.TRUE);
                scheme.setCover(PolicyCoverToEnum(data.getCover()));
                if (data.getSchemeCode() == null) {
                    scheme.setSchemeCode(sequenceNumberService.next(1L, Sequences.SchemeCode.name()));
                } else {
                    scheme.setSchemeCode(data.getSchemeCode());
                }
                scheme.setSchemeName(data.getSchemeName());
                scheme.setType(Scheme.SchemeType.Corporate);
                scheme.setPayer(payer);
                scheme = schemeService.createScheme(scheme);

                SchemeConfigurations sconfig = new SchemeConfigurations();
                sconfig.setCoPayType(CopayTypeToEnum(data.getCoPayType()));
                sconfig.setCoPayValue(data.getCoPayValue());
                sconfig.setDiscountMethod(DiscountTypeToEnum(data.getDiscountMethod()));
                sconfig.setDiscountValue(data.getDiscountValue());
                sconfig.setScheme(scheme);
                sconfig.setSmartEnabled(data.getSmartEnabled());
                sconfig.setStatus(true);
                schemeService.updateSchemeConfigurations(sconfig);
            }
            System.out.println(++i + " " + data.getSchemeName() + "uploaded successfully");

        }

        System.out.println(i + " :insurances uploaded successfully");
    }

    private Type PayerTypeToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return Type.Business;
        }
        if (EnumUtils.isValidEnum(Type.class, status)) {
            return Type.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid Account Type");
    }

    private CoPayType CopayTypeToEnum(String copayType) {
        if (copayType == null || copayType.equals("null") || copayType.equals("")) {
            return CoPayType.Percentage;
        }
        if (EnumUtils.isValidEnum(CoPayType.class, copayType)) {
            return CoPayType.valueOf(copayType);
        }
        throw APIException.internalError("Provide a Valid Copay Type");
    }

    private DiscountType DiscountTypeToEnum(String discountType) {
        if (discountType == null || discountType.equals("null") || discountType.equals("")) {
            return DiscountType.Percentage;
        }
        if (EnumUtils.isValidEnum(DiscountType.class, discountType)) {
            return DiscountType.valueOf(discountType);
        }
        throw APIException.internalError("Provide a Valid Dicount Type");
    }

    private PolicyCover PolicyCoverToEnum(String policyCover) {
        if (policyCover == null || policyCover.equals("null") || policyCover.equals("")) {
            return PolicyCover.Both;
        }
        if (EnumUtils.isValidEnum(PolicyCover.class, policyCover)) {
            return PolicyCover.valueOf(policyCover);
        }
        throw APIException.internalError("Provide a Valid PolicyCover Type");
    }

    public List<PayerStatement> getStatement(Long payerId, DateRange range) {
        List<PayerStatement> lists = payerRepository.getPayerStatement(payerId, range);
        return lists;
    }
}
