package com.bstek.designer.bean;

import java.util.Map;

public class ShowSearchForm {
	
	/**
	 * 报表名称
	 */
	private String reportName;
	
	/**
	 * 当前变更条件
	 */
	private Map<String, Object> item;
	
	/**
	 * 不可变参数
	 */
	private Map<String, Object> params;

	/**
	 * 表单参数
	 */
	private Map<String, Object> query;

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Map<String, Object> getItem() {
		return item;
	}

	public void setItem(Map<String, Object> item) {
		this.item = item;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public Map<String, Object> getQuery() {
		return query;
	}

	public void setQuery(Map<String, Object> query) {
		this.query = query;
	}
}
