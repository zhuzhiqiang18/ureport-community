package com.bstek.designer.bean;

import java.util.Map;

/**
 * 刷新表单数据
 * 主要用于出现表单级联选择问题
 * @author Administrator
 *
 */
public class PreviewSearchForm extends PreviewParameters {

	/**
	 * 当前变更条件
	 */
	private Map<String, Object> item;

	public Map<String, Object> getItem() {
		return item;
	}

	public void setItem(Map<String, Object> item) {
		this.item = item;
	}
}
