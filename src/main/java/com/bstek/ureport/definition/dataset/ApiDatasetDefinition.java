package com.bstek.ureport.definition.dataset;

import java.util.List;

public class ApiDatasetDefinition implements DatasetDefinition {

	private static final long serialVersionUID = -7238059187866936269L;

	private String name;
	
	private List<Field> fields;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Field> getFields() {
		return fields;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
}
