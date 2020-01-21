package io.smarthealth.debtor.scheme.api;

import io.smarthealth.appointment.data.AppointmentData;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.debtor.scheme.data.SchemConfigData;
import io.smarthealth.debtor.scheme.data.SchemeData;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author Simon.Waweru
 */
@Api
@RestController
@RequestMapping("/api")
public class SchemeController {

    @Autowired
    PayerService payerService;

    @Autowired
    SchemeService schemeService;

    @PostMapping("/scheme")
    public ResponseEntity<?> createScheme(@Valid @RequestBody SchemeData scheme) {
        //validate Payer
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(scheme.getPayerId());

        Scheme s = SchemeData.map(scheme);
        s.setPayer(payer);
        Scheme savedScheme = schemeService.createScheme(s);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/scheme/{id}")
                .buildAndExpand(s.getId()).toUri();

        SchemeData data = SchemeData.map(savedScheme);

        return ResponseEntity.created(location).body(data);
    }

    @GetMapping("/scheme")
    public ResponseEntity<?> fetchAllSchemes(Pageable pageable) {
        Page<SchemeData> scheme = schemeService.fetchSchemes(pageable).map(p -> SchemeData.map(p));

        Pager<List<SchemeData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(scheme.getContent());
        PageDetails details = new PageDetails();
        details.setPage(scheme.getNumber() + 1);
        details.setPerPage(scheme.getSize());
        details.setTotalElements(scheme.getTotalElements());
        details.setTotalPage(scheme.getTotalPages());
        details.setReportName("Scheme List");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/payer/{id}/scheme")
    public ResponseEntity<?> fetchAllSchemesByPayer(@PathVariable("id") Long id, Pageable pageable) {
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(id);
        Page<SchemeData> scheme = schemeService.fetchSchemesByPayer(payer, pageable).map(p -> SchemeData.map(p));

        Pager<List<SchemeData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(scheme.getContent());
        PageDetails details = new PageDetails();
        details.setPage(scheme.getNumber() + 1);
        details.setPerPage(scheme.getSize());
        details.setTotalElements(scheme.getTotalElements());
        details.setTotalPage(scheme.getTotalPages());
        details.setReportName("Scheme List");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/scheme/{id}")
    public ResponseEntity<?> fetchSchemeById(@PathVariable("id") final Long schemeId) {
        SchemeData schemeData = SchemeData.map(schemeService.fetchSchemeById(schemeId));

        return ResponseEntity.ok(schemeData);
    }

    @PostMapping("/scheme/{id}/scheme-configuration")
    public ResponseEntity<?> updateSchemeConfiguration(@PathVariable("id") final Long schemeId, @Valid @RequestBody SchemConfigData data) {
        Scheme scheme = schemeService.fetchSchemeById(schemeId);
        SchemeConfigurations configSaved = null;
        if (data.getConfigId() == null || data.getConfigId().equals("")) {
            //save as new
            SchemeConfigurations configurations = SchemConfigData.map(data);
            configurations.setScheme(scheme);
            configSaved = schemeService.updateSchemeConfigurations(configurations);
        } else {
            //look for scheme config 
            SchemeConfigurations schemeConfig = schemeService.fetchSchemeConfigById(data.getConfigId());
            schemeConfig.setCoPayType(data.getCoPayType());
            schemeConfig.setCoPayValue(data.getCoPayValue());
            schemeConfig.setDiscountMethod(data.getDiscountMethod());
            schemeConfig.setDiscountValue(data.getDiscountValue());
//            schemeConfig.setScheme(scheme);
            schemeConfig.setSmartEnabled(data.isSmartEnabled());
            schemeConfig.setStatus(data.isStatus());
            configSaved = schemeService.updateSchemeConfigurations(schemeConfig);
        }

        Pager<SchemConfigData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Scheme parameters have successfully been updated");
        pagers.setContent(SchemConfigData.map(configSaved));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/scheme/{id}/scheme-configuration")
    public ResponseEntity<?> fetchSchemeConfigurationByScheme(@PathVariable("id") final Long id) {
        //look for scheme config
        Scheme scheme = schemeService.fetchSchemeById(id);

        if (schemeService.SchemeConfigBySchemeExists(scheme)) {
            SchemeConfigurations schemeConfig = schemeService.fetchSchemeConfigBySchemeWithNotAvailableDetection(scheme);
            Pager<SchemConfigData> pagers = new Pager();
            pagers.setCode("200");
            pagers.setMessage("Scheme parameters");
            pagers.setContent(SchemConfigData.map(schemeConfig));

            return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
        } else {
            Pager<SchemConfigData> pagers = new Pager();
            pagers.setCode("404");
            pagers.setMessage("Scheme parameters not available");
            return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
        }

    }

    @GetMapping("/scheme-configuration/{id}")
    public ResponseEntity<?> fetchSchemeConfiguration(@PathVariable("id") final Long id) {
        //look for scheme config
        SchemeConfigurations schemeConfig = schemeService.fetchSchemeConfigById(id);

        Pager<SchemConfigData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Scheme parameters have successfully been updated");
        pagers.setContent(SchemConfigData.map(schemeConfig));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

}
