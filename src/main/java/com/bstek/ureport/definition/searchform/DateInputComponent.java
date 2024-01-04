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

import com.bstek.ureport.definition.datasource.DataType;
import com.bstek.ureport.export.html.DateInputComponentData;
import com.bstek.ureport.export.html.InputComponentData;

/**
 * @author Jacky.gao
 * @since 2016年1月11日
 */
public class DateInputComponent extends InputComponent {
	
	/**
	 * 时间格式
	 */
	private String format;
	
	/**
	 * 时间缺省值
	 */
	private String defaultValue;
	
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

	@Override
	public InputComponentData buildComponent(RenderContext context) {
		DateInputComponentData data = new DateInputComponentData();
		super.setInputComponentData(data);
		
		data.setFormat(getFormat());
		data.setDefaultValue(getDefaultValue());
		
		String bindParameter = getBindParameter();
		Object value = context.getParameter(bindParameter);
		data.setValue(DataType.Date.parse(value));
		return data;
	}

	@Override
	public String getId() {
		return super.getUuid();
	}
}
