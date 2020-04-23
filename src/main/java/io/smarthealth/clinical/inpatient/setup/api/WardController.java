/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.inpatient.setup.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.clinical.inpatient.setup.data.WardData;
import io.smarthealth.clinical.inpatient.setup.domain.Ward;
import io.smarthealth.clinical.inpatient.setup.service.WardService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class WardController {

    private final WardService service;

    public WardController(WardService service) {
        this.service = service;
    }

    @PostMapping("/wards")
    @ResponseBody
//    @PreAuthorize("hasAuthority('create_ward')")
    public ResponseEntity<?> createWard(@RequestBody @Valid final WardData data) {
        Ward ward = service.createWard(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(ward.toData());
    }

    @GetMapping("/wards/{id}")
//    @PreAuthorize("hasAuthority('view_ward')")
    public ResponseEntity<?> getWard(@PathVariable(value = "id") Long id) {
        Ward ward = service.getWardOrThrow(id);
        return ResponseEntity.ok(ward.toData());
    }

    @GetMapping("/wards")
//    @PreAuthorize("hasAuthority('view_ward')")
    public ResponseEntity<?> getWards(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createUnPaged(page, size);

        Page<WardData> list = service.getWards(pageable).map(x -> x.toData());

        Pager<List<WardData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Ward lists");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    //api/wards - PUT
    @PutMapping("/wards/{id}")
    @ResponseBody
//    @PreAuthorize("hasAuthority('update_ward')")
    public ResponseEntity<?> updateWard(@PathVariable(value = "id") Long id, WardData data) {
        Ward ward = service.updateWard(id, data);
        return ResponseEntity.ok(ward.toData());
    }

    //api/wards - Delete
    @DeleteMapping("/wards/{id}")
//    @PreAuthorize("hasAuthority('delete_ward')")
    public ResponseEntity<?> deleteWard(@PathVariable(value = "id") Long id) {
        service.deleteWard(id);
        return ResponseEntity.ok().build();
    }
}
