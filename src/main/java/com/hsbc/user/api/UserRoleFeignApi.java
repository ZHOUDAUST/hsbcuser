package com.hsbc.user.api;

import com.hsbc.user.dto.RoleDto;
import com.hsbc.user.dto.RoleUserDto;
import com.hsbc.user.dto.UserDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户角色鉴权接口层
 * @author dazhou
 * @since 2023-08-30
 */
@RequestMapping("/hsbc")
public interface UserRoleFeignApi {

    /**adduser
     * @param userDto dto
     * @return true/false Should fail if the user already exists
     */
    @PostMapping("/adduser")
    Boolean addUser(@RequestBody UserDto userDto);

    /**deleteUser
     * @param userDto dto
     * @return true/false Should fail if the user does not exists
     */
    @PostMapping("/deleteuser")
    Boolean deleteUser(@RequestBody UserDto userDto);

    /**addrole
     * @param roleDto roleName
     * @return true/false Should fail if the role already exists
     */
    @PostMapping("/createrole")
    Boolean createRole(@RequestBody RoleDto roleDto);

    /**deleteRole
     * @param roleDto roleDto
     * @return true/false Should fail if the role does not exists
     */
    @PostMapping("/deleterole")
    Boolean deleteRole(@RequestBody RoleDto roleDto);

    /**addRoleUser
     * @param roleUserDto roleUserDto
     * @return true/false If the role is already associated with the user, nothing should happen
     */
    @PostMapping("/addroleuser")
    Boolean addRoleUser(@RequestBody RoleUserDto roleUserDto);

    /**authenticate
     * @param userDto dto
     * @return a special "secret" auth token or error, if not found. The token is only valid
     * for pre-configured time (2h)
     */
    @PostMapping("/authenticate")
    String authenticate(@RequestBody UserDto userDto) throws Exception;

    /**authenticate
     * @param token token
     */
    @PostMapping("/invalidate")
    void invalidate(@RequestHeader String token);

    /**checkRole
     * @param token token
     * @param roleDto role
     * @return true if the user, identified by the token, belongs to the role, false; otherwise, error if token is invalid expired etc
     */
    @PostMapping("/checkrole")
    Boolean checkRole(@RequestHeader String token, @RequestBody RoleDto roleDto) throws Exception;

    /**get all roles
     * @param token token
     * @return all roles for the user, error if token is invalid
     */
    @PostMapping("/allroles")
    List<String> allRoles(@RequestHeader String token) throws Exception;
}
