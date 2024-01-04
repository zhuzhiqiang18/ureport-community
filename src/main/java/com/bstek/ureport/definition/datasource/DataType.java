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
package com.bstek.ureport.definition.datasource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bstek.common.utils.StringUtils;
import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.expression.ExpressionUtils;
import com.bstek.ureport.expression.model.Expression;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.expression.model.data.ObjectListExpressionData;
import com.bstek.ureport.model.Cell;

/**
 * @author Jacky.gao
 * @since 2016年12月27日
 */
public enum DataType {

	Integer, Float, Boolean, String, Date, List;

	public Object parse(Object obj) {
		String str = StringUtils.toString(obj);
		switch (this) {
		case Boolean:
			if (StringUtils.isBlank(str)) {
				return null;
			}
			if (obj instanceof Boolean) {
				return (Boolean) obj;
			} 
			return java.lang.Boolean.valueOf(str);
			
		case Float:
			if (StringUtils.isBlank(str)) {
				return null;
			}
			if (obj instanceof Float) {
				return (Float) obj;
			}
			return Utils.toBigDecimal(obj).doubleValue();
		case Integer:
			if (StringUtils.isBlank(str)) {
				return null;
			}
			if (obj instanceof Integer) {
				return (Integer) obj;
			}
			return Utils.toBigDecimal(obj).intValue();
		case String:
			return str;
		case List:
			if (StringUtils.isBlank(str)) {
				return null;
			}
			if (obj instanceof List) {
				return (List<?>) obj;
			}
			String[] arrs = obj.toString().split(",");
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < arrs.length; i++) {
				list.add(arrs[i]);
			}
			return list;
		case Date:
			if (StringUtils.isBlank(str)) {
				return new Date();
			}
			if (obj instanceof Date) {
				return (Date) obj;
			}
			try {
				Map<String, Object> params = new HashMap<String, Object>();
				Context context = new Context(params);
				Date date = getExpressionData(str,null, null, context);
				if (date != null) {
					return date;
				} else {
					return new Date();
				}
			} catch (Exception e) {
				throw new ReportComputeException("Date parameter value error " + e.getMessage());
			}
		}
		throw new ReportComputeException("Unknow parameter type : " + this);
	}
	
	public Date getExpressionData(String text,Cell cell,Cell currentCell,Context context) {
		Expression expr = ExpressionUtils.parseExpression(text);
		ExpressionData<?> data = expr.execute(cell, currentCell, context);
		if(data instanceof ObjectListExpressionData) {
			ObjectListExpressionData listData = (ObjectListExpressionData)data;
			List<?> list = listData.getData();
			if(list == null || list.size() != 1) {
				return null;
			}
			Object obj = list.get(0);
			if(obj == null) {
				return null;
			}
			return Utils.toDate(obj);
		} 
		if(data instanceof ObjectExpressionData) {
			ObjectExpressionData objData = (ObjectExpressionData)data;
			Object obj = objData.getData();
			if(obj == null){
				return null;
			}
			return Utils.toDate(obj);
		}
		return null;
	}
}
