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
package com.bstek.ureport.parser.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import com.bstek.ureport.definition.dataset.ApiDatasetDefinition;
import com.bstek.ureport.definition.dataset.BeanDatasetDefinition;
import com.bstek.ureport.definition.dataset.DatasetDefinition;
import com.bstek.ureport.definition.dataset.Field;
import com.bstek.ureport.definition.dataset.Parameter;
import com.bstek.ureport.definition.dataset.SqlDatasetDefinition;
import com.bstek.ureport.definition.datasource.ApiDatasourceDefinition;
import com.bstek.ureport.definition.datasource.BuildinDatasourceDefinition;
import com.bstek.ureport.definition.datasource.DataType;
import com.bstek.ureport.definition.datasource.DatasourceDefinition;
import com.bstek.ureport.definition.datasource.JdbcDatasourceDefinition;
import com.bstek.ureport.expression.ExpressionUtils;
import com.bstek.ureport.expression.model.Expression;
import com.bstek.ureport.parser.Parser;

/**
 * @author Jacky.gao
 * @since 2016年12月30日
 */
public class DatasourceParser implements Parser<DatasourceDefinition> {
	@Override
	public DatasourceDefinition parse(Element element) {
		String type = element.attributeValue("type");
		if (type.equals("jdbc")) {
			String code = element.attributeValue("code");
			String name = element.attributeValue("name");
			String username = element.attributeValue("username");
			String password = element.attributeValue("password");
			String driver = element.attributeValue("driver");
			String url = element.attributeValue("url");
			// 页面自定义
			if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password) && StringUtils.isNotBlank(driver) && StringUtils.isNotBlank(url)) {
				JdbcDatasourceDefinition ds = new JdbcDatasourceDefinition();
				ds.setCode(code);
				ds.setName(name);
				ds.setDriver(driver);
				ds.setUrl(url);
				ds.setUsername(username);
				ds.setPassword(password);
				ds.setDatasets(parseDatasets(element));
				return ds;
			} else { // 内置数据库连接
				BuildinDatasourceDefinition ds = new BuildinDatasourceDefinition();
				ds.setCode(code);
				ds.setName(element.attributeValue("name"));
				ds.setDatasets(parseDatasets(element));
				return ds;
			}
		} else if(type.equals("buildin")) {
			BuildinDatasourceDefinition ds = new BuildinDatasourceDefinition();
			ds.setCode(element.attributeValue("code"));
			ds.setName(element.attributeValue("name"));
			ds.setDatasets(parseDatasets(element));
			return ds;
		} else if(type.equals("api")) {
			ApiDatasourceDefinition ds = new ApiDatasourceDefinition();
			ds.setName(element.attributeValue("name"));
			ds.setUrl(element.attributeValue("url"));
			ds.setMethod(element.attributeValue("method"));
			
			List<Parameter> parameters = new ArrayList<Parameter>();
			List<Map<String,String>> headers = new ArrayList<Map<String,String>>();
			List<DatasetDefinition> list = new ArrayList<DatasetDefinition>();
			for (Object obj : element.elements()) {
				Element ele = (Element) obj;
				if (ele.getName().equals("parameter")) {
					Parameter param = new Parameter();
					param.setName(ele.attributeValue("name"));
					param.setDefaultValue(ele.attributeValue("default-value"));
					param.setType(DataType.valueOf(ele.attributeValue("type")));
					parameters.add(param);
				} else if(ele.getName().equals("dataset")) {
					ApiDatasetDefinition dataset = new ApiDatasetDefinition();
					dataset.setName(ele.attributeValue("name"));
					dataset.setFields(parseFields(ele));
					list.add(dataset);
				}  else if(ele.getName().equals("header")) {
					Map<String,String> header = new HashMap<String, String>();
					header.put("name", ele.attributeValue("name"));
					header.put("value", ele.attributeValue("value"));
					headers.add(header);
				}
			}
			ds.setParameters(parameters);
			ds.setHeaders(headers);
			ds.setDatasets(list);
			return ds;
		}
		return null;
	}

	private List<DatasetDefinition> parseDatasets(Element element) {
		List<DatasetDefinition> list = new ArrayList<DatasetDefinition>();
		for (Object obj : element.elements()) {
			if (obj == null || !(obj instanceof Element)) {
				continue;
			}
			Element ele = (Element) obj;
			String type = ele.attributeValue("type");
			if (type.equals("sql")) {
				SqlDatasetDefinition dataset = new SqlDatasetDefinition();
				dataset.setName(ele.attributeValue("name"));
				dataset.setSql(parseSql(ele, dataset));
				dataset.setFields(parseFields(ele));
				dataset.setParameters(parseParameters(ele));
				list.add(dataset);
			} else if (type.equals("bean")) {
				BeanDatasetDefinition dataset = new BeanDatasetDefinition();
				dataset.setName(ele.attributeValue("name"));
				dataset.setMethod(ele.attributeValue("method"));
				dataset.setFields(parseFields(ele));
				dataset.setClazz(ele.attributeValue("clazz"));
				list.add(dataset);
			}
		}
		return list;
	}

	private List<Parameter> parseParameters(Element element) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		for (Object obj : element.elements()) {
			if (obj == null || !(obj instanceof Element)) {
				continue;
			}
			Element ele = (Element) obj;
			if (!ele.getName().equals("parameter")) {
				continue;
			}
			Parameter param = new Parameter();
			param.setName(ele.attributeValue("name"));
			param.setDefaultValue(ele.attributeValue("default-value"));
			param.setType(DataType.valueOf(ele.attributeValue("type")));
			parameters.add(param);
		}
		return parameters;
	}

	private List<Field> parseFields(Element element) {
		List<Field> fields = new ArrayList<Field>();
		for (Object obj : element.elements()) {
			if (obj == null || !(obj instanceof Element)) {
				continue;
			}
			Element ele = (Element) obj;
			if (!ele.getName().equals("field")) {
				continue;
			}
			Field field = new Field(ele.attributeValue("name"));
			fields.add(field);
		}
		return fields;
	}

	private String parseSql(Element element, SqlDatasetDefinition dataset) {
		for (Object obj : element.elements()) {
			if (obj == null || !(obj instanceof Element)) {
				continue;
			}
			Element ele = (Element) obj;
			if (ele.getName().equals("sql")) {
				String sql = ele.getText().trim();
				if (sql.startsWith(ExpressionUtils.EXPR_PREFIX) && sql.endsWith(ExpressionUtils.EXPR_SUFFIX)) {
					String s = sql.substring(2, sql.length() - 1);
					Expression expr = ExpressionUtils.parseExpression(s);
					dataset.setSqlExpression(expr);
				}
				return ele.getText();
			}
		}
		return null;
	}
}
