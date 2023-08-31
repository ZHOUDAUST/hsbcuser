package com.hsbc.user.service;

import cn.hutool.json.JSONUtil;
import com.hsbc.user.dto.RoleDto;
import com.hsbc.user.dto.RoleUserDto;
import com.hsbc.user.dto.UserDto;
import com.hsbc.user.pojo.TokenObject;
import com.hsbc.user.util.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.crypto.SecretKey;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.hsbc.user.service.UserRoleService.TOKEN_MAP;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserRoleServiceTest {

    @InjectMocks
    UserRoleService userRoleService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void addUser() {
        UserDto userDto = new UserDto();
        userDto.setUsername("zhouda");
        userDto.setPassword("123456");

        assertTrue(userRoleService.addUser(userDto));
    }

    @Test
    void addUserExist() {
        UserDto userDto = new UserDto();
        userDto.setUsername("zhoudaexist");
        userDto.setPassword("1234567");
        userRoleService.addUser(userDto);

        assertFalse(userRoleService.addUser(userDto));
    }

    @Test
    void deleteUser() {
        UserDto userDto = new UserDto();
        userDto.setUsername("zhoudanotexist");
        assertFalse(userRoleService.deleteUser(userDto));
    }

    @Test
    void deleteUserTrue() {
        UserDto userDto = new UserDto();
        userDto.setUsername("zhouda2");
        userDto.setPassword("12345678");
        userRoleService.addUser(userDto);

        UserDto userDtoDel = new UserDto();
        userDtoDel.setUsername("zhouda2");
        assertTrue(userRoleService.deleteUser(userDtoDel));
    }

    @Test
    void createRole() {
        assertTrue(userRoleService.createRole("role1"));
    }

    @Test
    void createRoleExist() {
        userRoleService.createRole("role2");
        assertFalse(userRoleService.createRole("role2"));
    }

    @Test
    void deleteRole() {
        RoleDto roleDto = new RoleDto();
        roleDto.setRoleName("role3");
        assertFalse(userRoleService.deleteRole(roleDto));
    }

    @Test
    void addRoleUser() {
        UserDto userDto = new UserDto();
        userDto.setUsername("zhouda3");
        userDto.setPassword("123456789");
        userRoleService.addUser(userDto);

        userRoleService.createRole("role4");


        RoleUserDto roleUserDto = new RoleUserDto();
        roleUserDto.setRoleName("role4");
        roleUserDto.setUsername("zhouda3");
        assertTrue(userRoleService.addRoleUser(roleUserDto));
    }

    @Test
    void addRoleUserExist() {
        UserDto userDto = new UserDto();
        userDto.setUsername("zhouda4");
        userDto.setPassword("223456");
        userRoleService.addUser(userDto);

        userRoleService.createRole("role5");


        RoleUserDto roleUserDto = new RoleUserDto();
        roleUserDto.setRoleName("role5");
        roleUserDto.setUsername("zhouda4");
        userRoleService.addRoleUser(roleUserDto);
        assertTrue(userRoleService.addRoleUser(roleUserDto));
    }

    @Test
    void authenticate() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("zhouda5");
        userDto.setPassword("222222");
        userRoleService.addUser(userDto);

        String token = userRoleService.authenticate(userDto);

        assertNotNull(token);
    }

    @Test
    void authenticateNotFoundError() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("zhouda6");
        userDto.setPassword("1234567");

        assertThrows(RuntimeException.class, () -> userRoleService.authenticate(userDto));
    }


    @Test
    void invalidate() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("zhouda7");
        userDto.setPassword("123456");
        userRoleService.addUser(userDto);

        String token = userRoleService.authenticate(userDto);
        assertDoesNotThrow(() -> userRoleService.invalidate(token));
    }

    @Test
    void invalidateNull() {
        assertDoesNotThrow(() -> userRoleService.invalidate("token"));
    }

    @Test
    void checkRole() throws Exception {
//        addUser();
        UserDto userDto = new UserDto();
        userDto.setUsername("zhouda8");
        userDto.setPassword("123456");
        userRoleService.addUser(userDto);
//        createRole();
        userRoleService.createRole("role6");
//        addRoleUser();
        RoleUserDto roleUserDto = new RoleUserDto();
        roleUserDto.setRoleName("role6");
        roleUserDto.setUsername("zhouda8");
        userRoleService.addRoleUser(roleUserDto);

        String token = userRoleService.authenticate(userDto);
        RoleDto roleDto = new RoleDto();
        roleDto.setRoleName("role6");
        assertTrue(userRoleService.checkRole(token, roleDto));
    }

    @Test
    void checkRoleTokenInvalid() {
        RoleDto roleDto = new RoleDto();
        roleDto.setRoleName("role6");
        assertThrows(RuntimeException.class, () -> userRoleService.checkRole("ainvalidtoken", roleDto));
    }

    @Test
    void checkRoleInvalidRole() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("zhouda9");
        userDto.setPassword("123456");
        userRoleService.addUser(userDto);
        String token = userRoleService.authenticate(userDto);

        RoleDto roleDto = new RoleDto();
        roleDto.setRoleName("role6");
        assertFalse(userRoleService.checkRole(token, roleDto));
    }

    @Test
    void checkRoleInvalidUser() throws Exception {
        SecretKey zhoudaSecretKey = TokenGenerator.generateSecretKey("zhoudaSecretKey");
        TokenObject tokenObject = new TokenObject();
        tokenObject.setUsername("zhouda10");
        tokenObject.setExpireTime(LocalDateTime.now().plusHours(2l));
        String token = TokenGenerator.encryptToken(JSONUtil.toJsonStr(tokenObject), zhoudaSecretKey);
        TOKEN_MAP.put(token, zhoudaSecretKey);

        RoleDto roleDto = new RoleDto();
        roleDto.setRoleName("role6");
        assertThrows(RuntimeException.class, () -> userRoleService.checkRole(token, roleDto));
    }

    @Test
    void checkRoleTokenExpire() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("zhouda10");
        userDto.setPassword("123456");
        userRoleService.addUser(userDto);

        SecretKey zhoudaSecretKey = TokenGenerator.generateSecretKey("zhoudaSecretKey");
        TokenObject tokenObject = new TokenObject();
        tokenObject.setUsername("zhouda10");
        tokenObject.setExpireTime(LocalDateTime.now().minusHours(2l));
        String token = TokenGenerator.encryptToken(JSONUtil.toJsonStr(tokenObject), zhoudaSecretKey);
        TOKEN_MAP.put(token, zhoudaSecretKey);

        RoleDto roleDto = new RoleDto();
        roleDto.setRoleName("role6");
        assertThrows(RuntimeException.class, () -> userRoleService.checkRole(token, roleDto));
    }

    @Test
    void allRoles() throws Exception {
        //        addUser();
        UserDto userDto = new UserDto();
        userDto.setUsername("zhouda11");
        userDto.setPassword("123456");
        userRoleService.addUser(userDto);
//        createRole();
        userRoleService.createRole("role7");
//        addRoleUser();
        RoleUserDto roleUserDto = new RoleUserDto();
        roleUserDto.setRoleName("role7");
        roleUserDto.setUsername("zhouda11");
        userRoleService.addRoleUser(roleUserDto);

        String token = userRoleService.authenticate(userDto);
        List<String> roles = new ArrayList<>();
        roles.add("role7");
        assertEquals(userRoleService.allRoles(token), roles);
    }
}