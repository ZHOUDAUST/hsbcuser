package com.hsbc.user.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * tokenObject
 */
@Data
public class TokenObject {
    /**
     * 用户名
     * 用于查找相关信息
     */
    private String username;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
}
