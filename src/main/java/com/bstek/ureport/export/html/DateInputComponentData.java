package com.bstek.ureport.export.html;

public class DateInputComponentData extends InputComponentData{

	/**
	 * 时间格式
	 */
	private String format;
	
	/**
	 * 时间缺省值
	 */
	private String defaultValue;
	
	/**
	 * 默认值
	 */
	private Object value;
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public String getFormat() {
		return format;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
