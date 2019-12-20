package io.smarthealth.administration.app.api;

import io.smarthealth.accounting.acc.data.v1.JournalEntry;
import io.smarthealth.accounting.acc.service.JournalSender;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final JournalSender queueSender;

    @GetMapping("message/{message}")
    public ResponseEntity<String> publish(@PathVariable("message") final String message) {
        JournalEntry entry=new JournalEntry();
        entry.setNote(message);
        queueSender.postJournal(entry);

        return new ResponseEntity(message, HttpStatus.OK);
    }
}
