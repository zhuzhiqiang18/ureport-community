package com.bstek.ureport.export.excel.high.chart;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFNoFillProperties;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.AxisTickMark;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryDataSource;
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

import com.bstek.ureport.export.word.chart.ChartOption;

public class ExcelLineChart implements ExcelChart {

	@Override
	public void bulider(XSSFSheet sheet,XSSFChart chart, ChartOption options) {
		int rowIndex = this.refreshSheet(sheet, options);
		String[] dimensions = options.getDimensions();
		
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
				
		// Y轴
		XDDFValueAxis  yAxis = chart.createValueAxis(AxisPosition.LEFT);
		if(!options.isShowYAxis()) {
			yAxis.setVisible(false);// Y轴不显示
		}
		yAxis.setMajorTickMark(AxisTickMark.NONE);// 轴刻度线
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
		
		XDDFLineChartData lineChart = (XDDFLineChartData) chart.createData(ChartTypes.LINE, xAxis, yAxis);
		XDDFCategoryDataSource xAxisSource = XDDFDataSourcesFactory.fromStringCellRange(sheet,new CellRangeAddress(1, rowIndex - 1, 0, 0));
		CTLineChart  c = chart.getCTChart().getPlotArea().getLineChartArray(0);
		for (int i = 1; i < dimensions.length; i++) {
			XDDFNumericalDataSource<Double> yAxisSource = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(1, rowIndex - 1, i, i));
			XDDFLineChartData.Series series = (XDDFLineChartData.Series) lineChart.addSeries(xAxisSource, yAxisSource);
			
			XDDFSolidFillProperties seriesProperties = new XDDFSolidFillProperties(XDDFColor.from(options.getColor(i-1)));
			XDDFLineProperties lineProperties = new XDDFLineProperties();
			lineProperties.setWidth(1.4);
			lineProperties.setFillProperties(seriesProperties);
			series.setLineProperties(lineProperties);
			
			series.setSmooth(false); // 是否圆滑曲线
			series.setMarkerSize((short) 5); // 标记大小
			series.setMarkerStyle(MarkerStyle.CIRCLE); // 标记形状
			// 设置标记颜色
			CTLineSer ser = c.getSerArray(i-1);
			CTMarker marker = ser.getMarker();
			setMarkerColor(marker, options.getColor(i-1));
		}
		chart.plot(lineChart);
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
