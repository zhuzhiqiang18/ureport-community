package com.bstek.datasource.bean;

import java.util.List;
import java.util.Map;

import com.bstek.ureport.definition.dataset.Parameter;

public class ApiParams {
	
	private String method;
	
	private String url;
	
	private List<Parameter> parameters;
	
	private List<Map<String,String>> headers;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public List<Map<String, String>> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Map<String, String>> headers) {
		this.headers = headers;
	}
}
