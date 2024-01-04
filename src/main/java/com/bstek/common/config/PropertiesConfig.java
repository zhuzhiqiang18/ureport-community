package com.bstek.common.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesConfig {

	private static final Map<String, Object> properties = new HashMap<String, Object>();
	
	@Value("${ureport.token.secret-key}")
	private String secretKey;
	
	@Value("${ureport.token.expires}")
	private Integer expires;
	
	@Value("${ureport.api.header.token.key}")
	private String tokenKey;
	
	@PostConstruct
	private void init() {
		if(secretKey == null) {
			secretKey = "6fa87140-3a53-11ed-ad8e-89304f903851";
		}
		properties.put("secretKey", secretKey);
		if(expires == null) {
			expires = 60 * 60 * 24;
		}
		properties.put("expires", expires);
		properties.put("tokenKey", tokenKey);
	}
	
	public static int getExpires() {
		return Integer.parseInt(String.valueOf(properties.get("expires")));
	}
	
	public static String getTokenKey() {
		return String.valueOf(properties.get("tokenKey"));
	}
	
	public static String getSecretKey() {
		return String.valueOf(properties.get("secretKey"));
	}
}
