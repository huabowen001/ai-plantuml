package net.sourceforge.plantuml.servlet.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

/**
 * JWT工具类.
 */
public final class JWTUtil {
    private static String secret;
    private static long expiration;

    static {
        try {
            Properties props = new Properties();
            InputStream is = JWTUtil.class.getClassLoader()
                .getResourceAsStream("config.properties");
            if (is != null) {
                props.load(is);
                is.close();
            }

            secret = props.getProperty("jwt.secret", "default-secret-key");
            expiration = Long.parseLong(props.getProperty("jwt.expiration", "86400000"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JWT configuration", e);
        }
    }

    /**
     * Private constructor to hide the implicit public one.
     */
    private JWTUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 生成JWT Token.
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT Token
     */
    public static String generateToken(Long userId, String username) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
            .withSubject(username)
            .withClaim("userId", userId)
            .withClaim("username", username)
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
            .sign(algorithm);
    }

    /**
     * 验证JWT Token.
     *
     * @param token JWT Token
     * @return DecodedJWT 解码后的JWT
     * @throws JWTVerificationException JWT验证失败异常
     */
    public static DecodedJWT verifyToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    /**
     * 从Token中获取用户ID.
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            return jwt.getClaim("userId").asLong();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    /**
     * 从Token中获取用户名.
     *
     * @param token JWT Token
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            return jwt.getClaim("username").asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
