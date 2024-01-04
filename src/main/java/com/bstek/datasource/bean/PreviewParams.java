package com.bstek.datasource.bean;

import java.util.List;

import com.bstek.ureport.definition.dataset.Parameter;

public class PreviewParams {
	
	private DataSourceInfo info;
	
	private String sql;
	
	private List<Parameter> parameters;

	public DataSourceInfo getInfo() {
		return info;
	}

	public void setInfo(DataSourceInfo info) {
		this.info = info;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
}
