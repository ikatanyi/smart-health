package io.smarthealth.organization.org.api;

import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.org.data.OrganisationData;
import io.smarthealth.organization.org.domain.Organisation;
import io.smarthealth.organization.org.service.OrganisationService;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class OrganisationController {

    private final OrganisationService service;

    public OrganisationController(OrganisationService service) {
        this.service = service;
    }

    @PostMapping("/organization")
    @PreAuthorize("hasAuthority('create_organization')")
    public @ResponseBody
    ResponseEntity<?> createOrganization(@RequestBody @Valid final OrganisationData orgData) {
        OrganisationData result = service.createOrganization(orgData);
        Pager<OrganisationData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Organization created successfully");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/organization/{id}")
    @PreAuthorize("hasAuthority('view_organization')")
    public OrganisationData getOrganization(@PathVariable(value = "id") String id) {
        Organisation org = service.getOrganization(id);
        return OrganisationData.map(org);
    }

    @GetMapping("/organization")
    @PreAuthorize("hasAuthority('view_organization')")
    public OrganisationData getActiveOrganization() {
        Organisation org = service.getActiveOrganization();

        return OrganisationData.map(org);
    }
    
    @PutMapping("/organization/{id}")
    @PreAuthorize("hasAuthority('edit_organization')")
    public OrganisationData OrganizationData(@PathVariable(value = "id") String id, OrganisationData data) {
        return service.updateOrganization(id, data);
    }
     
}
