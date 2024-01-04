package com.bstek.shiro;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.bstek.common.config.PropertiesConfig;
import com.bstek.common.exception.ParamErrorException;
import com.bstek.common.utils.StringUtils;
import com.bstek.system.bean.User;

public class JWTUtil {
	
	/**
     * 获取token
     * @param u user
     * @return token
     */
    public static String getToken(User u) {
        Calendar instance = Calendar.getInstance();
        //默认令牌过期时间1天
        int expires = PropertiesConfig.getExpires();
        instance.add(Calendar.SECOND, expires);
        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("name", u.getName());
        String secretKey = PropertiesConfig.getSecretKey();
        return builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(secretKey));
    }
    
    /**
     * 验证token合法性 成功返回token
     */
    public static boolean verify(String token) {
        if(StringUtils.isEmpty(token)){
            throw new ParamErrorException("token不能为空");
        }
        String secretKey = PropertiesConfig.getSecretKey();
        JWTVerifier build = JWT.require(Algorithm.HMAC256(secretKey)).build();
         try {
			build.verify(token);
			return true;
		} catch (JWTVerificationException e) {
			return false;
		}
    }
    
    
    /**
     * 获取token参数，注意此方法不再验证token是否合法
     */
    public static Map<String, Object> getParams(String token) {
        if(StringUtils.isEmpty(token)){
            throw new ParamErrorException("token不能为空");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Claim> claims = JWT.decode(token).getClaims();
        if(claims != null && claims.size() > 0) {
        	for (Map.Entry<String, Claim> entry : claims.entrySet()) {
                String key = entry.getKey();
                Claim claim = entry.getValue();
                if(!claim.isNull()) {
                	Object value = claim.asString();
                	if(value == null) {
                		value = claim.asInt();
                	}
                	if(value == null) {
                		value = claim.asDouble();
                	}
                	if(value == null) {
                		value = claim.asBoolean();
                	}
                	if(value == null) {
                		value = claim.asDate();
                	}
                	if(value == null) {
                		value = claim.asList(Object.class);
                	}
                	params.put(key, value);
                }
            }
        }
        return params;
    }
}