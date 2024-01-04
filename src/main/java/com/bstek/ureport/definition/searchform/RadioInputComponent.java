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

import java.util.List;

import com.bstek.ureport.export.html.InputComponentData;
import com.bstek.ureport.export.html.RadioInputComponentData;

/**
 * @author Jacky.gao
 * @since 2017年10月23日
 */
public class RadioInputComponent extends InputComponent {
	
	/**
	 * 是否常量数据 0 否  1 是
	 */
	private String constant = "0";
	
	/**
	 * 下拉列表
	 */
	private List<Option> options;

	public String getConstant() {
		return constant;
	}

	public void setConstant(String constant) {
		this.constant = constant;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	@Override
	public InputComponentData buildComponent(RenderContext context) {
		RadioInputComponentData data = new RadioInputComponentData();
		super.setInputComponentData(data);
		String bindParameter = getBindParameter();
		Object value = context.getParameter(bindParameter);
		data.setValue(value);
		data.setOptions(getOptions());
		return data;
	}

	@Override
	public String getId() {
		return super.getUuid();
	}
}
