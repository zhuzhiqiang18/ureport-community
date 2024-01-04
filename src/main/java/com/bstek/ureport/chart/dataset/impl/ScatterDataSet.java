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
package com.bstek.ureport.chart.dataset.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bstek.common.utils.StringUtils;
import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.chart.dataset.Series;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.expression.model.Condition;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.utils.DataUtils;

/**
 * @author Jacky.gao
 * @since 2017年6月8日
 */
public class ScatterDataSet extends CategoryDataSet {
	
	@Override
	public String buildDataJson(Context context, Cell cell) {
		if(StringUtils.isBlank(categoryProperty)) {
			String name = cell.getName();
			throw new ReportComputeException("Cell ["+ name +"] scatter-chart categoryProperty is null");
		}
		String datasetJson = buildDataSetJson(context, cell, null);
		StringBuilder sb = new StringBuilder();
		sb.append("\"dataset\":[" + datasetJson + "],");
		String labels = getLabels();
		sb.append("\"dimensions\":[" + labels + "],");
		return sb.toString();
	}
	
	
	protected String buildDataSetJson(Context context, Cell cell, String props) {
		String categoryProperty = super.getCategoryProperty();
		//字典映射
		if(StringUtils.isNotBlank(categoryProperty)) {
			super.isDict = buildDict(context);
		}
		String datasetName = super.getDatasetName();
		List<?> dataList = DataUtils.fetchData(cell, context, datasetName);
		Map<Object,Map<Object,List<Object>>> seriesDataMap = new LinkedHashMap<Object,Map<Object,List<Object>>>();
		
		Condition condition = getCondition(cell);
		List<Series> series = super.getSeries();
		for (Object obj : dataList) {
			Object category = Utils.getProperty(obj, categoryProperty);
			if (category == null) {
				continue;
			}
			// 过滤条件
			if (condition != null && !condition.filter(cell, cell, obj, context)) {
				continue;
			}
			//格式化分类
			category = toLabel(category);
			Map<Object,List<Object>> categoryMap = seriesDataMap.get(category);
			if(categoryMap == null) {
				categoryMap = new LinkedHashMap<Object,List<Object>>();
				seriesDataMap.put(category, categoryMap);
			}
			//第一个为X轴第二个为Y轴第三个为球体大小
			for (int i = 0; i < series.size() && i < 3; i++) {
				Series s = series.get(i);
				String seriesProperty = s.getSeriesProperty();
				Object value = Utils.getProperty(obj, seriesProperty);
				if (value == null) {
					continue;
				}
				List<Object> valueList = categoryMap.get(s);
				if(valueList == null) {
					valueList = new ArrayList<Object>();
					categoryMap.put(s, valueList);
				}
				valueList.add(value);
			}
		}
		return buildDataSets(seriesDataMap, props);
	}
	
	@Override
	protected String buildDataSets(Map<Object, Map<Object, List<Object>>> map, String props) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : map.keySet()) {
			sb.append("{");
			Map<Object, List<Object>> series = map.get(obj);
			for (Object o : series.keySet()) {
				Series s = (Series) o;
				String seriesText = s.getSeriesText();
				List<Object> list = series.get(o);
				double data = collectData(list,s.getCollectType());
				Integer decimal = s.getFormat();
				sb.append("\""+ seriesText +"\":").append(Utils.format(data, decimal)).append(",");
			}
			if(super.isDict) {
				sb.append("\"category\":\"" + dictData.get(String.valueOf(obj))).append("\"");
			} else {
				sb.append("\"category\":\"" + obj).append("\"");
			}
			sb.append("},");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	
	@Override
	public String getType() {
		return "scatter";
	}
}
