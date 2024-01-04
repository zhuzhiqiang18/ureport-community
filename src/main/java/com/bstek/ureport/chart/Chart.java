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

import com.alibaba.fastjson.JSON;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.chart.dataset.DataSet;
import com.bstek.ureport.model.Cell;

/**
 * @author Jacky.gao
 * @since 2017年6月8日
 */
public class Chart {
	
	private DataSet dataset;
	
	private Object options;
	
	public ChartData doCompute(Cell cell, Context context) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"type\":\"" + dataset.getType() + "\",");
		sb.append(dataset.buildDataJson(context, cell));
		sb.append("\"options\":");
		String jsonOptions = JSON.toJSONString(options);
		sb.append(jsonOptions);
		sb.append("}");
		ChartData chartData = new ChartData(sb.toString(), cell);
		context.addChartData(chartData);
		return chartData;
	}

	public Object getOptions() {
		return options;
	}

	public void setOptions(Object options) {
		this.options = options;
	}

	public DataSet getDataset() {
		return dataset;
	}

	public void setDataset(DataSet dataset) {
		this.dataset = dataset;
	}
}
