package io.smarthealth.security.api;

import io.smarthealth.security.service.RoleService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.data.PermissionData;
import io.smarthealth.security.data.RoleData;
import io.smarthealth.security.data.RolePermissionsData;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@RestController
@Slf4j
@Api
@RequestMapping("/api")
public class RolesController {

    private final RoleService service;

    public RolesController(RoleService service) {
        this.service = service;
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('create_roles')")
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleData roleData) {

        RoleData result = service.createRole(roleData);

        Pager<RoleData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Role created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PostMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('create_roles')")
    public ResponseEntity<?> actionsOnRoles(@PathVariable(value = "id") Long roleId,
            @RequestParam(value = "command", required = true) Command commandParam) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.changeRoleStatus(roleId, commandParam.name()));

//        if (is(commandParam, "enable") || is(commandParam, "disable")) {
//            return ResponseEntity
//                    .status(HttpStatus.OK)
//                    .body(service.changeRoleStatus(roleId, commandParam));
//        } else {
//            throw APIException.badRequest("Unknown Command {0} ", commandParam);
//        }
    }

    @GetMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('view_roles')")
    public RoleData retrieveRole(@PathVariable(value = "id") Long code) {
        return service.getRoleWithNoFoundDetection(code)
                .toData();
    }

    @PutMapping("/roles/{roleId}")
    @PreAuthorize("hasAuthority('edit_roles')")
    public ResponseEntity<?> actionsOnRoles(@PathVariable(value = "roleId") Long roleId, @Valid @RequestBody RoleData roleData) {
        RoleData data = service.updateRole(roleId, roleData);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(data);
    }

    @GetMapping("/roles/{id}/permissions")
    @PreAuthorize("hasAuthority('view_roles')")
    public ResponseEntity<?> retrieveRolePermissions(@PathVariable(value = "id") Long roleId) {
        RolePermissionsData data = service.getRolePermissions(roleId);
        return ResponseEntity.ok(data);
    }

    @PutMapping("/roles/{id}/permissions")
    @PreAuthorize("hasAuthority('edit_roles')")
    public ResponseEntity<?> updateRolePermissions(@PathVariable(value = "id") Long roleId, @Valid @RequestBody List<PermissionData> data) {
        RoleData rd = service.updateRolePermissions(roleId, data);
        return ResponseEntity.ok(rd);
    }

    @DeleteMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('delete_roles')")
    public ResponseEntity<?> deleteRole(@PathVariable(value = "id") Long code) {
        service.deleteRole(code);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('view_roles')")
    public ResponseEntity<?> retrieveAllRoles(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<RoleData> list = service.retrieveAllRoles(pageable);

        Pager<List<RoleData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Roles");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

    public enum Command {
        enable,
        disable
    }
}
