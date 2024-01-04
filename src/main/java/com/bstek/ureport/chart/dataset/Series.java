package com.bstek.ureport.chart.dataset;

public class Series {

	private String seriesProperty;
	
	private String seriesText;
	
	private CollectType collectType = CollectType.select;
	
	private Integer format;
	
	private String type;
	
	private Boolean yAxisIndex;
	
	public String getSeriesProperty() {
		return seriesProperty;
	}

	public void setSeriesProperty(String seriesProperty) {
		this.seriesProperty = seriesProperty;
	}

	public String getSeriesText() {
		return seriesText;
	}

	public void setSeriesText(String seriesText) {
		this.seriesText = seriesText;
	}

	public CollectType getCollectType() {
		return collectType;
	}

	public void setCollectType(CollectType collectType) {
		this.collectType = collectType;
	}

	public Integer getFormat() {
		return format;
	}

	public void setFormat(Integer format) {
		this.format = format;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getyAxisIndex() {
		return yAxisIndex;
	}

	public void setyAxisIndex(Boolean yAxisIndex) {
		this.yAxisIndex = yAxisIndex;
	}
}
