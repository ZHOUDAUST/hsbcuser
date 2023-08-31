package com.hsbc.user.scheduledtasks;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.hsbc.user.pojo.TokenObject;
import com.hsbc.user.service.UserRoleService;
import com.hsbc.user.util.TokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Map;

import static com.hsbc.user.service.UserRoleService.TOKEN_MAP;

/**
 *  定时检查失效token
 */
@Component
public class TokenExpireTask {

    private static final Logger logger = LoggerFactory.getLogger(TokenExpireTask.class);

    /**
     * 定时检查失效token
     * 每30min执行一次
     */
    @Scheduled(cron = "* 0/30 * * * ?")
    public void checkExpireToken() throws Exception {
        logger.debug("定时检查失效token开始 --------------------");
        for (Map.Entry<String, SecretKey> tokenSecretKeyEntry : TOKEN_MAP.entrySet()) {
            String token = tokenSecretKeyEntry.getKey();
            SecretKey secretKey = tokenSecretKeyEntry.getValue();
            String decryptToken = TokenGenerator.decryptToken(token, secretKey);
            TokenObject tokenObject = BeanUtil.toBean(JSONUtil.parseObj(decryptToken), TokenObject.class);
            if (tokenObject.getExpireTime().isBefore(LocalDateTime.now())) {
                //token过期 删除token
                TOKEN_MAP.remove(token);
                logger.debug("删除过期的token成功--------------------" + "用户名为：" + tokenObject.getUsername() + "/n"
                + "过期时间为+" + tokenObject.getExpireTime());
            }
        }
    }
}
