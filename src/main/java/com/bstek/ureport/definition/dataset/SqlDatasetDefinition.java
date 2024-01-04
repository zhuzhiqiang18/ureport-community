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
package com.bstek.ureport.definition.dataset;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.definition.datasource.DataType;
import com.bstek.ureport.expression.ExpressionUtils;
import com.bstek.ureport.expression.model.Expression;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.utils.ProcedureUtils;

/**
 * @author Jacky.gao
 * @since 2016年12月27日
 */
public class SqlDatasetDefinition implements DatasetDefinition {

	private static final long serialVersionUID = -1134526105416805870L;

	private String name;

	private String sql;

	private List<Parameter> parameters;

	private List<Field> fields;

	private Expression sqlExpression;

	public Dataset buildDataset(Map<String, Object> params, Connection conn) {
		long start = System.currentTimeMillis();
		Map<String, Object> pmap = buildParameters(params);
		String sqlForUse = parseSql(pmap);
		if (ProcedureUtils.isProcedure(sqlForUse)) {
			List<Map<String, Object>> result = ProcedureUtils.procedureQuery(sqlForUse, pmap, conn);
			return new Dataset(name, result);
		}
		Utils.logToConsole("SQL参数:" + pmap);
		SingleConnectionDataSource datasource = new SingleConnectionDataSource(conn, false);
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(datasource);
		List<Map<String, Object>> list = jdbcTemplate.query(sqlForUse, pmap,new ResultSetExtractor<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
				while (rs.next()) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					int i = 1;
					for (Field field : fields) {
						map.put(field.getName(), rs.getObject(i));
						i++;
					}
					list.add(map);
				}
				return list;
			}
		});
		long end = System.currentTimeMillis();
		Utils.logToConsole("SQL查询耗时:" + (end - start) + "ms");
		return new Dataset(name, list);
	}


	public String parseSql(Map<String, Object> params) {
		String sqlForUse = sql.trim();
		Context context = new Context(params);
		if (sqlForUse.startsWith(ExpressionUtils.EXPR_PREFIX) && sqlForUse.endsWith(ExpressionUtils.EXPR_SUFFIX)) {
			sqlForUse = sqlForUse.substring(2, sqlForUse.length() - 1);
			Expression expr = ExpressionUtils.parseExpression(sqlForUse);
			sqlForUse = executeSqlExpr(expr, context);
			return sqlForUse;
		} else if (sqlExpression != null) {
			sqlForUse = executeSqlExpr(sqlExpression, context);
		} else {
			Pattern pattern = Pattern.compile("\\$\\{.*?\\}");
			Matcher matcher = pattern.matcher(sqlForUse);
			while (matcher.find()) {
				String substr = matcher.group();
				String sqlExpr = substr.substring(2, substr.length() - 1);
				Expression expr = ExpressionUtils.parseExpression(sqlExpr);
				String result = executeSqlExpr(expr, context);
				sqlForUse = sqlForUse.replace(substr, result);
			}
		}
		Utils.logToConsole("SQL:" + sqlForUse);
		return sqlForUse;
	}

	private String executeSqlExpr(Expression sqlExpr, Context context) {
		String sqlForUse = null;
		ExpressionData<?> exprData = sqlExpr.execute(null, null, context);
		if (exprData instanceof ObjectExpressionData) {
			ObjectExpressionData data = (ObjectExpressionData) exprData;
			Object obj = data.getData();
			if (obj != null) {
				String s = obj.toString();
				s = s.replaceAll("\\\\", "");
				sqlForUse = s;
			}
		}
		return sqlForUse;
	}

	public Map<String, Object> buildParameters(Map<String, Object> params) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Parameter param : parameters) {
			String name = param.getName();
			DataType datatype = param.getType();
			Object value = param.getDefaultValue();
			if (params != null && params.containsKey(name)) {
				value = params.get(name);
			}
			map.put(name, datatype.parse(value));
		}
		return map;
	}

	@Override
	public List<Field> getFields() {
		return fields;
	}

	public void setSqlExpression(Expression sqlExpression) {
		this.sqlExpression = sqlExpression;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}
	
	public boolean hasParameter(String key) {
		if(parameters != null &&  parameters.size() > 0) {
			for (Parameter p : parameters) {
				if(p.getName().equals(key)) {
					return true;
				}
			}
		}
		return false;
	}
}
