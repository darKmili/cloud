package com.cloud.encrypting_cloud_storage.util;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cloud.encrypting_cloud_storage.enums.StatusEnum;
import com.cloud.encrypting_cloud_storage.exceptions.ApiException;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon @description： @date： 2022/5/19
 * 
 * @version: 1.0
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class MyJWTUtil {
    private static String header;
    private static String secret;
    private static Long expireTime;

    private static final ConcurrentHashSet<String> invalidTokenSet = new ConcurrentHashSet<>();

    public void setHeader(String header) {
        MyJWTUtil.header = header;
    }

    public void setSecret(String secret) {
        MyJWTUtil.secret = secret;
    }

    public void setExpireTime(Long expireTime) {
        MyJWTUtil.expireTime = expireTime * 1000L * 60 * 60;
    }

    public static String createToken(UserPo userPo) {
        return JWT.create().withClaim("uid", userPo.getId()).withClaim("email", userPo.getEmail())
            .withClaim("randomValue", userPo.getClientRandomValue())
            .withExpiresAt(new Date(System.currentTimeMillis() + expireTime)).sign(Algorithm.HMAC256(secret));
    }

    public static DecodedJWT verifyToken(String token) {
        if (token == null || token.length() == 0) {
            throw new ApiException(StatusEnum.NOT_LOGIN);
        }
        if (invalidTokenSet.contains(token)) {
            throw new ApiException(StatusEnum.NOT_LOGIN);
        }
        try {
            return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);

        } catch (TokenExpiredException e) {
            throw new ApiException(-2, "token已经过期");
        } catch (Exception e) {
            throw new ApiException(StatusEnum.NOT_LOGIN);
        }
    }

    /**
     * 是否需要更新token
     *
     * @param token
     * @return
     */
    public static Boolean isNeedUpdateToken(String token) {
        Date expiresAt = null;
        try {
            expiresAt = JWT.require(Algorithm.HMAC256(secret)).build().verify(token).getExpiresAt();
        } catch (TokenExpiredException e) {
            // 已经过期，需要更新
            return true;
        } catch (Exception e) {
            throw new ApiException(StatusEnum.NOT_LOGIN);
        }
        return (expiresAt.getTime() - System.currentTimeMillis()) < (expireTime >> 2);

    }

    public static void invalidToken(String token) {
        invalidTokenSet.add(token);
    }

}
