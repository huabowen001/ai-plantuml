package net.sourceforge.plantuml.servlet.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * 密码加密工具类.
 */
public final class PasswordUtil {

    /**
     * Private constructor to hide the implicit public one.
     */
    private PasswordUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 加密密码.
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    /**
     * 验证密码.
     *
     * @param password 明文密码
     * @param hashedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
        return result.verified;
    }
}
