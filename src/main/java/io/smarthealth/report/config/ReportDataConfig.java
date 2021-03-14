package io.smarthealth.report.config;

import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.domain.FacilityRepository;
import io.smarthealth.report.data.CompanyHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class ReportDataConfig {
    @Bean
    public CompanyHeader companyHeader(FacilityRepository repository){
        log.info("loading facility details...");
        Facility facility =  repository.findAll()
                .stream().findFirst().orElse(null);
        if(facility!=null){
            log.info("found a facility {} ",facility.getFacilityName());
        }
        return CompanyHeader.of(facility);
    }
}
