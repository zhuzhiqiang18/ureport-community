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

import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.export.html.InputComponentData;
import com.bstek.ureport.export.html.SearchFormData;

/**
 * @author Jacky.gao
 * @since 2017年10月23日
 */
public class SearchForm {

	/**
	 * 表单组件
	 */
	private List<Component> components;

	public SearchFormData builder(RenderContext context) {
		SearchFormData data = new SearchFormData();
		List<InputComponentData> items = new ArrayList<InputComponentData>();
		for (Component component : components) {
			items.add(component.buildComponent(context));
		}
		data.setItems(items);
		return data;
	}

	/**
	 * 刷新表单，用于表单多级联动，仅对数据集表单有效
	 */
	public List<Option> refersh(Component component, Dataset dataset) {
		String valueField = null;
		String labelField = null;
		if(component instanceof SelectInputComponent) {
			SelectInputComponent selectInputComponent = (SelectInputComponent) component;
			valueField = selectInputComponent.getValueField();
			labelField = selectInputComponent.getLabelField();
		} else if(component instanceof MultipleSelectInputComponent) {
			MultipleSelectInputComponent multipleSelectInputComponent = (MultipleSelectInputComponent) component;
			valueField = multipleSelectInputComponent.getValueField();
			labelField = multipleSelectInputComponent.getLabelField();
		}
		List<Option> options = new ArrayList<Option>();
		for (Object obj : dataset.getData()) {
			Object label = Utils.getProperty(obj, labelField);
			Object value = Utils.getProperty(obj, valueField);
			Option option = new Option();
			option.setLabel(String.valueOf(label));
			option.setValue(String.valueOf(value));
			options.add(option);
		}
		return options;
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}
}
