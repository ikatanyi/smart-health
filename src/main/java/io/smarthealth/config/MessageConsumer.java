package io.smarthealth.config;

import io.smarthealth.accounting.acc.service.FinancialActivityAccountService;
import io.smarthealth.accounting.acc.service.JournalEntryService;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Component
@EnableJms
public class MessageConsumer {

    private FinancialActivityAccountService financialActivityAccountService;
    private JournalEntryService journalService;
    private ServicePointService pointsService;

    @JmsListener(destination = "journal-queue")
    public void listener(String message) {
        log.info("Message received on Journal posting {} ", message);
    }
}
