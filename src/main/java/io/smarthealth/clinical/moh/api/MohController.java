package io.smarthealth.clinical.moh.api;

import io.smarthealth.clinical.moh.data.MohData;
import io.smarthealth.clinical.moh.domain.Moh;
import io.smarthealth.clinical.moh.service.MohService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api")
public class MohController {

    private final MohService service;

    public MohController(MohService itemService) {
        this.service = itemService;
    }

    @GetMapping("/moh/{id}")
//    @PreAuthorize("hasAuthority('view_moh')")
    public Moh getMoh(@PathVariable(value = "id") Long code) {
        Moh moh = service.fetchMohById(code);
        return moh;
    }

    @GetMapping("/moh")
//    @PreAuthorize("hasAuthority('view_moh')")
    public ResponseEntity<?> getAllMohs(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<MohData> list = service.fetchAllMoh(pageable).map(u -> u.toData());

        Pager<List<MohData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Unit of Measure");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @PostMapping("/moh")
//    @PreAuthorize("hasAuthority('create_moh')")
    public ResponseEntity<?> createMoh(@Valid @RequestBody MohData mohData) {

        MohData result = service.createMoh(mohData).toData();

        Pager<MohData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Moh created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @PostMapping("/moh/Batch")
//    @PreAuthorize("hasAuthority('create_moh')")
    public ResponseEntity<?> createBatchMoh(@Valid @RequestBody List<MohData> mohData) {

        List<MohData> result = service.createBatchMoh(mohData)
                .stream()
                .map(u -> u.toData())
                .collect(Collectors.toList());

        Pager<List<MohData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Moh created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @PutMapping("/moh/{id}")
//    @PreAuthorize("hasAuthority('create_moh')")
    public ResponseEntity<?> updateMoh(@PathVariable("id") Long id, @Valid @RequestBody MohData mohData) {

        MohData result = service.updateMoh(id, mohData).toData();

        Pager<MohData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Moh created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

}
