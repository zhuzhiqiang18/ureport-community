package com.bstek.ureport.export.excel.high.chart;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.RadarStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFRadarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xddf.usermodel.text.XDDFRunProperties;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRadarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRadarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.STMarkerStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;

import com.bstek.ureport.export.word.chart.ChartOption;

public class ExcelRadarChart implements ExcelChart {

	@Override
	public void bulider(XSSFSheet sheet,XSSFChart chart, ChartOption options) {
		String sheetName = sheet.getSheetName();
		int rowIndex = this.refreshSheet(sheet, options);
		String[] dimensions = options.getDimensions();
		
		// 字体颜色
		XDDFSolidFillProperties fontColor = new XDDFSolidFillProperties(XDDFColor.from(new byte[] {(byte)96,(byte)98,(byte)102}));

		// 图例
		if(options.isShowLegend()) {
			XDDFChartLegend legend = chart.getOrAddLegend();
			legend.setPosition(options.getLegendPosition());
		}
		// X轴
		XDDFCategoryAxis xAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
		// 标签样式
		XDDFRunProperties xTextProperties = xAxis.getOrAddTextProperties();
		xTextProperties.setFillProperties(fontColor);
				
		// Y轴
		XDDFValueAxis  yAxis = chart.createValueAxis(AxisPosition.LEFT);
		// 标签样式
		XDDFRunProperties yTextProperties = yAxis.getOrAddTextProperties();
		yTextProperties.setFillProperties(fontColor);
		// 网格线
		XDDFShapeProperties yGridProperties = yAxis.getOrAddMajorGridProperties();
		XDDFLineProperties yGridLine = new XDDFLineProperties();
		yGridLine.setFillProperties(new XDDFSolidFillProperties(XDDFColor.from(new byte[] {(byte)228,(byte)231,(byte)237})));
		yGridLine.setWidth(0.5);
		yGridProperties.setLineProperties(yGridLine);
		
		XDDFRadarChartData radar = (XDDFRadarChartData) chart.createData(ChartTypes.RADAR, xAxis, yAxis);
		radar.setStyle(RadarStyle.MARKER);
		
		XDDFCategoryDataSource xAxisSource = XDDFDataSourcesFactory.fromStringCellRange(sheet,new CellRangeAddress(1, rowIndex - 1, 0, 0));
		CTRadarChart  c = chart.getCTChart().getPlotArea().getRadarChartArray(0);
		for (int i = 1; i < dimensions.length; i++) {
			XDDFNumericalDataSource<Double> yAxisSource = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(1, rowIndex - 1, i, i));
			XDDFRadarChartData.Series series = (XDDFRadarChartData.Series) radar.addSeries(xAxisSource, yAxisSource);
			
			XDDFLineProperties lineProperties = new XDDFLineProperties();
			XDDFSolidFillProperties seriesProperties = new XDDFSolidFillProperties(XDDFColor.from(options.getColor(i-1)));
			lineProperties.setWidth(1.5);
			lineProperties.setFillProperties(seriesProperties);
			series.setLineProperties(lineProperties);
			
			// 设置标记颜色
			CTRadarSer ser = c.getSerArray(i-1);
			CTMarker marker = ser.addNewMarker();
			setMarkerColor(marker, options.getColor(i-1));
			
			String name = dimensions[i];
			series.setTitle(name, new CellReference(sheetName, 0, i, true, true));
		}
		chart.plot(radar);
	}
	
	/**
	 * 	设置标记点颜色
	 */
	private void setMarkerColor(CTMarker marker, byte[] color) {
		marker.addNewSymbol().setVal(STMarkerStyle.CIRCLE);
		CTShapeProperties shapeProperties = marker.addNewSpPr();
		// 边框颜色
		CTLineProperties borderProperties = shapeProperties.addNewLn();
		CTSolidColorFillProperties borderColor = borderProperties.addNewSolidFill();
		borderColor.addNewSrgbClr().setVal(color);
		// 填充颜色
		CTSolidColorFillProperties fillProperties = shapeProperties.addNewSolidFill();
		fillProperties.addNewSrgbClr().setVal(color);
	}
}
