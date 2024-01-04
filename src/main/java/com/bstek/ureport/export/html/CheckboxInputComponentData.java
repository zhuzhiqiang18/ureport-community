package com.bstek.ureport.export.html;

import java.util.List;

import com.bstek.ureport.definition.searchform.Option;

public class CheckboxInputComponentData extends InputComponentData{

	/**
	 * 下拉列表
	 */
	private List<Option> options;
	
	/**
	 * 默认值
	 */
	private List<Object> value;

	
	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public List<Object> getValue() {
		return value;
	}

	public void setValue(List<Object> value) {
		this.value = value;
	}
}
