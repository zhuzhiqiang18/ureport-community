package com.bstek.system.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bstek.common.exception.ParamErrorException;
import com.bstek.common.utils.StringUtils;
import com.bstek.shiro.JWTUtil;
import com.bstek.system.bean.User;
import com.github.benmanes.caffeine.cache.Cache;

@RestController
@RequestMapping("/sys/user")
public class LoginAction {
	
	@Resource(name="caffeineCacheNonce")
	private Cache<String, String> caffeineCacheNonce;
	
	@Value("${ureport.username}")
	private String username;
	
	@Value("${ureport.password}")
	private String password;
	
	@RequestMapping("/digest")
	public String digest() {
		String uuid = StringUtils.getUUID();
		caffeineCacheNonce.put(uuid, "");
		return uuid;
	}
	
	@RequestMapping("/login")
	public Map<String,Object> login(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		if(StringUtils.isNotBlank(authorization)) {
			String params = "{\"" + authorization.replaceFirst("Digest", "").trim().replaceAll("=", "\":\"").replaceAll(",", "\",\"") + "\"}";
			Map<String,String> data = JSON.parseObject(params, new TypeReference<Map<String, String>>() {});
			String nonce = data.get("nonce");
			String nc = data.get("nc");
			String cacheNonce = caffeineCacheNonce.asMap().get(nonce);
			if(cacheNonce == null || nc.equals(cacheNonce)) {
				throw new ParamErrorException("用户名或密码错误");
			}
			//String name = data.get("username");
			String cnonce = data.get("cnonce");
			String response = data.get("response");
			String h = DigestUtils.md5Hex(DigestUtils.md5Hex(username + password) + nonce + nc + cnonce);
			if(response.equals(h)) {
				User user = new User();
				user.setName(username);
				String token = JWTUtil.getToken(user);
				Map<String,Object> r = new HashMap<String, Object>();
				r.put("token", token);
				caffeineCacheNonce.put(nonce, nc);
				return r;
			}
		}
		throw new ParamErrorException("用户名或密码错误");
	}
	
	@RequestMapping("/info")
	public User info() {
		User user = new User();
		user.setName(username);
		return user;
	}
	
	@RequestMapping("/logout")
	public String login(String token) {
		return "logout";
	}
}
