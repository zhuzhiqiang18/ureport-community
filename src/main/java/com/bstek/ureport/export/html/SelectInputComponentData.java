package com.bstek.ureport.export.html;

import java.util.List;

import com.bstek.ureport.definition.searchform.Option;

public class SelectInputComponentData  extends InputComponentData {

	/**
	 * 下拉列表数据
	 */
	private List<Option> options;
	
	/**
	 * 默认值
	 */
	private Object value;

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
