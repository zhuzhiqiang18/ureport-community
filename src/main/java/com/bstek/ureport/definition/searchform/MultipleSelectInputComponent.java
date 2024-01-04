package com.bstek.ureport.definition.searchform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bstek.common.utils.StringUtils;
import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.export.html.InputComponentData;
import com.bstek.ureport.export.html.MultipleSelectInputComponentData;

public class MultipleSelectInputComponent extends SelectInputComponent {

	@Override
	@SuppressWarnings("unchecked")
	public InputComponentData buildComponent(RenderContext context) {
		MultipleSelectInputComponentData data = new MultipleSelectInputComponentData();
		super.setInputComponentData(data);
		
		String bindParameter = getBindParameter();
		List<Object> value = (List<Object>) context.getParameter(bindParameter);
		if(value == null) {
			value = Collections.emptyList();
		}
		data.setValue(value);
		
		String constant = super.getConstant();
		if (StringUtils.isNotBlank(constant) && "1".equals(constant)) {
			String datasetName = getDataset();
			Dataset dataset = context.getDataset(datasetName);
			List<Option> options = new ArrayList<Option>();
			for (Object obj : dataset.getData()) {
				Object optionLabel = Utils.getProperty(obj, super.getLabelField());
				Object optionValue = Utils.getProperty(obj, super.getValueField());
				Option option = new Option();
				option.setLabel(String.valueOf(optionLabel));
				option.setValue(String.valueOf(optionValue));
				options.add(option);
			}
			data.setOptions(options);
		} else {
			data.setOptions(getOptions());
		}
		return data;
	}
}
