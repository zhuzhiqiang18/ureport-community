package com.bstek.datasource.action;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bstek.common.utils.MultipleJdbcTemplate;
import com.bstek.datasource.bean.ApiParams;
import com.bstek.datasource.bean.DataSourceInfo;
import com.bstek.datasource.bean.PreviewParams;
import com.bstek.datasource.service.DataSourceService;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.definition.dataset.Field;

/**
 * 数据源配置
 * 
 * @author tomcat
 *
 */
@RestController
@RequestMapping("/datasource")
public class DataSourceInfoAction {
	
	@Resource
	private DataSourceService dataSourceService;
	
	@RequestMapping("/selectBuildinDruidList")
	public List<Map<String, String>> selectBuildinDruidList() {
		return dataSourceService.selectBuildinDruidList();
	}
	
	@RequestMapping("/testConnection")
	public String testConnection(@RequestBody DataSourceInfo info) {
		return MultipleJdbcTemplate.testConnection(info);
	}

	@RequestMapping("/tableList")
	public List<Map<String, String>> selectTableList(@RequestBody DataSourceInfo info) {
		return dataSourceService.selectTableList(info);
	}

	@RequestMapping("/tableFieldList")
	public List<Field> selectFieldList(@RequestBody PreviewParams previewParams) {
		return dataSourceService.selectFieldList(previewParams);
	}

	@RequestMapping("/preview")
	public Map<String, Object> previewData(@RequestBody PreviewParams previewParams) {
		return dataSourceService.previewData(previewParams);
	}
	
	@RequestMapping("/api")
	public List<Dataset> api(@RequestBody ApiParams info) {
		return dataSourceService.api(info);
	}
}
