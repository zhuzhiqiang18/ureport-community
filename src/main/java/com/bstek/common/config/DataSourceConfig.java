package com.bstek.common.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.bstek.datasource.bean.DataSourceInfo;

@Component
@ConfigurationProperties(prefix="ureport.support")
public class DataSourceConfig {

	private static final Map<String,DataSourceInfo>  datasources = new HashMap<String, DataSourceInfo>();
	
	private List<DataSourceInfo>  datasource;

	public List<DataSourceInfo> getDatasource() {
		return datasource;
	}

	public void setDatasource(List<DataSourceInfo> datasource) {
		this.datasource = datasource;
	}

	@PostConstruct
	private void init() {
		if(datasource != null && datasource.size() > 0) {
			for (DataSourceInfo ds : datasource) {
				datasources.put(ds.getCode(), ds);
			}
		}
	}
	
	public static DataSourceInfo getDataSourceInfo(String code) {
		return datasources.get(code);
	}
}