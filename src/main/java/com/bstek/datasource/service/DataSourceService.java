package com.bstek.datasource.service;

import java.util.List;
import java.util.Map;

import com.bstek.datasource.bean.ApiParams;
import com.bstek.datasource.bean.DataSourceInfo;
import com.bstek.datasource.bean.PreviewParams;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.definition.dataset.Field;

public interface DataSourceService {

	/**
	 * 查询内置数据源
	 * @param info
	 * @return
	 */
	List<Map<String, String>> selectBuildinDruidList();
	
	/**
	 * 查询数据库表
	 * @param info
	 * @return
	 */
	List<Map<String, String>> selectTableList(DataSourceInfo info);
	
	/**
	 * 查询数据表对应字段
	 * @param previewParams
	 * @return
	 */
	List<Field> selectFieldList(PreviewParams previewParams);
	
	/**
	 * 预览结果仅显示100条数据
	 * @param previewParams
	 * @return
	 */
	Map<String, Object> previewData(PreviewParams previewParams);
	
	
	/**
	 * 预览结果仅显示100条数据
	 * @param previewParams
	 * @return
	 */
	List<Dataset> api(ApiParams info);
}
