package com.hsbc.user.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * 密码加密
 */
public class PasswordEncryptUtil {

    private PasswordEncryptUtil() {
    }

    /**加密方法
     * @param plainPassword 明文password
     * @return hashed pwd
     */
    public static String hashPassword(String plainPassword) {
        int bcryptRounds = 12;
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(bcryptRounds));
        }

    /**检验密码
     * @param plainPassword 明文password
     * @param hashedPassword hashed pwd
     * @return true/false
     */
        public static boolean checkPassword(String plainPassword, String hashedPassword) {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        }
}
