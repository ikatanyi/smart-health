package io.smarthealth.infrastructure.sequence.numbers.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.sequence.numbers.data.SequenceNumberFormatData;
import io.smarthealth.infrastructure.sequence.numbers.domain.EntitySequenceType;
import io.smarthealth.infrastructure.sequence.numbers.domain.SequenceNumberFormat;
import io.smarthealth.infrastructure.sequence.numbers.service.SequenceGeneratorService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Deprecated
@RestController
@Slf4j
@RequestMapping("/api")
public class SequenceGeneratorController {

    private final SequenceGeneratorService service;

    public SequenceGeneratorController(SequenceGeneratorService service) {
        this.service = service;
    }

    @PostMapping("/sequences")
    public ResponseEntity<?> createPricebook(@Valid @RequestBody SequenceNumberFormatData data) {
        if (service.getSequenceFormatByType(data.getSequenceType()).isPresent()) {
            throw APIException.conflict("Sequence Format with name {0} already exists.", data.getSequenceType().getCode());
        }

        SequenceNumberFormat result = service.createSequenceFormat(data);

        Pager<SequenceNumberFormatData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Sequence Format created successful");
        pagers.setContent(result.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/sequences/{id}")
    public SequenceNumberFormatData getSequenceFormat(@PathVariable(value = "id") Long code) {
        SequenceNumberFormat format = service.getSequenceFormatOrThrow(code);
        return format.toData();
    }

    @GetMapping("/sequences/{id}/name")
    public SequenceNumberFormatData getSequenceFormatByName(@PathVariable(value = "id") String code) {
        SequenceNumberFormat format = service.getSequenceFormatByTypeOrThrow(EntitySequenceType.valueOf(code));
        return format.toData();
    }

    @GetMapping("/sequences")
    public ResponseEntity<?> getAllSequences(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<SequenceNumberFormatData> list = service.getSequenceFormats(pageable).map(d -> d.toData());

        Pager<List<SequenceNumberFormatData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Sequences");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
}
