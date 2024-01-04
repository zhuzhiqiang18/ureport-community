package com.bstek.ureport.export.excel.high.chart;

import java.util.List;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFNoFillProperties;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.AxisCrossBetween;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.AxisTickMark;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.BarGrouping;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData.Series;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xddf.usermodel.text.XDDFRunProperties;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bstek.ureport.export.word.chart.ChartOption;

public class ExcelComboChart implements ExcelChart {
	
	@Override
	public void bulider(XSSFSheet sheet,XSSFChart chart, ChartOption options) {
		String sheetName = sheet.getSheetName();
		int rowIndex = this.refreshSheet(sheet, options);
		// 字体颜色
		XDDFSolidFillProperties fontColor = new XDDFSolidFillProperties(XDDFColor.from(new byte[] {(byte)96,(byte)98,(byte)102}));
		
		// 样式线
		XDDFLineProperties line = new XDDFLineProperties();
		line.setFillProperties(new XDDFSolidFillProperties(XDDFColor.from(new byte[] {(byte)228,(byte)231,(byte)237})));
		line.setWidth(0.5);
		
		XDDFLineProperties noLine = new XDDFLineProperties();
		noLine.setFillProperties(new XDDFNoFillProperties());
		
		// 图例
		if(options.isShowLegend()) {
			XDDFChartLegend legend = chart.getOrAddLegend();
			legend.setPosition(options.getLegendPosition());
		}
				
		// X轴
		XDDFCategoryAxis xAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
		if(!options.isShowXAxis()) {
			xAxis.setVisible(false);// X轴不显示
		}
		xAxis.setMajorTickMark(AxisTickMark.NONE);// 轴刻度线
		// 标签样式
		XDDFRunProperties xTextProperties = xAxis.getOrAddTextProperties();
		xTextProperties.setFillProperties(fontColor);
		// 轴线样式
		XDDFShapeProperties xAxisLineProperties = xAxis.getOrAddShapeProperties();
		if(options.isShowXAxisLine()) {
			xAxisLineProperties.setLineProperties(line);
		} else {
			xAxisLineProperties.setLineProperties(noLine);
		}
		// 网格线
		if(options.isShowXGrid()) {
			XDDFShapeProperties xGridProperties = xAxis.getOrAddMajorGridProperties();
			xGridProperties.setLineProperties(line);
		}	
		boolean isCreateLeft = false;
		boolean isCreateRight = false;
		JSONArray arr = options.getSeries();
		for (int i = 0; i < arr.size(); i++) {
			JSONObject item = arr.getJSONObject(i);
			boolean yAxisIndex = item.getBooleanValue("yAxisIndex");
			if(yAxisIndex) {
				isCreateRight = true;
			} else {
				isCreateLeft = true;
				
			}
		}
		XDDFCategoryDataSource xAxisSource = XDDFDataSourcesFactory.fromStringCellRange(sheet,new CellRangeAddress(1, rowIndex - 1, 0, 0));
		if(isCreateLeft) {
			// 左侧
			handleLeft(chart, options, sheet, sheetName, xAxisSource,xAxis, rowIndex);
		}
		if(isCreateRight) {
			// 右侧
			handleRight(chart, options, sheet, sheetName, xAxisSource,xAxis, rowIndex);
		}
	}
	
	private void handleLeft(XSSFChart chart, ChartOption options, XSSFSheet sheet, String sheetName, XDDFCategoryDataSource xAxisSource,XDDFCategoryAxis xAxis, int rowIndex) {
		
		// 字体颜色
		XDDFSolidFillProperties fontColor = new XDDFSolidFillProperties(XDDFColor.from(new byte[] {(byte)96,(byte)98,(byte)102}));
		
		// 样式线
		XDDFLineProperties line = new XDDFLineProperties();
		line.setFillProperties(new XDDFSolidFillProperties(XDDFColor.from(new byte[] {(byte)228,(byte)231,(byte)237})));
		line.setWidth(0.5);
		
		XDDFLineProperties noLine = new XDDFLineProperties();
		noLine.setFillProperties(new XDDFNoFillProperties());
				
		// Y主要坐标轴
		XDDFValueAxis  yAxis = chart.createValueAxis(AxisPosition.LEFT);
		if(!options.isShowYAxis()) {
			yAxis.setVisible(false);// Y轴不显示
		}
		yAxis.setMajorTickMark(AxisTickMark.NONE);// 轴刻度线
		yAxis.setCrossBetween(AxisCrossBetween.BETWEEN);// 两端留白
		// 标签样式
		XDDFRunProperties yTextProperties = yAxis.getOrAddTextProperties();
		yTextProperties.setFillProperties(fontColor);
		// 轴线样式
		XDDFShapeProperties yAxisLineProperties = yAxis.getOrAddShapeProperties();
		if(options.isShowYAxisLine()) {
			yAxisLineProperties.setLineProperties(line);
		} else {
			yAxisLineProperties.setLineProperties(noLine);
		}
		// 网格线
		if(options.isShowYGrid()) {
			XDDFShapeProperties yGridProperties = yAxis.getOrAddMajorGridProperties();
			yGridProperties.setLineProperties(line);
		}
		XDDFBarChartData histogramChart = null;
		XDDFBarChartData histogramStackedChart = null;
		XDDFLineChartData lineChart = null;
		
		JSONArray arr = options.getSeries();
		String[] dimensions = options.getDimensions();
		for (int i = 1; i < dimensions.length; i++) {
			XDDFNumericalDataSource<Double> yAxisSource = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(1, rowIndex - 1, i, i));
			JSONObject item = arr.getJSONObject(i-1);
			boolean yAxisIndex = item.getBooleanValue("yAxisIndex");
			if(!yAxisIndex) {
				XDDFSolidFillProperties seriesProperties = new XDDFSolidFillProperties(XDDFColor.from(options.getColor(i-1)));
				String type = item.getString("type");
				if("bar-group".equals(type)) { // 分组柱状图
					if(histogramChart == null) {
						histogramChart = (XDDFBarChartData) chart.createData(ChartTypes.BAR, xAxis, yAxis);
						histogramChart.setBarDirection(BarDirection.COL); // COL柱状图  BAR条形图
						histogramChart.setOverlap((byte)-20);// 多个柱子间距
					}
					Series series = histogramChart.addSeries(xAxisSource, yAxisSource);
					series.setFillProperties(seriesProperties);
					String name = dimensions[i];
					series.setTitle(name, new CellReference(sheetName, 0, i, true, true));
				}
				if("bar-stack".equals(type)) { // 左侧叠加柱状图
					if(histogramStackedChart == null) {
						histogramStackedChart = (XDDFBarChartData) chart.createData(ChartTypes.BAR, xAxis, yAxis);
						histogramStackedChart.setBarDirection(BarDirection.COL); // COL柱状图  BAR条形图
						histogramStackedChart.setBarGrouping(BarGrouping.STACKED);// 叠加柱状图
						histogramStackedChart.setOverlap((byte)100);// 满值叠加柱子才不会错位
					}
					Series series = histogramStackedChart.addSeries(xAxisSource, yAxisSource);
					series.setFillProperties(seriesProperties);
					String name = dimensions[i];
					series.setTitle(name, new CellReference(sheetName, 0, i, true, true));
				}
				if("line".equals(type)) { // 左侧折线图
					if(lineChart == null) {
						lineChart = (XDDFLineChartData) chart.createData(ChartTypes.LINE, xAxis, yAxis);
					}
					XDDFLineChartData.Series series = (XDDFLineChartData.Series) lineChart.addSeries(xAxisSource, yAxisSource);
					XDDFLineProperties lineProperties = new XDDFLineProperties();
					lineProperties.setWidth(1.5);
					lineProperties.setFillProperties(seriesProperties);
					series.setLineProperties(lineProperties);
					
					series.setSmooth(false); // 是否圆滑曲线
					//series.setMarkerSize((short) 3); // 标记大小
					series.setMarkerStyle(MarkerStyle.CIRCLE); // 标记形状
					
					// 设置标记颜色
					CTLineChart  c = chart.getCTChart().getPlotArea().getLineChartArray(0);
					CTLineSer[] sers = c.getSerArray();
					CTMarker marker = sers[sers.length-1].getMarker();
					setMarkerColor(marker, options.getColor(i-1));
					
					String name = dimensions[i];
					series.setTitle(name, new CellReference(sheetName, 0, i, true, true));
				}
			}
		}
		if(histogramChart != null) {
			chart.plot(histogramChart);
		}
		if(histogramStackedChart != null) {
			chart.plot(histogramStackedChart);
		}
		if(lineChart != null) {
			chart.plot(lineChart);
		}
	}
	
	private void handleRight(XSSFChart chart, ChartOption options, XSSFSheet sheet, String sheetName, XDDFCategoryDataSource xAxisSource,XDDFCategoryAxis xAxis, int rowIndex) {
		
		// 字体颜色
		XDDFSolidFillProperties fontColor = new XDDFSolidFillProperties(XDDFColor.from(new byte[] {(byte)96,(byte)98,(byte)102}));
		
		// 样式线
		XDDFLineProperties line = new XDDFLineProperties();
		line.setFillProperties(new XDDFSolidFillProperties(XDDFColor.from(new byte[] {(byte)228,(byte)231,(byte)237})));
		line.setWidth(0.5);
		
		XDDFLineProperties noLine = new XDDFLineProperties();
		noLine.setFillProperties(new XDDFNoFillProperties());
				
		// Y轴
		XDDFValueAxis  yAxis = chart.createValueAxis(AxisPosition.RIGHT);
		yAxis.setCrosses(AxisCrosses.MAX);
		yAxis.crossAxis(xAxis);
		xAxis.crossAxis(yAxis);
		if(!options.isShowYSubAxis()) {
			yAxis.setVisible(false);// Y轴不显示
		}
		yAxis.setMajorTickMark(AxisTickMark.NONE);// 轴刻度线
		yAxis.setCrossBetween(AxisCrossBetween.BETWEEN);// 两端留白
		// 标签样式
		XDDFRunProperties yTextProperties = yAxis.getOrAddTextProperties();
		yTextProperties.setFillProperties(fontColor);
		// 轴线样式
		XDDFShapeProperties yAxisLineProperties = yAxis.getOrAddShapeProperties();
		if(options.isShowYSubAxisLine()) {
			yAxisLineProperties.setLineProperties(line);
		} else {
			yAxisLineProperties.setLineProperties(noLine);
		}
		// 网格线
		if(options.isShowYSubGrid()) {
			XDDFShapeProperties yGridProperties = yAxis.getOrAddMajorGridProperties();
			yGridProperties.setLineProperties(line);
		}
		XDDFBarChartData histogramChart = null;
		XDDFBarChartData histogramStackedChart = null;
		XDDFLineChartData lineChart = null;
		
		JSONArray arr = options.getSeries();
		String[] dimensions = options.getDimensions();
		for (int i = 1; i < dimensions.length; i++) {
			XDDFNumericalDataSource<Double> yAxisSource = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(1, rowIndex - 1, i, i));
			JSONObject item = arr.getJSONObject(i-1);
			boolean yAxisIndex = item.getBooleanValue("yAxisIndex");
			if(yAxisIndex) {
				XDDFSolidFillProperties seriesProperties = new XDDFSolidFillProperties(XDDFColor.from(options.getColor(i-1)));
				String type = item.getString("type");
				if("bar-group".equals(type)) { // 分组柱状图
					if(histogramChart == null) {
						histogramChart = (XDDFBarChartData) chart.createData(ChartTypes.BAR, xAxis, yAxis);
						histogramChart.setBarDirection(BarDirection.COL); // COL柱状图  BAR条形图
						histogramChart.setOverlap((byte)-20);// 多个柱子间距
					}
					Series series = histogramChart.addSeries(xAxisSource, yAxisSource);
					series.setFillProperties(seriesProperties);
					String name = dimensions[i];
					series.setTitle(name, new CellReference(sheetName, 0, i, true, true));
				}
				if("bar-stack".equals(type)) { // 左侧叠加柱状图
					if(histogramStackedChart == null) {
						histogramStackedChart = (XDDFBarChartData) chart.createData(ChartTypes.BAR, xAxis, yAxis);
						histogramStackedChart.setBarDirection(BarDirection.COL); // COL柱状图  BAR条形图
						histogramStackedChart.setBarGrouping(BarGrouping.STACKED);// 叠加柱状图
						histogramStackedChart.setOverlap((byte)100);// 满值叠加柱子才不会错位
					}
					Series series = histogramStackedChart.addSeries(xAxisSource, yAxisSource);
					series.setFillProperties(seriesProperties);
					String name = dimensions[i];
					series.setTitle(name, new CellReference(sheetName, 0, i, true, true));
				}
				if("line".equals(type)) { // 左侧折线图
					if(lineChart == null) {
						lineChart = (XDDFLineChartData) chart.createData(ChartTypes.LINE, xAxis, yAxis);
					}
					XDDFLineChartData.Series series = (XDDFLineChartData.Series) lineChart.addSeries(xAxisSource, yAxisSource);
					XDDFLineProperties lineProperties = new XDDFLineProperties();
					lineProperties.setWidth(1.5);
					lineProperties.setFillProperties(seriesProperties);
					series.setLineProperties(lineProperties);
					
					series.setSmooth(false); // 是否圆滑曲线
					//series.setMarkerSize((short) 5); // 标记大小
					series.setMarkerStyle(MarkerStyle.CIRCLE); // 标记形状
					
					// 设置标记颜色
					List<CTLineChart> list = chart.getCTChart().getPlotArea().getLineChartList();
					CTLineChart  c = list.get(list.size()-1);
					CTLineSer[] sers = c.getSerArray();
					CTMarker marker = sers[sers.length-1].getMarker();
					setMarkerColor(marker, options.getColor(i-1));
					
					String name = dimensions[i];
					series.setTitle(name, new CellReference(sheetName, 0, i, true, true));
				}
			}
		}
		if(histogramChart != null) {
			chart.plot(histogramChart);
		}
		if(histogramStackedChart != null) {
			chart.plot(histogramStackedChart);
		}
		if(lineChart != null) {
			chart.plot(lineChart);
		}
	}
	
	/**
	 * 	设置标记点颜色
	 */
	private void setMarkerColor(CTMarker marker, byte[] color) {
		CTShapeProperties shapeProperties = marker.addNewSpPr();
		// 边框颜色
		CTLineProperties borderProperties = shapeProperties.addNewLn();
		CTSolidColorFillProperties borderColor = borderProperties.addNewSolidFill();
		borderColor.addNewSrgbClr().setVal(color);
		// 填充颜色
		CTSolidColorFillProperties fillProperties = shapeProperties.addNewSolidFill();
		fillProperties.addNewSrgbClr().setVal(new byte[] { (byte) 255, (byte) 255, (byte) 255 });
	}
}
