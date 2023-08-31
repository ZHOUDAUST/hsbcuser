package com.hsbc.user.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * user pojo
 * 用来存储用户信息
 */
@Data
public class UserPo {

    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;

    /**
     * roles
     */
    private List<String> roles = new ArrayList<>();

    /**add role
     * @param roleName role
     */
    public void addRole(String roleName) {
        roles.add(roleName);
    }
}
