package com.bstek.shiro;

import org.apache.shiro.authc.AuthenticationToken;

public class ShiroToken implements AuthenticationToken {

	private static final long serialVersionUID = -5498011202539320439L;
	
	private String token;

	public ShiroToken(String token) {
		this.token = token;
	}

	@Override
	public Object getPrincipal() {
		return token;
	}

	@Override
	public Object getCredentials() {
		return token;
	}

}
