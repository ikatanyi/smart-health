package io.smarthealth.debtor.scheme.api;

import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.debtor.scheme.data.SchemConfigData;
import io.smarthealth.debtor.scheme.data.SchemeData;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    SequenceNumberService sequenceNumberService;

    @PostMapping("/scheme")
    @PreAuthorize("hasAuthority('create_scheme')")
    public ResponseEntity<?> createScheme(@Valid @RequestBody SchemeData scheme) {
        //validate Payer
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(scheme.getPayerId());

        Scheme s = SchemeData.map(scheme);
        if (s.getSchemeCode() == null) {
            s.setSchemeCode(sequenceNumberService.next(1L, Sequences.SchemeCode.name()));
        }
        s.setPayer(payer);
        Scheme savedScheme = schemeService.createScheme(s);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/scheme/{id}")
                .buildAndExpand(s.getId()).toUri();

        SchemeData data = SchemeData.map(savedScheme);
        Optional<SchemeConfigurations> config = schemeService.fetchSchemeConfigByScheme(savedScheme);
        if (config.isPresent()) {
            data.setConfigData(SchemConfigData.map(config.get()));
        }

        return ResponseEntity.created(location).body(data);
    }

    @PutMapping("/scheme/{id}")
    @PreAuthorize("hasAuthority('create_scheme')")
    public ResponseEntity<?> updateScheme(
            @PathVariable("id") final Long schemeId,
            @Valid @RequestBody SchemeData d) {
        Scheme scheme = schemeService.fetchSchemeById(schemeId);
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(d.getPayerId());

        scheme.setSchemeCode(d.getSchemeCode());
        scheme.setCover(d.getCover());
        scheme.setEmailAddress(d.getEmailAddress());
        scheme.setLine1(d.getLine1());
        scheme.setLine2(d.getLine2());
        scheme.setMobileNo(d.getMobileNo());
        scheme.setSchemeName(d.getSchemeName());
        scheme.setTelNo(d.getTelNo());

        scheme.setPayer(payer);
        if (scheme.getSchemeCode() == null) {
            scheme.setSchemeCode(sequenceNumberService.next(1L, Sequences.SchemeCode.name()));
        }
        Scheme savedScheme = schemeService.createScheme(scheme);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/scheme/{id}")
                .buildAndExpand(scheme.getId()).toUri();

        SchemeData data = SchemeData.map(savedScheme);
        Optional<SchemeConfigurations> config = schemeService.fetchSchemeConfigByScheme(savedScheme);
        if (config.isPresent()) {
            data.setConfigData(SchemConfigData.map(config.get()));
        }

        return ResponseEntity.created(location).body(data);
    }

    @GetMapping("/scheme")
    @PreAuthorize("hasAuthority('view_scheme')")
    public ResponseEntity<?> fetchAllSchemes(
            @RequestParam(required = false) final String term,
            @RequestParam(value = "smartEnabled", required = false) Boolean smartEnabled, 
            @RequestParam(value = "withCopay", required = false) Boolean withCopay, 

            Pageable pageable) {
        Page<SchemeData> scheme = schemeService.fetchSchemes(term, pageable).map(p
                -> {
            SchemeData data = SchemeData.map(p);
            Optional<SchemeConfigurations> config = schemeService.fetchSchemeConfigByScheme(p);
            if (config.isPresent()) {
                data.setConfigData(SchemConfigData.map(config.get()));
            }
            return data;
        });

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
    @PreAuthorize("hasAuthority('view_scheme')")
    public ResponseEntity<?> fetchAllSchemesByPayer(
            @PathVariable("id") Long id,
            @RequestParam(required = false) final String term,
            Pageable pageable) {
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(id);
        Page<SchemeData> scheme = schemeService.fetchSchemesByPayer(payer, term, pageable).map(p -> {
            SchemeData data = SchemeData.map(p);
            Optional<SchemeConfigurations> config = schemeService.fetchSchemeConfigByScheme(p);
            if (config.isPresent()) {
                data.setConfigData(SchemConfigData.map(config.get()));
            }
            return data;
        }
        );

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
    @PreAuthorize("hasAuthority('view_scheme')")
    public ResponseEntity<?> fetchSchemeById(@PathVariable("id") final Long schemeId) {
        Scheme scheme = schemeService.fetchSchemeById(schemeId);
        SchemeData schemeData = SchemeData.map(scheme);
        Optional<SchemeConfigurations> config = schemeService.fetchSchemeConfigByScheme(scheme);
        if (config.isPresent()) {
            schemeData.setConfigData(SchemConfigData.map(config.get()));
        }
        return ResponseEntity.ok(schemeData);
    }

    @PostMapping("/scheme/{id}/scheme-configuration")
    @PreAuthorize("hasAuthority('create_scheme')")
    public ResponseEntity<?> updateSchemeConfiguration(@PathVariable("id") final Long schemeId, @Valid @RequestBody SchemConfigData data) {
        Scheme scheme = schemeService.fetchSchemeById(schemeId);
        SchemeConfigurations configSaved = null;
        if (data.getConfigId() == null || data.getConfigId().equals("")) {
            //save as new
            SchemeConfigurations configurations = SchemConfigData.map(data);
            configurations.setScheme(scheme);
            //configurations.setClaimSwitching(true);
            configSaved = schemeService.updateSchemeConfigurations(configurations);
        } else {
            //look for scheme config 
            SchemeConfigurations schemeConfig = schemeService.fetchSchemeConfigById(data.getConfigId());
            schemeConfig.setCoPayType(data.getCoPayType());
            schemeConfig.setCopayEnabled(data.isCopayEnabled());
            schemeConfig.setCoPayValue(data.getCoPayValue());
            schemeConfig.setDiscountMethod(data.getDiscountMethod());
            schemeConfig.setDiscountValue(data.getDiscountValue());
//            schemeConfig.setScheme(scheme);
            schemeConfig.setSmartEnabled(data.isSmartEnabled());
            schemeConfig.setStatus(data.isStatus());
            schemeConfig.setCapitationAmount(data.getCapitationAmount());
            schemeConfig.setCapitationEnabled(data.isCapitationEnabled());
            configSaved = schemeService.updateSchemeConfigurations(schemeConfig);
        }

        Scheme myscheme=configSaved.getScheme();
        Pager<SchemeData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Scheme parameters have successfully been updated");
        pagers.setContent(SchemeData.map(myscheme));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/scheme/{id}/scheme-configuration")
    @PreAuthorize("hasAuthority('view_scheme')")
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
    @PreAuthorize("hasAuthority('view_scheme')")
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
