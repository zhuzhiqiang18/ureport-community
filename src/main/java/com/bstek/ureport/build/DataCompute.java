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
package com.bstek.ureport.build;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bstek.ureport.build.compute.ChartValueCompute;
import com.bstek.ureport.build.compute.DatasetValueCompute;
import com.bstek.ureport.build.compute.ExpressionValueCompute;
import com.bstek.ureport.build.compute.ImageValueCompute;
import com.bstek.ureport.build.compute.SimpleValueCompute;
import com.bstek.ureport.build.compute.SlashValueCompute;
import com.bstek.ureport.build.compute.ValueCompute;
import com.bstek.ureport.build.compute.ZxingValueCompute;
import com.bstek.ureport.definition.value.Value;
import com.bstek.ureport.definition.value.ValueType;
import com.bstek.ureport.exception.ReportException;
import com.bstek.ureport.model.Cell;

/**
 * @author Jacky.gao
 * @since 2016年12月21日
 */
public class DataCompute {

	private static final Map<ValueType, ValueCompute> valueComputesMap = new HashMap<ValueType, ValueCompute>();

	static {
		// 文本值
		valueComputesMap.put(ValueType.simple, new SimpleValueCompute());
		// 数据集值
		valueComputesMap.put(ValueType.dataset, new DatasetValueCompute());
		// 表达式值
		valueComputesMap.put(ValueType.expression, new ExpressionValueCompute());
		// 图片
		valueComputesMap.put(ValueType.image, new ImageValueCompute());
		// 斜表头
		valueComputesMap.put(ValueType.slash, new SlashValueCompute());
		// 二维码
		valueComputesMap.put(ValueType.zxing, new ZxingValueCompute());
		// 图表
		valueComputesMap.put(ValueType.chart, new ChartValueCompute());
	}

	public static List<BindData> buildCellData(Cell cell, Context context) {
		context.resetVariableMap();
		Value value = cell.getValue();
		ValueCompute valueCompute = valueComputesMap.get(value.getType());
		if (valueCompute != null) {
			List<BindData> list = valueCompute.compute(cell, context);
			return list;
		}
		throw new ReportException("Unsupport value: " + value);
	}
}
