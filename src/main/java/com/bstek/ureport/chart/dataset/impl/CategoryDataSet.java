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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.chart.dataset.CollectType;
import com.bstek.ureport.chart.dataset.DataSet;
import com.bstek.ureport.chart.dataset.Dict;
import com.bstek.ureport.chart.dataset.Series;
import com.bstek.ureport.definition.mapping.MappingItem;
import com.bstek.ureport.definition.mapping.MappingType;
import com.bstek.ureport.definition.value.ChartValue;
import com.bstek.ureport.definition.value.Value;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.expression.model.Condition;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.utils.DataUtils;

/**
 * @author Jacky.gao
 * @since 2017年6月9日
 */
public abstract class CategoryDataSet implements DataSet {

	private String datasetName;

	protected String categoryProperty;

	private List<Series> series;

	private String format;

	protected Dict dict;

	protected Map<String, Object> dictData = new HashMap<String, Object>();

	protected boolean isDict = false;

	protected String buildDataSetJson(Context context, Cell cell, String props) {
		// 字典映射
		if (StringUtils.isNotBlank(categoryProperty)) {
			isDict = buildDict(context);
		}
		List<?> dataList = DataUtils.fetchData(cell, context, datasetName);
		Map<Object, Map<Object, List<Object>>> cacheData = new LinkedHashMap<Object, Map<Object, List<Object>>>();
		Condition condition = getCondition(cell);
		for (Object obj : dataList) {
			Object category = Utils.getProperty(obj, categoryProperty);
			if (category == null) {
				continue;
			}
			// 过滤条件
			if (condition != null && !condition.filter(cell, cell, obj, context)) {
				continue;
			}
			// 格式化分类
			category = toLabel(category);
			Map<Object, List<Object>> row = cacheData.get(category);
			if (row == null) {
				row = new LinkedHashMap<Object, List<Object>>();
				cacheData.put(category, row);
			}
			for (Series s : series) {
				String seriesProperty = s.getSeriesProperty();
				Object value = Utils.getProperty(obj, seriesProperty);
				if (value == null) {
					continue;
				}
				List<Object> valueList = row.get(s);
				if (valueList == null) {
					valueList = new ArrayList<Object>();
					row.put(s, valueList);
				}
				valueList.add(value);
			}
		}
		return buildDataSets(cacheData, props);
	}

	protected String buildDataSets(Map<Object, Map<Object, List<Object>>> map, String props) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Object, Map<Object, List<Object>>> entry : map.entrySet()) {
			Object category = entry.getKey();
			sb.append("{");

			Map<Object, List<Object>> series = entry.getValue();
			for (Map.Entry<Object, List<Object>> o : series.entrySet()) {
				Series s = (Series) o.getKey();
				CollectType collectType = s.getCollectType();
				Integer decimal = s.getFormat();
				List<Object> values = o.getValue();
				double value = collectData(values, collectType);
				sb.append("\"" + s.getSeriesText() + "\":" + Utils.format(value, decimal) + ",");
			}
			if (isDict) {
				sb.append("\"category\":\"" + dictData.get(String.valueOf(category)) + "\"");
			} else {
				sb.append("\"category\":\"" + category + "\"");
			}
			sb.append("},");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	protected double collectData(List<Object> list, CollectType collectType) {
		double result = 0;
		if (list.size() == 0) {
			return result;
		}
		switch (collectType) {
		case select:
			result = Utils.toBigDecimal(list.get(0)).doubleValue();
			break;
		case avg:
			double total = 0;
			for (Object data : list) {
				total += Utils.toBigDecimal(data).doubleValue();
			}
			result = Utils.toBigDecimal(total).divide(Utils.toBigDecimal(list.size()), 8, BigDecimal.ROUND_HALF_UP)
					.doubleValue();
			break;
		case count:
			result = list.size();
			break;
		case max:
			Double max = null;
			for (Object data : list) {
				double value = Utils.toBigDecimal(data).doubleValue();
				if (max == null) {
					max = value;
				} else if (max < value) {
					max = value;
				}
			}
			result = max;
			break;
		case min:
			Double min = null;
			for (Object data : list) {
				double value = Utils.toBigDecimal(data).doubleValue();
				if (min == null) {
					min = value;
				} else if (min > value) {
					min = value;
				}
			}
			result = min;
			break;
		case sum:
			for (Object data : list) {
				double value = Utils.toBigDecimal(data).doubleValue();
				result += value;
			}
			break;
		}
		return result;
	}

	protected String getLabels() {
		StringBuilder sb = new StringBuilder("\"category\",");
		for (Series s : series) {
			sb.append("\"" + s.getSeriesText() + "\",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	protected Object toLabel(Object category) {
		if (StringUtils.isNotBlank(format)) {
			if (category instanceof Date) {
				Date date = (Date) category;
				SimpleDateFormat sd = new SimpleDateFormat(format);
				return sd.format(date);
			}
			try {
				BigDecimal data = Utils.toBigDecimal(category);
				DecimalFormat df = new DecimalFormat(format);
				return df.format(data.doubleValue());
			} catch (Exception ex) {
				throw new ReportComputeException("Can not format data [" + category + "] use pattern [" + format + "]");
			}
		}
		return category;
	}

	protected boolean buildDict(Context context) {
		if (dict == null) {
			return false;
		}
		if (MappingType.simple == dict.getMappingType()) {
			List<MappingItem> mappingItems = dict.getMappingItems();
			if (mappingItems == null || mappingItems.size() == 0) {
				return false;
			}
			for (MappingItem mappingItem : mappingItems) {
				dictData.put(mappingItem.getValue(), mappingItem.getLabel());
			}
			return true;
		}
		if (MappingType.dataset == dict.getMappingType()) {
			String name = dict.getMappingDataset();
			if (StringUtils.isBlank(name)) {
				return false;
			}
			String keyProperty = dict.getMappingKeyProperty();
			if (StringUtils.isBlank(keyProperty)) {
				return false;
			}
			String valueProperty = dict.getMappingValueProperty();
			if (StringUtils.isBlank(valueProperty)) {
				return false;
			}
			List<?> dataList = context.getDatasetData(name);
			for (Object obj : dataList) {
				String key = String.valueOf(Utils.getProperty(obj, keyProperty));
				Object value = Utils.getProperty(obj, valueProperty);
				if (key != null && value != null) {
					dictData.put(key, value);
				}
			}
			return true;
		}
		return false;
	}

	protected String getIndicator() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		int index = 0;
		for (Series s : series) {
			if (index > 0) {
				sb.append(",");
			}
			sb.append("\"").append(s.getSeriesText()).append("\"");
			index++;
		}
		sb.append("]");
		return sb.toString();
	}

	protected Condition getCondition(Cell cell) {
		Value value = cell.getValue();
		Condition condition = null;
		if (value instanceof ChartValue) {
			ChartValue chartValue = (ChartValue) value;
			condition = chartValue.getCondition();
		}
		return condition;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getCategoryProperty() {
		return categoryProperty;
	}

	public void setCategoryProperty(String categoryProperty) {
		this.categoryProperty = categoryProperty;
	}

	public List<Series> getSeries() {
		return series;
	}

	public void setSeries(List<Series> series) {
		this.series = series;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Dict getDict() {
		return dict;
	}

	public void setDict(Dict dict) {
		this.dict = dict;
	}
}
