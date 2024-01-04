package com.bstek.designer.bean;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PreviewParameters {

	/**
	 * 签名
	 */
	@JsonIgnore
	private String sign;
	
	/**
	 * 报表内容
	 */
	private String content;
	
	/**
	 * 报表名称
	 */
	private String reportName;
	
	/**
	 * 表单参数
	 */
	private Map<String, Object> query;
	
	/**
	 * 表单对应组件唯一标识
	 */
	private Map<String, String> component;
	
	/**
	 * 图表图片目前用于PDF导出
	 */
	private Map<String, String> chartImages;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
	
	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public Map<String, String> getComponent() {
		return component;
	}

	public void setComponent(Map<String, String> component) {
		this.component = component;
	}

	@Override
	public String toString() {
		return "PreviewParameters [sign=" + sign + ", content=" + content + ", query=" + query.toString() + "]";
	}
}
