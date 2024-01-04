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
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFScatterChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xddf.usermodel.text.XDDFRunProperties;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterChart;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bstek.ureport.export.word.chart.ChartOption;

public class ExcelBubbleChart implements ExcelChart{

	@Override
	public void bulider(XSSFSheet sheet,XSSFChart chart, ChartOption options) {
		refreshSheet(sheet, options);
		// 字体颜色
		XDDFSolidFillProperties fontColor = new XDDFSolidFillProperties(XDDFColor.from(new byte[] {(byte)96,(byte)98,(byte)102}));
		
		// 样式线
		XDDFLineProperties line = new XDDFLineProperties();
		line.setFillProperties(new XDDFSolidFillProperties(XDDFColor.from(new byte[] {(byte)228,(byte)231,(byte)237})));
		line.setWidth(0.5);
		
		XDDFLineProperties noLine = new XDDFLineProperties();
		noLine.setFillProperties(new XDDFNoFillProperties());
		
		
		// X轴
		XDDFValueAxis  xAxis = chart.createValueAxis(AxisPosition.BOTTOM);
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
				
		XDDFScatterChartData scatter = (XDDFScatterChartData) chart.createData(ChartTypes.SCATTER, xAxis, yAxis);
		
		XDDFLineProperties chartLine = new XDDFLineProperties();
		chartLine.setFillProperties(new XDDFNoFillProperties());
		
		String[] dimensions = options.getDimensions();
		JSONArray array = options.getDataSet();
		double min = 0;
		double max = 0;
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			Double d = obj.getDouble(dimensions[2]);
			if(d != null && max < d) {
				max = d;
			}
			if(d != null && min > d) {
				min = d;
			}
		}
		CTScatterChart  c = chart.getCTChart().getPlotArea().getScatterChartArray(0);
		for (int i = 0; i < array.size(); i++) {
			XDDFNumericalDataSource<Double> x = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(i+1, i+1, 1, 1));
			XDDFNumericalDataSource<Double> y = XDDFDataSourcesFactory.fromNumericCellRange(sheet,new CellRangeAddress(i+1, i+1, 2, 2));
			XDDFScatterChartData.Series series = (XDDFScatterChartData.Series) scatter.addSeries(x, y);
			
			JSONObject obj = array.getJSONObject(i);
			Double d = obj.getDouble(dimensions[2]);
			if(d != null) {
				if(min == max){
					series.setMarkerSize((short)5);
			    } else {
			    	Double size = (d - min) * (40 - 5) / (max - min) + 5;
			    	series.setMarkerSize((short)size.intValue());
			    }
			} else {
				series.setMarkerSize((short)5);
			}
			series.setLineProperties(chartLine);
			series.setMarkerStyle(MarkerStyle.CIRCLE);
			// 设置标记颜色
			CTMarker marker = c.getSerArray(i).getMarker();
			setMarkerColor(marker, options.getColor(0));
		}
		chart.plot(scatter);
	}
	
	/**
	 * 	设置标记点颜色
	 */
	private void setMarkerColor(CTMarker marker, byte[] color) {
		CTShapeProperties shapeProperties = marker.addNewSpPr();
		// 边框颜色
		CTLineProperties borderProperties = shapeProperties.addNewLn();
		CTSolidColorFillProperties borderColor = borderProperties.addNewSolidFill();
		CTSRgbColor borderRgb = borderColor.addNewSrgbClr();
		borderRgb.addNewAlpha().setVal(80000);  // 透明度 0- 100000
		borderRgb.setVal(color);
		// 填充颜色
		CTSolidColorFillProperties fillProperties = shapeProperties.addNewSolidFill();
		CTSRgbColor fillRgb = fillProperties.addNewSrgbClr();
		fillRgb.addNewAlpha().setVal(80000);  // 透明度 0- 100000
		fillRgb.setVal(color);
	}
}
