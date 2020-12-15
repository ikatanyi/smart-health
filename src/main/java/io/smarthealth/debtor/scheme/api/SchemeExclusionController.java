/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.api;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.smarthealth.debtor.scheme.service.SchemeExclusionService;
import io.smarthealth.debtor.scheme.data.SchemeExclusionData;
import io.smarthealth.debtor.scheme.domain.SchemeExclusions;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.Pager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.Value;
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
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/")
public class SchemeExclusionController {

    private final SchemeExclusionService service;

    public SchemeExclusionController(SchemeExclusionService service) {
        this.service = service;
    }

    @PostMapping("/scheme-exclusions")
    public ResponseEntity<List<SchemeExclusionData>> createExclusion(@Valid @RequestBody List<SchemeExclusionData> data) {
        List<SchemeExclusions> results = service.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                results.stream()
                        .map(SchemeExclusionData::map)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/scheme-exclusions/{id}")
    public ResponseEntity<SchemeExclusionData> get(@PathVariable(value = "id") Long id) {
        SchemeExclusions results = service.get(id)
                .orElseThrow(() -> APIException.notFound("Scheme Exclusion with ID {0} Not Found", id));
        return ResponseEntity.ok(SchemeExclusionData.map(results));
    }

    @PutMapping("/scheme-exclusions/{id}")
    public ResponseEntity<SchemeExclusionData> update(@PathVariable(value = "id") Long id, @Valid @RequestBody SchemeExclusionData data) {
        SchemeExclusions results = service.update(id, data);
        return ResponseEntity.ok(SchemeExclusionData.map(results));
    }

    @DeleteMapping("/scheme-exclusions/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        service.delete(id);
        return ResponseEntity.accepted().body(new Delete("Success", "Exclusion Deleted Successful"));
        
    }

    @GetMapping("/scheme-exclusions")
    public ResponseEntity<?> gets(
            @RequestParam(value = "itemId", required = false) Long itemId,
            @RequestParam(value = "schemeId", required = false) Long schemeId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<SchemeExclusionData> list = service.get(itemId, schemeId, pageable)
                .map(SchemeExclusionData::map);

        return ResponseEntity.ok((Pager<SchemeExclusionData>) PaginationUtil.toPager(list, "Scheme Exclusions"));
    }

    @GetMapping("/scheme-exclusions/isExclusion")
    public ResponseEntity<?> isExclusion(
            @RequestParam(value = "itemId", required = true) Long itemId,
            @RequestParam(value = "schemeId", required = true) Long schemeId) {
        Optional<SchemeExclusions> exc = service.get(itemId, schemeId);

        return ResponseEntity.ok(exc.isPresent());
    }
//    @GetMapping("/scheme-exclusions/{sche}/list")
//    public ResponseEntity<List<SchemeExclusionData>> getByItem(@PathVariable(value = "itemId") Long id) {
//        List<SchemeExclusionData> list = service.getByItem(id).stream()
//                .map(SchemeExclusionData::map)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(list);
//    }

    @Value
    public class Delete {

        String status;
        String message;
    }
}
