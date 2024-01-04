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
package com.bstek.ureport.parser.impl.value;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import com.alibaba.fastjson.JSON;
import com.bstek.ureport.chart.Chart;
import com.bstek.ureport.chart.dataset.CollectType;
import com.bstek.ureport.chart.dataset.DataSet;
import com.bstek.ureport.chart.dataset.Dict;
import com.bstek.ureport.chart.dataset.Series;
import com.bstek.ureport.chart.dataset.impl.AreaDataSet;
import com.bstek.ureport.chart.dataset.impl.BarDataSet;
import com.bstek.ureport.chart.dataset.impl.BubbleDataSet;
import com.bstek.ureport.chart.dataset.impl.CategoryDataSet;
import com.bstek.ureport.chart.dataset.impl.ComboDataSet;
import com.bstek.ureport.chart.dataset.impl.DoughnutDataSet;
import com.bstek.ureport.chart.dataset.impl.HistogramDataSet;
import com.bstek.ureport.chart.dataset.impl.LineDataSet;
import com.bstek.ureport.chart.dataset.impl.PieDataSet;
import com.bstek.ureport.chart.dataset.impl.RadarDataSet;
import com.bstek.ureport.chart.dataset.impl.ScatterDataSet;
import com.bstek.ureport.definition.mapping.MappingItem;
import com.bstek.ureport.definition.mapping.MappingType;
import com.bstek.ureport.definition.value.ChartValue;
import com.bstek.ureport.definition.value.Value;
import com.bstek.ureport.exception.ReportParseException;
import com.bstek.ureport.expression.ExpressionUtils;
import com.bstek.ureport.expression.model.Condition;
import com.bstek.ureport.expression.model.Op;
import com.bstek.ureport.expression.model.condition.Join;
import com.bstek.ureport.expression.model.condition.PropertyExpressionCondition;

/**
 * @author Jacky.gao
 * @since 2017年6月28日
 */
public class ChartValueParser extends ValueParser {

	@Override
	public Value parse(Element element) {
		ChartValue value = new ChartValue();
		Chart chart = new Chart();
		value.setChart(chart);

		List<Condition> conditions = new ArrayList<Condition>();
		PropertyExpressionCondition topCondition = null;
		PropertyExpressionCondition prevCondition = null;
		value.setConditions(conditions);
		for (Object obj : element.elements()) {
			if (obj == null || !(obj instanceof Element)) {
				continue;
			}
			Element ele = (Element) obj;
			String name = ele.getName();
			if (name.equals("dataset")) {
				DataSet dataset = parseDataset(ele);
				chart.setDataset(dataset);
			} else if (name.equals("options")) {
				String op = String.valueOf(ele.getData());
				chart.setOptions(JSON.parse(op));
			} else if (name.equals("condition")) {
				PropertyExpressionCondition condition = parseCondition(ele);
				conditions.add(condition);
				if (topCondition == null) {
					topCondition = condition;
					prevCondition = topCondition;
				} else {
					prevCondition.setNextCondition(condition);
					prevCondition.setJoin(condition.getJoin());
					prevCondition = condition;
				}
			}
		}
		if (topCondition != null) {
			value.setCondition(topCondition);
		}
		return value;
	}

	private DataSet parseDataset(Element element) {
		String type = element.attributeValue("type");
		DataSet dataset = null;
		if (type.equals("line")) {
			dataset = new LineDataSet();
		} else if (type.equals("area")) {
			dataset = new AreaDataSet();
		} else if (type.equals("bar")) {
			dataset = new BarDataSet();
		} else if (type.equals("histogram")) {
			dataset = new HistogramDataSet();
		} else if (type.equals("pie")) {
			dataset = new PieDataSet();
		} else if (type.equals("doughnut")) {
			dataset = new DoughnutDataSet();
		} else if (type.equals("radar")) {
			dataset = new RadarDataSet();
		} else if (type.equals("scatter")) {
			dataset = new ScatterDataSet();
		} else if (type.equals("bubble")) {
			dataset = new BubbleDataSet();
		} else if (type.equals("combo")) {
			dataset = new ComboDataSet();
		}
		if (dataset != null && dataset instanceof CategoryDataSet) {
			CategoryDataSet ds = (CategoryDataSet) dataset;
			String datasetName = element.attributeValue("dataset-name");
			ds.setDatasetName(datasetName);
			String format = element.attributeValue("format");
			ds.setFormat(format);
			String categoryProperty = element.attributeValue("category-property");
			ds.setCategoryProperty(categoryProperty);

			List<Series> series = new ArrayList<Series>();
			Dict dict = null;
			for (Object obj : element.elements()) {
				if (obj == null || !(obj instanceof Element)) {
					continue;
				}
				Element ele = (Element) obj;
				String name = ele.getName();
				if (name.equals("series")) {
					series.add(parseSeries(ele));
				} else if (name.equals("dict")) {
					dict = parseDict(ele);
				}
			}
			ds.setDict(dict);
			ds.setSeries(series);
		}
		if (dataset != null) {
			return dataset;
		}
		throw new ReportParseException("Unknow chart type : " + type);
	}

	private Series parseSeries(Element element) {
		Series s = new Series();
		String seriesProperty = element.attributeValue("series-property");
		s.setSeriesProperty(seriesProperty);
		String collectType = element.attributeValue("collect-type");
		s.setCollectType(CollectType.valueOf(collectType));
		s.setSeriesText(element.attributeValue("series-text"));

		String format = element.attributeValue("format");
		if (StringUtils.isNotBlank(format)) {
			s.setFormat(Integer.parseInt(format));
		}
		String type = element.attributeValue("type");
		if (StringUtils.isNotBlank(type)) {
			s.setType(type);
		}
		String yAxisIndex = element.attributeValue("yAxisIndex");
		if (StringUtils.isNotBlank(yAxisIndex)) {
			s.setyAxisIndex(Boolean.valueOf(yAxisIndex));
		}
		return s;
	}

	private Dict parseDict(Element element) {
		Dict dict = new Dict();
		String mappingType = element.attributeValue("mapping-type");
		if (StringUtils.isNotBlank(mappingType)) {
			dict.setMappingType(MappingType.valueOf(mappingType));
		}
		dict.setMappingDataset(element.attributeValue("mapping-dataset"));
		dict.setMappingKeyProperty(element.attributeValue("mapping-key-property"));
		dict.setMappingValueProperty(element.attributeValue("mapping-value-property"));

		if (MappingType.simple == dict.getMappingType()) {
			List<MappingItem> mappingItems = dict.getMappingItems();
			for (Object obj : element.elements()) {
				if (obj == null || !(obj instanceof Element)) {
					continue;
				}
				Element ele = (Element) obj;
				MappingItem item = new MappingItem();
				item.setLabel(ele.attributeValue("label"));
				item.setValue(ele.attributeValue("value"));
				if (mappingItems == null) {
					mappingItems = new ArrayList<MappingItem>();
					dict.setMappingItems(mappingItems);
				}
				mappingItems.add(item);
			}
		}
		return dict;
	}

	private PropertyExpressionCondition parseCondition(Element ele) {
		PropertyExpressionCondition condition = new PropertyExpressionCondition();
		String property = ele.attributeValue("property");
		condition.setLeftProperty(property);
		condition.setLeft(property);
		String operation = ele.attributeValue("op");
		condition.setOperation(operation);
		condition.setOp(Op.parse(operation));
		for (Object o : ele.elements()) {
			if (o == null || !(o instanceof Element)) {
				continue;
			}
			Element e = (Element) o;
			if (!e.getName().equals("value")) {
				continue;
			}
			String expr = e.getTextTrim();
			condition.setRightExpression(ExpressionUtils.parseExpression(expr));
			condition.setRight(expr);
			break;
		}
		String join = ele.attributeValue("join");
		if (StringUtils.isNotBlank(join)) {
			condition.setJoin(Join.valueOf(join));
		}
		return condition;
	}
}
