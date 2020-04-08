package io.smarthealth.security.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.security.data.PermissionData;
import io.smarthealth.security.data.RoleData;
import io.smarthealth.security.data.RolePermissionsData;
import io.smarthealth.security.domain.Permission;
import io.smarthealth.security.domain.PermissionRepository;
import io.smarthealth.security.domain.Role;
import io.smarthealth.security.domain.RoleRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public RoleData createRole(RoleData data) {

        if (getRoleByName(data.getName()).isPresent()) {
            throw APIException.conflict("Role with name {0} already exists.", data.getName());
        }

        Role role = new Role(data.getName(), data.getDescription());
        Role savedRole = roleRepository.save(role);
        return savedRole.toData();
    }

    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public Role getRoleWithNoFoundDetection(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Role with id {0} not found", id));
    }

    public Page<RoleData> retrieveAllRoles(Pageable page) {
        return roleRepository.findAll(page).map(rd -> rd.toData());
    }

    public String changeRoleStatus(Long id, String command) {
        Role role = getRoleWithNoFoundDetection(id);
        if (command.equals("enabled")) {
            role.setDisabled(Boolean.FALSE);
        } else {
            role.setDisabled(Boolean.TRUE);
        }
        roleRepository.save(role);
        return "Role " + command;
    }

    public RoleData updateRole(Long id, RoleData data) {
        Role role = getRoleWithNoFoundDetection(id);
        if (!role.getName().equals(data.getName())) {
            role.setName(data.getName());
        }
        if (!role.getDescription().equals(data.getDescription())) {
            role.setDescription(data.getDescription());
        }
        return roleRepository.save(role).toData();
    }

    public RolePermissionsData getRolePermissions(Long roleId) {
        Role role = getRoleWithNoFoundDetection(roleId);
        Collection<Permission> permissions = role.getPermissions();
        RoleData roleData = role.toData();
        Collection<PermissionData> pd = permissions
                .stream()
                .map(p -> p.toData())
                .collect(Collectors.toSet());

        RolePermissionsData permissionsData = roleData.toRolePermissionData(pd);
        return permissionsData;
    }

    public RoleData updateRolePermissions(Long roleId, List<PermissionData> data) {
        Role role = getRoleWithNoFoundDetection(roleId);
        List<Permission> list = data.stream()
                .map(permission -> {
                    return permissionRepository.findOneByName(permission.getName()).orElse(null);
                })
                .filter(x -> x != null)
                .collect(Collectors.toList());

        role.updatePermission(list);
        
        return roleRepository.save(role).toData();
        //then we 
    }

    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }
}
