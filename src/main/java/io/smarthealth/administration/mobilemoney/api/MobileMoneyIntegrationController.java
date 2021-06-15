package io.smarthealth.administration.mobilemoney.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.smarthealth.administration.mobilemoney.data.MobileMoneyIntegrationData;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyIntegration;
import io.smarthealth.administration.mobilemoney.service.MobileMoneyIntegrationService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.integration.data.ClaimFileData;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Api
@RestController
@RequestMapping("/api")
public class MobileMoneyIntegrationController {
    private final MobileMoneyIntegrationService moneyIntegrationService;

    @PostMapping("/mobile-money-integration")
    public @ResponseBody
    ResponseEntity<?> save(@RequestBody @Valid final MobileMoneyIntegrationData data) {
        MobileMoneyIntegration moneyIntegration = moneyIntegrationService.save(data);
        Pager pagers = new Pager();
        pagers.setCode("201");
        pagers.setMessage("success");
        pagers.setContent(MobileMoneyIntegrationData.map(moneyIntegration));
        return ResponseEntity.ok(pagers);
    }

    @PutMapping("/mobile-money-integration")
    public @ResponseBody
    ResponseEntity<?> updateMobileIntegProvider(@RequestBody @Valid final MobileMoneyIntegrationData data, Long id) {
        MobileMoneyIntegration moneyIntegration = moneyIntegrationService.updateMIP(data, id);
        Pager pagers = new Pager();
        pagers.setCode("201");
        pagers.setMessage("success");
        pagers.setContent(MobileMoneyIntegrationData.map(moneyIntegration));
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/mobile-money-integration")
    public @ResponseBody
    ResponseEntity<?> findAll() {
        List<MobileMoneyIntegrationData> mip =
                moneyIntegrationService.findAll().stream().map(m -> MobileMoneyIntegrationData.map(m)).collect(Collectors.toList());
        Pager pager = new Pager();
        pager.setCode("200");
        pager.setContent(mip);
        pager.setMessage("Success");
        PageDetails details = new PageDetails();
        details.setPage(1);
        details.setPerPage(1000);
        details.setReportName("Mobile Integrations");
        pager.setPageDetails(details);
        return ResponseEntity.ok(pager);
    }


}
