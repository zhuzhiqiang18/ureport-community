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
package com.bstek.ureport.definition.value;

import java.util.List;

import com.bstek.ureport.chart.Chart;
import com.bstek.ureport.expression.model.Condition;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Jacky.gao
 * @since 2017年6月9日
 */
public class ChartValue implements Value {

	private Chart chart;

	@JsonIgnore
	private Condition condition;

	/**
	 * 此属性给设计器使用，引擎不使用该属性
	 */
	private List<Condition> conditions;

	@Override
	public String getValue() {
		return null;
	}

	@Override
	public ValueType getType() {
		return ValueType.chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
	}

	public Chart getChart() {
		return chart;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
}
