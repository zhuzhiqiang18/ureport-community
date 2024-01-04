package com.bstek.shiro;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;

import com.bstek.common.utils.StringUtils;

public class TokenFilter extends AuthenticatingFilter {

	public static final String X_TOKEN = "X-Token";
		
	@Override
	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
		String token = this.getToken((HttpServletRequest)request);
		if(StringUtils.isBlank(token)){
			return null;
		}
		return new ShiroToken(token);
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		String token = this.getToken((HttpServletRequest)request);
		if(StringUtils.isBlank(token)){
			message((HttpServletResponse)response);
			return false;
		}
		boolean isOk = JWTUtil.verify(token);
		if(!isOk) {
			message((HttpServletResponse)response);
		}
		return isOk;
	}
	
	private void message(HttpServletResponse response) throws IOException {
		PrintWriter writer = response.getWriter();
		String str = "{\"code\":401,\"message\":\"Unauthorized\"}";
		writer.write(str);
	}

	/**
	 * 获取前端携带token
	 * @param request
	 * @return
	 */
	private String getToken(HttpServletRequest request) {
        String token = request.getHeader(X_TOKEN);
        if(StringUtils.isBlank(token)) {
        	token = request.getParameter(X_TOKEN);
        	if(StringUtils.isBlank(token)) {
        		Cookie[] cookies = request.getCookies();
        		if(cookies != null && cookies.length > 0) {
            		for (Cookie cookie : cookies) {
                		String name = cookie.getName();
                		if(X_TOKEN.equals(name)) {
                			token = cookie.getValue();
                			break;
                		}
        			}
            	}
        	}
        }
        return token;
    }
}
