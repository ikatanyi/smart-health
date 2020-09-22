package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.BedChargeData;
import io.smarthealth.clinical.admission.service.BedChargeService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class BedChargeController {

    private final BedChargeService service;

    @GetMapping("/bed-charge/{id}")
//    @PreAuthorize("hasAuthority('view_bed-charge')")
    public BedChargeData getBedCharge(@PathVariable(value = "id") Long code) {
        return service.getBedCharge(code).toData();
    }

    @GetMapping("/bed-charge")
//    @PreAuthorize("hasAuthority('view_bed-charge')")
    public ResponseEntity<?> getAllChargeTypes(
            @RequestParam(value = "bedTypeId", required = false) Long bedTypeId,
            @RequestParam(value = "itemName", required = false) String itemName,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<BedChargeData> list = service.fetchAllBedCharges(bedTypeId, itemName, pageable).map(u -> u.toData());

        Pager<List<BedChargeData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Charge");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @PostMapping("/bed-charge")
//     @PreAuthorize("hasAuthority('create_bed-charge')")
    public ResponseEntity<?> createChargeType(@Valid @RequestBody BedChargeData bedChargeData) {

        BedChargeData result = service.createBedCharge(bedChargeData).toData();

        Pager<BedChargeData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Charge created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @PostMapping("/bed-charge/batch")
//     @PreAuthorize("hasAuthority('create_bed-charge')")
    public ResponseEntity<?> createChargeType(@Valid @RequestBody List<BedChargeData> bedChargeData) {

        List<BedChargeData> result = service.createBatchBedCharge(bedChargeData)
                .stream()
                .map((c)->c.toData())
                .collect(Collectors.toList());

        Pager<List<BedChargeData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Charge created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @PutMapping("/bed-charge/{id}")
//    @PreAuthorize("hasAuthority('create_bedcharge')")
    public ResponseEntity<?> updateCharge(@PathVariable("id") Long id, @Valid @RequestBody BedChargeData bedTypeData) {

        BedChargeData result = service.updateBedCharge(id, bedTypeData).toData();

        Pager<BedChargeData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Charge Updated successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

}
