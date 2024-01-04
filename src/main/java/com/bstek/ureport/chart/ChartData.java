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
package com.bstek.ureport.chart;

import org.springframework.util.DigestUtils;

import com.bstek.ureport.model.Cell;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Jacky.gao
 * @since 2017年6月16日
 */
public class ChartData {

	private String id;
	
	private String json;
	
	@JsonIgnore
	private int width;
	
	@JsonIgnore
	private int height;

	public ChartData(String json, Cell cell) {
		String key = DigestUtils.md5DigestAsHex(("chart_" + json).getBytes());
		this.json = json;
		this.id = key;
	}

	public String getJson() {
		return json;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getId() {
		return id;
	}
}
