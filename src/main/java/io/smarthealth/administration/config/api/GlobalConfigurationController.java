package io.smarthealth.administration.config.api;

import io.smarthealth.administration.config.domain.GlobalConfigNum;
import io.smarthealth.administration.config.domain.GlobalConfiguration;
import io.smarthealth.administration.config.service.ConfigService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

;

/**
 * Global configuration related to set of supported enable/disable
 * configurations
 *
 * @author Kelsas
 */


@Api
@RestController
@Slf4j
@RequestMapping("/api")
public class GlobalConfigurationController {

    private final ConfigService service;

    public GlobalConfigurationController(ConfigService service) {
        this.service = service;
    }

    @GetMapping("/configurations/{id}")
    @PreAuthorize("hasAuthority('view_configurations')")
    public ResponseEntity<?> getConfiguration(@PathVariable(value = "id") Long code) {
        GlobalConfiguration config = service.getWithNonFoundDetection(code);
        return ResponseEntity.ok(config);
    }
    
    @GetMapping("/configurations/{configName}/name")
    @PreAuthorize("hasAuthority('view_globalConfigurations')")
    public ResponseEntity<?> getConfigurationByName(@PathVariable(value = "configName") GlobalConfigNum configName) {
        GlobalConfiguration config = service.getByNameOrThrow(configName.name());
        return ResponseEntity.ok(config);
    }

    @PutMapping("/configurations/{id}")
    @PreAuthorize("hasAuthority('edit_configurations')")
    public ResponseEntity<?> updateConfiguration(@PathVariable(value = "id") Long code, @Valid @RequestBody GlobalConfiguration config) {

        GlobalConfiguration savedConfig = service.updateConfig(code, config);

        return ResponseEntity.ok(savedConfig);
    }

    @GetMapping("/configurations")
    @PreAuthorize("hasAuthority('view_configurations')")
    public ResponseEntity<?> listConfigurations(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<GlobalConfiguration> list = service.fetchAllConfigs(pageable);
        Pager<List<GlobalConfiguration>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Global Configurations");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
}
