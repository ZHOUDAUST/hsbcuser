package com.hsbc.user.controller;

import com.hsbc.user.api.UserRoleFeignApi;
import com.hsbc.user.dto.RoleDto;
import com.hsbc.user.dto.RoleUserDto;
import com.hsbc.user.dto.UserDto;
import com.hsbc.user.service.UserRoleService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * mvc controller
 * @author dazhou
 * @since 2023-08-30
 */
@RestController
public class UserRoleController implements UserRoleFeignApi {

    /**
     * 引入service
     */
    @Autowired
    private UserRoleService userRoleService;

    @Override
    public Boolean addUser(UserDto userDto) {
        return userRoleService.addUser(userDto);
    }

    @Override
    public Boolean deleteUser(UserDto userDto) {
        return userRoleService.deleteUser(userDto);
    }

    /**
     * addrole
     *
     * @param roleDto roleName
     * @return true/false Should fail if the role already exists
     */
    @Override
    public Boolean createRole(RoleDto roleDto) {
        return userRoleService.createRole(roleDto.getRoleName());
    }

    /**
     * deleteRole
     *
     * @param roleDto roleDto
     * @return true/false Should fail if the role does not exists
     */
    @Override
    public Boolean deleteRole(RoleDto roleDto) {
        return userRoleService.deleteRole(roleDto);
    }

    /**
     * addRoleUser
     *
     * @param roleUserDto dto
     * @return true/false If the role is already associated with the user, nothing should happen
     */
    @Override
    public Boolean addRoleUser(RoleUserDto roleUserDto) {
        return userRoleService.addRoleUser(roleUserDto);
    }

    /**
     * authenticate
     *
     * @param userDto dto
     * @return a special "secret" auth token or error, if not found. The token is only valid
     * for pre-configured time (2h)
     */
    @Override
    public String authenticate(UserDto userDto) throws Exception {
        return userRoleService.authenticate(userDto);
    }

    /**
     * authenticate
     *
     * @param token token
     */
    @Override
    public void invalidate(String token) {
        userRoleService.invalidate(token);
    }

    /**
     * checkRole
     *
     * @param token   token
     * @param roleDto role
     * @return true if the user, identified by the token, belongs to the role, false; otherwise, error if token is invalid expired etc
     */
    @Override
    public Boolean checkRole(String token, RoleDto roleDto) throws Exception {
        return userRoleService.checkRole(token, roleDto);
    }

    /**
     * get all roles
     *
     * @param token token
     * @return all roles for the user, error if token is invalid
     */
    @Override
    public List<String> allRoles(String token) throws Exception {
        return userRoleService.allRoles(token);
    }
}
