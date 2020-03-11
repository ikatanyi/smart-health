package io.smarthealth.debtor.claim.creditNote.api;

import io.smarthealth.debtor.claim.creditNote.data.CreditNoteData;
import io.smarthealth.debtor.claim.creditNote.service.CreditNoteService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Api
@RestController
@RequestMapping("/api/")
public class CreditNoteController {

    private final CreditNoteService creditNoteService;

    public CreditNoteController(CreditNoteService creditNoteService) {
        this.creditNoteService = creditNoteService;
    }

    

    
    @PostMapping("/credit-note")
    public ResponseEntity<?> createCreditNote(@Valid @RequestBody CreditNoteData creditNoteData) {

        CreditNoteData creditNote = creditNoteService.createCreditNote(creditNoteData).toData();

        Pager<CreditNoteData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Credit Note successfully Created.");
        pagers.setContent(creditNote);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/credit-note/{id}")
    public CreditNoteData getCreditNote(@PathVariable(value = "id") Long id) {
        CreditNoteData creditNote = creditNoteService.getCreditNoteByIdWithFailDetection(id).toData();
        return creditNote;
    }

    @PutMapping("/credit-note/{id}")
    public CreditNoteData updateRemitance(@PathVariable(value = "id") Long id, CreditNoteData creditNoteData) {
        CreditNoteData creditNote = creditNoteService.updateCreditNote(id, creditNoteData).toData();
        return creditNote;
    }

    @GetMapping("/credit-note")
    public ResponseEntity<?> getAllCreditNotes(
            @RequestParam(value = "invoiceNo", required = false) String invoiceNo,
            @RequestParam(value = "payerId", required = false) Long payerId,     
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<CreditNoteData> list = creditNoteService.getCreditNotes(invoiceNo,  payerId, range, pageable)
                .map(crnote -> crnote.toData());

        Pager<List<CreditNoteData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPerPage(list.getSize());
        details.setPage(list.getNumber() + 1);
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Credit Notes");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }
}
