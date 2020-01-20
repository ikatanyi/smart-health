/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.member.api;

import io.smarthealth.debtor.member.data.PayerMemberData;
import io.smarthealth.debtor.member.domain.PayerMember;
import io.smarthealth.debtor.member.service.PayerMemberService;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author simz
 */
@RestController
@Api
@RequestMapping("/api")
public class PayerMemberShipController {

    @Autowired
    PayerMemberService payerMemberService;

    @Autowired
    SchemeService schemeService;

    @GetMapping("/scheme/{id}/scheme-member")
    public ResponseEntity<?> fetchMembersByScheme(@PathVariable("id") final Long schemeId, Pageable pageable) {
        Scheme scheme = schemeService.fetchSchemeById(schemeId);

        Page<PayerMemberData> page = payerMemberService.fetchPayerMemberByScheme(scheme, pageable).map(m -> PayerMemberData.map(m));

        Pager<List<PayerMemberData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(page.getContent());
        PageDetails details = new PageDetails();
        details.setPage(page.getNumber());
        details.setPerPage(page.getSize());
        details.setTotalElements(page.getTotalElements());
        details.setTotalPage(page.getTotalPages());
        details.setReportName("Patient Register");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @PostMapping("/scheme/{id}/scheme-member")
    public ResponseEntity<?> addMemberToAScheme(@PathVariable("id") final Long schemeId, @Valid @RequestBody PayerMemberData data) {
        Scheme scheme = schemeService.fetchSchemeById(schemeId);

        PayerMember member = PayerMemberData.map(data);
        member.setScheme(scheme);
        PayerMember savedMember = payerMemberService.createNewMember(member);

        Pager<PayerMemberData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(PayerMemberData.map(savedMember));
        PageDetails details = new PageDetails();
        details.setReportName("Member Details");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/scheme-member/{policyNo}")
    public ResponseEntity<?> fetchMemberByPolicyNo(/*@PathVariable("id") final Long schemeId,*/@PathVariable("policyNo") String policyNo) {
        PayerMember pm = payerMemberService.fetchMemberByPolicyNo(policyNo);
        PayerMemberData member = PayerMemberData.map(pm);
        Pager<PayerMemberData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(member);
        PageDetails details = new PageDetails();
        details.setReportName("Member Details");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
}
