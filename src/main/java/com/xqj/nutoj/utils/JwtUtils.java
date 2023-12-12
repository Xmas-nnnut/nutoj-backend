package com.xqj.nutoj.utils;

import cn.hutool.crypto.asymmetric.RSA;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.xqj.nutoj.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Jwt工具类
 */
//@Component
//@ConfigurationProperties(prefix = "jwt")
public class JwtUtils {


    // 签名密钥和过期时间
    private static final String SECRET = "!DAR$xqj";
    private static final Integer expireTime = 7;

    /**
     * 根据用户信息⽣成token
     * @param user 用户信息
     * @return token字符串
     */
    public static String getTokenByUser(User user){
        // 用户信息
        String userId = String.valueOf(user.getId());
        String userName = user.getUserName();
        // 指定token过期时间为7天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, expireTime);
        JWTCreator.Builder builder = JWT.create();
        // payload 添加需要的东西
        Map<String, String> payload = new HashMap<>();
        payload.put("id",userId);
        payload.put("name",userName);
        // 构建payload
        payload.forEach((k,v) -> builder.withClaim(k,v));
        // 指定过期时间和签名算法
        String token = builder.withExpiresAt(calendar.getTime()).sign(Algorithm.HMAC256(SECRET));
        return token;
    }

    /**
     * ⽣成token
     * @param payload token携带的信息
     * @return token字符串
     */
    public static String getToken(Map<String,String> payload){
        // 指定token过期时间为7天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 7);
        JWTCreator.Builder builder = JWT.create();
        // 构建payload
        payload.forEach((k,v) -> builder.withClaim(k,v));
        // 指定过期时间和签名算法
        String token = builder.withExpiresAt(calendar.getTime()).sign(Algorithm.HMAC256(SECRET));
        return token;
    }

    /**
     * 解析token
     * @param token token字符串
     * @return 解析后的token
     */
    public static DecodedJWT decode(String token){
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return decodedJWT;
    }

    // 非对称签名
    private static final String RSA_PRIVATE_KEY = "!RSA";
    private static final String RSA_PUBLIC_KEY = "!RSA";
    /**
     * ⽣成token
     * @param payload token携带的信息
     * @return token字符串
     */
    public static String getTokenRsa(Map<String,String> payload){
        // 指定token过期时间为7天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, expireTime);
        JWTCreator.Builder builder = JWT.create();
        // 构建payload
        payload.forEach((k,v) -> builder.withClaim(k,v));
        // 利⽤hutool创建RSA
        RSA rsa = new RSA(RSA_PRIVATE_KEY, null);
        // 获取私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) rsa.getPrivateKey();
        // 签名时传⼊私钥
        String token = builder.withExpiresAt(calendar.getTime()).sign(Algorithm.RSA256(null, privateKey));
        return token;
    }
    /**
     * 解析token
     * @param token token字符串
     * @return 解析后的token
     */
    public static DecodedJWT decodeRsa(String token){
        // 利⽤hutool创建RSA
        RSA rsa = new RSA(null, RSA_PUBLIC_KEY);
        // 获取RSA公钥
        RSAPublicKey publicKey = (RSAPublicKey) rsa.getPublicKey();
        // 验签时传⼊公钥
        JWTVerifier jwtVerifier = JWT.require(Algorithm.RSA256(publicKey, null
        )).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return decodedJWT;
    }

}