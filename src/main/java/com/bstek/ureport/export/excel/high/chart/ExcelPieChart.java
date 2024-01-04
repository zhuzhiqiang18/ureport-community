package com.bstek.ureport.export.excel.high.chart;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFPieChartData;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;

import com.bstek.ureport.export.word.chart.ChartOption;

public class ExcelPieChart implements ExcelChart {

	@Override
	public void bulider(XSSFSheet sheet,XSSFChart chart, ChartOption options) {
		int rowIndex = this.refreshSheet(sheet, options);
		// 图例
		if(options.isShowLegend()) {
			XDDFChartLegend legend = chart.getOrAddLegend();
			legend.setPosition(options.getLegendPosition());
		}

		XDDFPieChartData pie = (XDDFPieChartData) chart.createData(ChartTypes.PIE, null, null);
		XDDFCategoryDataSource categorySource = XDDFDataSourcesFactory.fromStringCellRange(sheet,new CellRangeAddress(1, rowIndex - 1, 0, 0));
		XDDFNumericalDataSource<Double> valueSource = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(1, rowIndex - 1, 1, 1));
		XDDFChartData.Series series = pie.addSeries(categorySource, valueSource);
		
		chart.plot(pie);
		// 自定义饼图颜色
		CTChart c = chart.getCTChart();
		if (c.getAutoTitleDeleted() == null) {
			c.addNewAutoTitleDeleted();
		} 
	    c.getAutoTitleDeleted().setVal(false);
	    int pointCount = series.getCategoryData().getPointCount();
        CTPieSer ser = c.getPlotArea().getPieChartArray(0).getSerArray(0);
        for (int p = 0; p < pointCount; p++) {
    	  ser.addNewDPt().addNewIdx().setVal(p);
    	  ser.getDPtArray(p).addNewSpPr().addNewSolidFill().addNewSrgbClr().setVal(options.getColor(p));
        }
	}
}
