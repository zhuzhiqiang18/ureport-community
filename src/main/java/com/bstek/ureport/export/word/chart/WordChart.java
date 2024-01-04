package com.bstek.ureport.export.word.chart;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xwpf.usermodel.XWPFChart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface WordChart {
	
	default int refreshSheet(XSSFSheet sheet, ChartOption options){
		// 标题行
		int rowIndex = 0;
		Row row = sheet.createRow(rowIndex);
		String[] dimensions = options.getDimensions();
		for (int i = 1; i < dimensions.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(dimensions[i]);
		}
		rowIndex++;
		JSONArray array = options.getDataSet();
		for (Object object : array) {
			row = sheet.createRow(rowIndex);
			JSONObject obj = (JSONObject) object;
			for (int i = 0; i < dimensions.length; i++) {
				String key = dimensions[i];
				Cell cell = row.createCell(i);
				if (i == 0) {
					cell.setCellValue(obj.getString(key));
				} else {
					cell.setCellValue(obj.getDoubleValue(key));
				}
			}
			rowIndex++;
		}
		return rowIndex;
	}
	
	void bulider(XWPFChart chart, ChartOption options);

}
