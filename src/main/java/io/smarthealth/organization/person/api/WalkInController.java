/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.api;

import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.person.data.WalkInData;
import io.smarthealth.organization.person.domain.WalkIn;
import io.smarthealth.organization.person.service.WalkingService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Simon.waweru
 */
@Api
@Slf4j
@RestController
@RequestMapping("/api")
public class WalkInController {

    @Autowired
    WalkingService walkInService;

    @Autowired
    SequenceService sequenceService;

    @PostMapping("/walk-in")
    public ResponseEntity<?> createWalkingPatient(@Valid @RequestBody WalkInData WalkInData) {

        WalkIn w = WalkInData.convertToWalkingEntity(WalkInData);
        w.setWalkingIdentitificationNo(sequenceService.nextNumber(SequenceType.WalkingNumber));
        WalkIn savedWalking = walkInService.createWalking(w);
        Pager<WalkInData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("WalkIn Successfully Created.");
        pagers.setContent(WalkInData.convertToWalkingData(savedWalking));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/walk-in/{WalkInNo}")
    public ResponseEntity<?> createWalkingPatient(@PathVariable("WalkInNo") final String WalkInNo) {
        WalkIn w = walkInService.fetchWalkingByWalkingNoWithNotFoundDetection(WalkInNo);
        Pager<WalkInData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("WalkIn Data");
        pagers.setContent(WalkInData.convertToWalkingData(w));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/walk-in")
    public ResponseEntity<?> fetchAllWalkingPatients(@RequestParam(required = false) MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        int pageNo = 1;
        int size = 10;
        if (queryParams.getFirst("page") != null) {
            pageNo = Integer.valueOf(queryParams.getFirst("page"));
        }
        if (queryParams.getFirst("results") != null) {
            size = Integer.valueOf(queryParams.getFirst("results"));
        }
        pageNo = pageNo - 1;
        pageable = PageRequest.of(pageNo, size, Sort.by("id").descending());
        Page<WalkInData> page = walkInService.fetchWalkingPatients(queryParams, pageable).map(p -> WalkInData.convertToWalkingData(p));
        Pager<List<WalkInData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(page.getContent());
        PageDetails details = new PageDetails();
        details.setPage(page.getNumber());
        details.setPerPage(page.getSize());
        details.setTotalElements(page.getTotalElements());
        details.setTotalPage(page.getTotalPages());
        details.setReportName("WalkIn Register");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
    }

}
