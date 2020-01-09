/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.api;

import io.smarthealth.debtor.payer.data.PayerData;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.debtor.scheme.data.InsuranceSchemeData;
import io.smarthealth.debtor.scheme.domain.InsuranceScheme;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author Simon.Waweru
 */
@Api
@RestController
@RequestMapping("/api")
public class SchemeController {

    @Autowired
    PayerService payerService;

    @Autowired
    SchemeService schemeService;

    @PostMapping("/scheme")
    public ResponseEntity<?> createScheme(@Valid @RequestBody InsuranceSchemeData scheme) {
        //validate Payer
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(scheme.getPayerId());

        Scheme s = InsuranceSchemeData.map(scheme);
        s.setPayer(payer);
        Scheme savedScheme = schemeService.createScheme(s);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/scheme/{id}")
                .buildAndExpand(s.getId()).toUri();

        InsuranceSchemeData data = InsuranceSchemeData.map(savedScheme);

        return ResponseEntity.created(location).body(data);
    }

    @GetMapping("/scheme")
    public ResponseEntity<?> fetchAllSchemes(Pageable pageable) {
        Page<InsuranceSchemeData> scheme = schemeService.fetchSchemes(pageable).map(p -> InsuranceSchemeData.map(p));

        Pager<List<InsuranceSchemeData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(scheme.getContent());
        PageDetails details = new PageDetails();
        details.setPage(scheme.getNumber() + 1);
        details.setPerPage(scheme.getSize());
        details.setTotalElements(scheme.getTotalElements());
        details.setTotalPage(scheme.getTotalPages());
        details.setReportName("Scheme List");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/scheme/{id}")
    public ResponseEntity<?> fetchSchemeById(@PathVariable("id") final Long schemeId) {
        InsuranceSchemeData schemeData = InsuranceSchemeData.map(schemeService.fetchSchemeById(schemeId));

        return ResponseEntity.ok(schemeData);
    }

}
