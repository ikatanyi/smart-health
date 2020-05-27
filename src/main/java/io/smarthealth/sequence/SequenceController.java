package io.smarthealth.sequence;

import io.smarthealth.sequence.data.SequenceRequest;
import io.swagger.annotations.Api;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Sequence REST Controller.
 */
@Api
@RestController
public class SequenceController {

//    @Autowired
//    private Reactor reactor;
    @Autowired
    private SequenceNumberService sequenceNumberService;

//    @Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('view_sequences')")
    @GetMapping("/api/sequence/{tenant}")
    public ResponseEntity<?> list(final @PathVariable("tenant") Long tenant) {
        return ResponseEntity.ok(sequenceNumberService.getDefinitions(tenant));
    }

//    @Secured("ROLE_ADMIN")
    @PostMapping("/api/sequence/{tenant}/{name}")
    @PreAuthorize("hasAuthority('create_sequences')")
    @Transactional
    public SequenceDefinition create(final @PathVariable("tenant") Long tenant,
            final @PathVariable("name") String name,
            final @Valid @RequestBody SequenceRequest seqData) {
        String format = seqData.getFormat();
        Long number = seqData.getNumber();
        return sequenceNumberService.create(tenant, name, format != null ? format : "%d", number != null ? number : 1L);
    }

//    @Secured("ROLE_USER")
    @GetMapping(value = "/api/sequence/{tenant}/{name}")
    @PreAuthorize("hasAuthority('view_sequences')")
    @Transactional
    public SequenceStatus status(final @PathVariable("tenant") Long tenant,
            final @PathVariable("name") String name) {
        return sequenceNumberService.status(tenant, name);
    }

//    @Secured("ROLE_ADMIN")
    @PutMapping("/api/sequence/{tenant}/{name}")
    @PreAuthorize("hasAuthority('edit_sequences')")
    @Transactional
    public SequenceDefinition update(final @PathVariable("tenant") Long tenant,
            final @PathVariable("name") String name,
            final @Valid @RequestBody SequenceRequest params) {
        return sequenceNumberService.update(tenant, name, params.getFormat(), params.getCurrent(), params.getNumber());
    }

//    @Secured("ROLE_ADMIN")
    @DeleteMapping("/api/sequence/{tenant}/{name}")
    @PreAuthorize("hasAuthority('delete_sequences')")
    @Transactional
    public SequenceDefinition delete(final @PathVariable("tenant") Long tenant, final @PathVariable("name") String name) {
        return sequenceNumberService.delete(tenant, name);
    }

//    @Secured("ROLE_USER")
//    @RequestMapping(value = "/api/sequence/{tenant}/{name}/next", method = RequestMethod.GET)
//    @Transactional
//    public DeferredResult<String> next(final @PathVariable("tenant") Long tenant,
//                                       final @PathVariable("name") String name) {
//        DeferredResult<String> response = new DeferredResult<>();
//        SequenceEvent data = new SequenceEvent(tenant, name, response);
//        reactor.notify("sequence", Event.wrap(data));
//        return response;
//    }
    @GetMapping("/api/sequence/{tenant}/{name}/next")
    @PreAuthorize("hasAuthority('view_sequences')")
    @Transactional
    public ResponseEntity<?> next(final @PathVariable("tenant") Long tenant, final @PathVariable("name") String name) {
//        DeferredResult<String> response = new DeferredResult<>();
//        SequenceEvent data = new SequenceEvent(tenant, name, response);
        String number = sequenceNumberService.next(tenant, name);
//        reactor.notify("sequence", Event.wrap(data));
        return ResponseEntity.ok("{ \"number\": \"" + number + "\" }\n");
    }
    
      @GetMapping("/api/sequence/types")
    public ResponseEntity<?> getTypelist() {
        return ResponseEntity.ok(Sequences.values());
    }

}
