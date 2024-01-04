package com.bstek.designer.bean;

import java.util.Map;

public class ShowParameters {
	
	/**
	 * 报表名称
	 */
	private String reportName;
	
	/**
	 * 不可变参数
	 */
	private Map<String, Object> params;

	/**
	 * 表单参数
	 */
	private Map<String, Object> query;
	
	/**
	 * PDF下载图表图片
	 */
	private Map<String, String> chartImages;

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
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

	public Map<String, String> getChartImages() {
		return chartImages;
	}

	public void setChartImages(Map<String, String> chartImages) {
		this.chartImages = chartImages;
	}
}
