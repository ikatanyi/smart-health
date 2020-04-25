/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure;

import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
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
public class jobs {

    @Autowired
    VisitService visitService;

    /*
    Ukitaka kukumbuka 
    
    Reference source https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
     */
    @Scheduled(cron = "0 15 06 * * ?") //Fire at 06:15 AM every day 
    public void checkoutAllActiveVisitsPast24Hours() {
        log.info("checkoutAllActiveVisitsPast24Hours Job started");
        List<Visit> visits = visitService.fetchAllVisitsSurpassed24hrs();
        for (Visit v : visits) {
            v.setStatus(VisitEnum.Status.CheckOut);
            v.setStopDatetime(LocalDateTime.now());
            visitService.createAVisit(v);
        }
        log.info("checkoutAllActiveVisitsPast24Hours Job ended");
    }
}
