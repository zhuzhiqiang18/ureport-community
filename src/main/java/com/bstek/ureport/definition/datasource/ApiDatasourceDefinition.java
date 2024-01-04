/*******************************************************************************
 * Copyright 2017 Bstek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.definition.datasource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.bstek.common.config.PropertiesConfig;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.definition.dataset.DatasetDefinition;
import com.bstek.ureport.definition.dataset.Parameter;
import com.bstek.ureport.exception.ReportComputeException;

/**
 * @author Jacky.gao
 * @since 2017年2月9日
 */
public class ApiDatasourceDefinition implements DatasourceDefinition {

	private String name;
	
	private String url;

	private String method;

	private List<DatasetDefinition> datasets;
	
	private List<Parameter> parameters;
	
	private List<Map<String,String>> headers;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	private String token;

	public List<Dataset> buildDatasets(Map<String, Object> params) {
		if (datasets == null || datasets.size() == 0) {
			return Collections.emptyList();
		}
		Map<String,Boolean> cache = new HashMap<String, Boolean>();
		for (DatasetDefinition datasetDefinition : datasets) {
			cache.put(datasetDefinition.getName(), true);
		}
		List<Dataset> result = new ArrayList<Dataset>();
		List<Dataset> list = null;
		if("GET".equals(method)) {
			list = doGet(params);
		} else if("POST".equals(method)) {
			list = doPost(params);
		}
		if(list != null && list.size() > 0) {
			for (Dataset dataset : list) {
				if(cache.get(dataset.getName()) != null) {
					result.add(dataset);
				}
			}
		}
		return result;
	}
	
	public List<Dataset> doGet(Map<String, Object> params) {
		HttpHeaders headers = new HttpHeaders();
		for (Map<String,String> map : this.headers) {
			headers.add(map.get("name"), map.get("value"));
		}
		headers.add(PropertiesConfig.getTokenKey(), token);
        Map<String,Object> p = buildParameters(params);
		HttpEntity<Object> entity = new HttpEntity<>(p, headers);
		try {
			String linkURL = url;
			String urlParameter = buildLinkParameters(p);
			if (StringUtils.isNotBlank(urlParameter)) {
				if (linkURL.indexOf("?") == -1) {
					linkURL += "?" + urlParameter;
				} else {
					linkURL += "&" + urlParameter;
				}
			}
			ResponseEntity<List<Dataset>> responseEntity = restTemplate.exchange(linkURL, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Dataset>>() {});
			return responseEntity.getBody();
		} catch (RestClientException e) {
			e.printStackTrace();
			throw new ReportComputeException(e.getMessage());
		}
	}
	
	public List<Dataset> doPost(Map<String, Object> params) {
		HttpHeaders headers = new HttpHeaders();
		for (Map<String,String> map : this.headers) {
			headers.add(map.get("name"), map.get("value"));
		}
		// 以json方式提交
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
		headers.add(PropertiesConfig.getTokenKey(), token);
        Map<String,Object> p = buildParameters(params);
		HttpEntity<Object> entity = new HttpEntity<>(p, headers);
		try {
			ResponseEntity<List<Dataset>> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<List<Dataset>>() {});
			return responseEntity.getBody();
		} catch (RestClientException e) {
			e.printStackTrace();
			throw new ReportComputeException(e.getMessage());
		}
	}
	
	private Map<String,Object> buildParameters(Map<String, Object> params) {
		Map<String,Object> map = new HashMap<String, Object>();
		for (Parameter param : parameters) {
			String name = param.getName();
			DataType datatype = param.getType();
			Object value = param.getDefaultValue();
			if (params != null && params.containsKey(name)) {
				value = params.get(name);
			}
			map.put(name, datatype.parse(value));
		}
		return map;
	}
	
	private String buildLinkParameters(Map<String,Object> params) {
		StringBuilder sb = new StringBuilder();
		if (params != null) {
			int i = 0;
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				String name = entry.getKey();
				String value = String.valueOf(entry.getValue());
				try {
					value = URLEncoder.encode(value, "utf-8");
					value = URLEncoder.encode(value, "utf-8");
				} catch (UnsupportedEncodingException e) {
					throw new ReportComputeException(e);
				}
				if (i > 0) {
					sb.append("&");
				}
				sb.append(name + "=" + value);
				i++;
			}
		}
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<DatasetDefinition> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<DatasetDefinition> datasets) {
		this.datasets = datasets;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public List<Map<String, String>> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Map<String, String>> headers) {
		this.headers = headers;
	}

	@Override
	public DatasourceType getType() {
		return DatasourceType.api;
	}
}
