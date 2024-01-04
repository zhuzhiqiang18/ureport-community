package com.bstek.ureport.chart.dataset;

import java.util.List;

import com.bstek.ureport.definition.mapping.MappingItem;
import com.bstek.ureport.definition.mapping.MappingType;

public class Dict {

	private MappingType mappingType = MappingType.simple;
	
	private String mappingDataset;
	
	private String mappingKeyProperty;
	
	private String mappingValueProperty;
	
	private List<MappingItem> mappingItems;

	public MappingType getMappingType() {
		return mappingType;
	}

	public void setMappingType(MappingType mappingType) {
		this.mappingType = mappingType;
	}

	public String getMappingDataset() {
		return mappingDataset;
	}

	public void setMappingDataset(String mappingDataset) {
		this.mappingDataset = mappingDataset;
	}

	public String getMappingKeyProperty() {
		return mappingKeyProperty;
	}

	public void setMappingKeyProperty(String mappingKeyProperty) {
		this.mappingKeyProperty = mappingKeyProperty;
	}

	public String getMappingValueProperty() {
		return mappingValueProperty;
	}

	public void setMappingValueProperty(String mappingValueProperty) {
		this.mappingValueProperty = mappingValueProperty;
	}

	public List<MappingItem> getMappingItems() {
		return mappingItems;
	}

	public void setMappingItems(List<MappingItem> mappingItems) {
		this.mappingItems = mappingItems;
	}
}
