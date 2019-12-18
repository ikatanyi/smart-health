package io.smarthealth.organization.org.api;

import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.org.data.OrganizationData;
import io.smarthealth.organization.org.domain.Organization;
import io.smarthealth.organization.org.service.OrganizationService;
import io.swagger.annotations.Api;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class OrganizationController {

    private final OrganizationService service;

    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    @PostMapping("/organization")
    public @ResponseBody
    ResponseEntity<?> createOrganization(@RequestBody @Valid final OrganizationData orgData) {
        OrganizationData result = service.createOrganization(orgData);
        Pager<OrganizationData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Organization created successfully");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/organization/{id}")
    public OrganizationData getOrganization(@PathVariable(value = "id") String id) {
        Organization org = service.getOrganization(id);
        return OrganizationData.map(org);
    }

    @PutMapping("/organization/{id}")
    public ResponseEntity<?> OrganizationData(@PathVariable(value = "id") String id, @RequestBody @Valid OrganizationData data) {
        System.out.println("org_organization id " + id);
        System.out.println("data " + data.toString());
        OrganizationData org = service.updateOrganization(id, data);
        return ResponseEntity.status(HttpStatus.OK).body(org);
    }

    @GetMapping("/organization")
    public OrganizationData getActiveOrganization() {
        Organization org = service.getActiveOrganization();

        return OrganizationData.map(org);
    }
}
