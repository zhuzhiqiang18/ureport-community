package com.bstek.designer.excel;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.bstek.ureport.definition.ReportDefinition;

public class ExcelParserUtils {

	private static final ExcelParser xls = new HSSFExcelParser();
	
	private static final ExcelParser xlsx = new XSSFExcelParser();
	
	public static ReportDefinition parser(MultipartFile file) {
		try (InputStream inputStream = file.getInputStream()) {
			String name = file.getOriginalFilename();
			if (name.endsWith(".xls")) {
				return xls.parse(inputStream);
			}
			if (name.endsWith(".xlsx")) {
				return xlsx.parse(inputStream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
