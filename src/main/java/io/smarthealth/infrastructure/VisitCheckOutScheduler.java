package io.smarthealth.infrastructure;

import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author Simon.waweru
 */
@Slf4j
@Component
public class VisitCheckOutScheduler {

    @Autowired
    VisitService visitService;

    @Autowired
    PatientQueueService patientQueueService;

    /*
    Ukitaka kukumbuka 
    
    Reference source https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
     */
    @Scheduled(cron = "0 15 06 * * ?") //Fire at 06:15 AM every day
//    @Scheduled(cron = "0 52 13 * * ?") //Fire at 06:15 AM every day
    public void checkoutAllActiveVisitsPast24Hours() {
        log.info("checkoutAllActiveVisitsPast24Hours Job started");
        List<Visit> visits = visitService.fetchAllVisitsSurpassed24hrs();
        for (Visit v : visits) {
            v.setStatus(VisitEnum.Status.CheckOut);
            v.setStopDatetime(LocalDateTime.now());
            visitService.createAVisit(v);

            //mark active visit status on queue as false
            List<PatientQueue> pq = patientQueueService.fetchQueueByVisit(v);
            for(PatientQueue q: pq){
              q.setStatus(false);
              patientQueueService.createPatientQueue(q);
            }
        }
        log.info("checkoutAllActiveVisitsPast24Hours Job ended");
    }
}
