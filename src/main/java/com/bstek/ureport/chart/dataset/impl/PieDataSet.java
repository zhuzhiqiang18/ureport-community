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
import com.bstek.ureport.chart.dataset.CollectType;
import com.bstek.ureport.chart.dataset.Series;
import com.bstek.ureport.expression.model.Condition;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.utils.DataUtils;

/**
 * @author Jacky.gao
 * @since 2017年6月8日
 */
public class PieDataSet extends CategoryDataSet {
	
	private String seriesText = "系列一";
	
	@Override
	public String buildDataJson(Context context, Cell cell) {
		String datasetJson = buildDataSetJson(context, cell, null);
		StringBuilder sb = new StringBuilder();
		sb.append("\"dataset\":[" + datasetJson + "],");
		sb.append("\"dimensions\":[\"name\",\"" + seriesText + "\"],");
		return sb.toString();
	}
	
	@Override
	public String getType() {
		return "pie";
	}
	
	@Override
	protected String buildDataSetJson(Context context, Cell cell, String props) {
		String categoryProperty = super.getCategoryProperty();
		//字典映射
		if(StringUtils.isNotBlank(categoryProperty)) {
			super.isDict = buildDict(context);
		}
		String datasetName = super.getDatasetName();
		List<?> dataList = DataUtils.fetchData(cell, context, datasetName);
		Map<Object,List<Object>> seriesDataMap = new LinkedHashMap<Object,List<Object>>();
		
		List<Series> series = super.getSeries();
		CollectType collectType = CollectType.select;
		Integer decimal = null;
		
		Condition condition = getCondition(cell);
		for (Object obj : dataList) {
			// 过滤条件
			if (condition != null && !condition.filter(cell, cell, obj, context)) {
				continue;
			}
			//一个分类一个指标构成饼图
			if (StringUtils.isNotBlank(categoryProperty)) {
				Object category = Utils.getProperty(obj, categoryProperty);
				if (category == null) {
					continue;
				}
				//格式化分类
				category = toLabel(category);
				if(series != null && series.size() >0) {
					Series s = series.get(0);
					collectType = s.getCollectType();
					decimal = s.getFormat();
					seriesText = s.getSeriesText();
					String seriesProperty = s.getSeriesProperty();
					Object value = Utils.getProperty(obj, seriesProperty);
					if (value == null) {
						continue;
					}
					List<Object> valueList = seriesDataMap.get(category);
					if(valueList == null) {
						valueList = new ArrayList<Object>();
						seriesDataMap.put(category, valueList);
					}
					valueList.add(value);
				}
				
			} else {//没有分类，多个指标构成饼图
				for (Series s : series) {
					String seriesProperty = s.getSeriesProperty();
					Object value = Utils.getProperty(obj, seriesProperty);
					if (value == null) {
						continue;
					}
					List<Object> valueList = seriesDataMap.get(s);
					if(valueList == null) {
						valueList = new ArrayList<Object>();
						seriesDataMap.put(s, valueList);
					}
					valueList.add(value);
				}
			}
		}
		return buildDataSets(seriesDataMap, collectType, decimal, seriesText, props);
	}

	
	private String buildDataSets(Map<Object,List<Object>> map, CollectType collectType, Integer decimal, String seriesText, String props) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : map.keySet()) {
			String name = String.valueOf(obj);
			if (obj instanceof Series) {
				Series s = (Series) obj;
				name = s.getSeriesText();
				collectType = s.getCollectType();
				decimal = s.getFormat();
			} else if (super.isDict) {
				name = String.valueOf(dictData.get(name));
			}
			List<Object> list = map.get(obj);
			double data = collectData(list,collectType);
			
			sb.append("{\"name\":\"").append(name).append("\",\""+ seriesText + "\":").append(Utils.format(data, decimal)).append("},");
			
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
}
