package com.hsbc.user.scheduledtasks;

import cn.hutool.json.JSONUtil;
import com.hsbc.user.pojo.TokenObject;
import com.hsbc.user.util.TokenGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.crypto.SecretKey;

import java.time.LocalDateTime;

import static com.hsbc.user.service.UserRoleService.TOKEN_MAP;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenExpireTaskTest {

    @InjectMocks
    TokenExpireTask tokenExpireTask;

    @Test
    void checkExpireToken() throws Exception {
        SecretKey zhoudaSecretKey = TokenGenerator.generateSecretKey("zhoudaSecretKey");
        TokenObject tokenObject = new TokenObject();
        tokenObject.setUsername("zhouda11");
        tokenObject.setExpireTime(LocalDateTime.now().minusHours(2l));
        String token = TokenGenerator.encryptToken(JSONUtil.toJsonStr(tokenObject), zhoudaSecretKey);
        TOKEN_MAP.put(token, zhoudaSecretKey);
        assertDoesNotThrow(() -> tokenExpireTask.checkExpireToken());
    }
}