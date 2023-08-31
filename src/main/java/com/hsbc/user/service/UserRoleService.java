package com.hsbc.user.service;

import cn.hutool.core.bean.BeanUtil;
import com.hsbc.user.dto.RoleDto;
import com.hsbc.user.dto.RoleUserDto;
import com.hsbc.user.dto.UserDto;
import com.hsbc.user.pojo.TokenObject;
import com.hsbc.user.pojo.UserPo;
import com.hsbc.user.util.PasswordEncrpytUtil;
import com.hsbc.user.util.TokenGenerator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import cn.hutool.json.JSONUtil;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mvc service
 * 处理用户角色鉴权具体逻辑
 * @author dazhou
 * @since 2023-08-30
 */
@Service
@Slf4j
public class UserRoleService {

    private static final Logger logger = LoggerFactory.getLogger(UserRoleService.class);

    /**
     * key username
     * value UserPo
     * 如果不在map里面说明token无效
     */
    private static final Map<String, UserPo> USER_MAP = new ConcurrentHashMap<>();

    /**
     * 用来存放角色
     */
    private static final Set<String> ROLE_SET = new HashSet<>();

    /**
     * key token
     * value 解密的key
     * 如果不在map里面说明token无效
     * 定时任务删除过期token
     */
    public static final Map<String, SecretKey> TOKEN_MAP = new HashMap<>();

    /**
     * 密钥
     */
    private static final String secretKey = "zhoudaSecretKey";

    /** add user method
     * @param userDto dto
     * @return true/false Should fail if the user already exists
     */
    public Boolean addUser(UserDto userDto) {
        if (USER_MAP.containsKey(userDto.getUsername())) {
            logger.debug(JSONUtil.toJsonStr(USER_MAP));
           return false;
        }
        UserPo userPo = new UserPo();
        userPo.setUsername(userDto.getUsername());
        userPo.setPassword(PasswordEncrpytUtil.hashPassword(userDto.getPassword()));
        USER_MAP.putIfAbsent(userDto.getUsername(), userPo);
        logger.debug(JSONUtil.toJsonStr(USER_MAP));
        log.debug(JSONUtil.toJsonStr(USER_MAP));
        return true;
    }

    /**deleteUser
     * @param userDto dto
     * @return true/false Should fail if the user do not exists
     */
    public Boolean deleteUser(UserDto userDto) {
        if (USER_MAP.get(userDto.getUsername()) == null) {
            return false;
        }
        USER_MAP.remove(userDto.getUsername());
        return true;
    }

    public Boolean createRole(String roleName) {
        return ROLE_SET.add(roleName);
    }

    public Boolean deleteRole(RoleDto roleDto) {
        return ROLE_SET.remove(roleDto.getRoleName());
    }

    public Boolean addRoleUser(RoleUserDto roleUserDto) {
        String username = roleUserDto.getUsername();
        UserPo userPo = USER_MAP.get(username);
        List<String> roles = userPo.getRoles();
        if (roles.contains(roleUserDto.getRoleName())) {
            return true;
        }
        userPo.addRole(roleUserDto.getRoleName());
        return true;
    }

    /**生成加密的token
     * 包含：
     * 用户名
     * 过期时间
     * 是否有效
     * @param userDto dto
     * @return 加密的token
     */
    public String authenticate(UserDto userDto) throws Exception {
        UserPo userPo = USER_MAP.get(userDto.getUsername());
        if (userPo == null || !PasswordEncrpytUtil.checkPassword(userDto.getPassword(), userPo.getPassword())) {
            //如果用户not found 信息不实 则返回error
            throw new RuntimeException("user not found error");
        }
        TokenObject tokenObject = new TokenObject();
        tokenObject.setUsername(userDto.getUsername());
        tokenObject.setExpireTime(LocalDateTime.now().plusHours(2l));
        SecretKey secretKey = TokenGenerator.generateSecretKey(UserRoleService.secretKey);
        String encryptToken = TokenGenerator.encryptToken(JSONUtil.toJsonStr(tokenObject), secretKey);
        //将token放入set 此处map用作缓存功能
        TOKEN_MAP.put(encryptToken, secretKey);
        return encryptToken;
    }

    /**将token失效
     * 之后带有此token的请求都无效
     * @param token token
     */
    public void invalidate(String token) {
        TOKEN_MAP.remove(token);
    }

    public Boolean checkRole(String token, RoleDto roleDto) throws Exception {
        UserPo userPo = checkToken(token);
        //token对应的用户所属角色不对应
        return userPo.getRoles().contains(roleDto.getRoleName());
    }

    private UserPo checkToken(String token) throws Exception {
        if (!TOKEN_MAP.containsKey(token)) {
            //token不存在了
            throw new RuntimeException("token invalid");
        }
        //解密token
        SecretKey secretKey = TOKEN_MAP.get(token);
        String decryptToken = TokenGenerator.decryptToken(token, secretKey);
        TokenObject tokenObject = BeanUtil.toBean(JSONUtil.parseObj(decryptToken), TokenObject.class);
        UserPo userPo = USER_MAP.get(tokenObject.getUsername());
        if (userPo == null) {
            //token对应的用户不存在
            throw new RuntimeException("token invalid with an wrong username");
        }
        if (tokenObject.getExpireTime().isBefore(LocalDateTime.now())) {
            //token过期 //删除token
            TOKEN_MAP.remove(token);
            throw new RuntimeException("token expire");
        }
        return userPo;
    }

    public List<String> allRoles(String token) throws Exception {
        UserPo userPo = checkToken(token);
        return userPo.getRoles();
    }
}
