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
package com.bstek.ureport.definition.searchform;

import java.util.ArrayList;
import java.util.List;

import com.bstek.common.utils.StringUtils;
import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.export.html.InputComponentData;
import com.bstek.ureport.export.html.SelectInputComponentData;

/**
 * @author Jacky.gao
 * @since 2017年10月23日
 */
public class SelectInputComponent extends InputComponent {
	
	/**
	 * 是否常量数据 0 否  1 是
	 */
	private String constant = "0";
	
	/**
	 * 数据集
	 */
	private String dataset;
	
	/**
	 * 标签对应字段
	 */
	private String labelField;
	
	/**
	 * 值对应字段
	 */
	private String valueField;
	
	/**
	 * 下拉列表数据
	 */
	private List<Option> options;
	
	public String getConstant() {
		return constant;
	}

	public void setConstant(String constant) {
		this.constant = constant;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public String getLabelField() {
		return labelField;
	}

	public void setLabelField(String labelField) {
		this.labelField = labelField;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	@Override
	public InputComponentData buildComponent(RenderContext context) {
		SelectInputComponentData data = new SelectInputComponentData();
		super.setInputComponentData(data);
		String bindParameter = getBindParameter();
		Object value = context.getParameter(bindParameter);
		data.setValue(value);
		if (StringUtils.isNotBlank(constant) && "1".equals(constant)) {
			String datasetName = getDataset();
			Dataset dataset = context.getDataset(datasetName);
			List<Option> options = new ArrayList<Option>();
			for (Object obj : dataset.getData()) {
				Object optionLabel = Utils.getProperty(obj, labelField);
				Object optionValue = Utils.getProperty(obj, valueField);
				Option option = new Option();
				option.setLabel(String.valueOf(optionLabel));
				option.setValue(String.valueOf(optionValue));
				options.add(option);
			}
			data.setOptions(options);
		} else {
			data.setOptions(getOptions());
		}
		return data;
	}

	@Override
	public String getId() {
		return super.getUuid();
	}
}
