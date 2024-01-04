package com.bstek.ureport.export.word.chart;

import org.apache.poi.xddf.usermodel.chart.LegendPosition;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bstek.common.utils.StringUtils;
import com.bstek.ureport.chart.ChartData;

public class ChartOption {

	private ChartData chartData;
	
	private String type;
	
	private JSONObject data;
	
	private JSONObject global;
	
	private JSONArray colors;
	
	private JSONObject xAxis;
	
	private JSONObject yAxis;
	
	private JSONObject ySubAxis;
	
	private JSONObject legend;
	
	public ChartOption(ChartData chartData) {
		this.chartData = chartData;
		init();
	}
	
	private void init() {
		String json = chartData.getJson();
		JSONObject config = JSON.parseObject(json);
		this.type = config.getString("type");
		JSONObject options = config.getJSONObject("options");
		this.global = options.getJSONObject("global");
		if("stacked".equals(this.global.getString("type"))) {
			if("bar".equals(this.type)) {
				this.type = "bar-stacked";
			} else if("histogram".equals(this.type)){
				this.type = "histogram-stacked";
			} if("line".equals(this.type)) {
				this.type = "line-stacked";
			} else if("area".equals(this.type)) {
				this.type = "area-stacked";
			}
		}
		
		this.xAxis = options.getJSONObject("xAxis");
		this.yAxis = options.getJSONObject("yAxis");
		this.ySubAxis = options.getJSONObject("yAxis1");
		
		this.legend = options.getJSONObject("legend");
		colors = global.getJSONArray("color");
		this.data = config;
	}
	
	public String[] getDimensions() {
		JSONArray arr = data.getJSONArray("dimensions");
		String[] labels = new String[arr.size()];
		return arr.toArray(labels);
	}
	
	public byte[] getColor(int i) {
		if(i >= colors.size()) {
			i = 0;
		}
		String color = colors.getString(i).replace("rgb(", "").replace(")", "");
		String[] rgb = color.split(",");
		int red = Integer.parseInt(rgb[0]);
		int green = Integer.parseInt(rgb[1]);
		int blue = Integer.parseInt(rgb[2]);
		byte[] cbyte = new byte[] { (byte) red, (byte) green, (byte) blue };
		return cbyte;
	}
	
	public int getDiaphaneity() {
		double opacity = global.getJSONObject("areaStyle").getDoubleValue("opacity");
		return Double.valueOf(100000 * opacity).intValue();
	}
	
	public JSONArray getDataSet() {
		return data.getJSONArray("dataset");
	}
	
	public String getType() {
		return type;
	}

	public String[] getLabels() {
		JSONArray arr = data.getJSONArray("labels");
		if (arr != null && arr.size() > 0) {
			String[] labels = new String[arr.size()];
			return arr.toArray(labels);
		}
		return new String[0];
	} 
	
	public JSONArray getSeries() {
		JSONArray arr = data.getJSONArray("series");
		return arr;
	}
	
	public boolean isShowLegend() {
		return legend.getBooleanValue("show");
	}
	
	public LegendPosition getLegendPosition() {
		String position = legend.getString("position");
		if(StringUtils.isBlank(position)) {
			return LegendPosition.TOP;
		}
		if("top-right".equals(position)) {
		   return LegendPosition.TOP_RIGHT;
		}
		if(position.startsWith("top")) {
			return LegendPosition.TOP;
		}
		if("middle-left".equals(position)) {
			return LegendPosition.LEFT;
		}
		if("middle-right".equals(position)) {
			return LegendPosition.RIGHT;
		}
		if(position.startsWith("bottom")) {
			return LegendPosition.BOTTOM;
		}
		return LegendPosition.TOP;
	}
	
	/**
	 * 是否显示X轴
	 * @return
	 */
	public boolean isShowXAxis() {
		return xAxis.getJSONObject("axisLabel").getBooleanValue("show");
	}
	
	/**
	 * 是否显示Y轴主要坐标轴
	 * @return
	 */
	public boolean isShowYAxis() {
		return yAxis.getJSONObject("axisLabel").getBooleanValue("show");
	}
	
	/**
	 * 是否显示Y轴次要坐标轴
	 * @return
	 */
	public boolean isShowYSubAxis() {
		return ySubAxis.getJSONObject("axisLabel").getBooleanValue("show");
	}
	
	/**
	 * 是否显示X轴网格线
	 * @return
	 */
	public boolean isShowXGrid() {
		return xAxis.getJSONObject("splitLine").getBooleanValue("show");
	}
	
	/**
	 * 是否显示Y轴主要坐标轴网格线
	 * @return
	 */
	public boolean isShowYGrid() {
		return yAxis.getJSONObject("splitLine").getBooleanValue("show");
	}
	
	/**
	 * 是否显示Y轴次要坐标轴网格线
	 * @return
	 */
	public boolean isShowYSubGrid() {
		return ySubAxis.getJSONObject("splitLine").getBooleanValue("show");
	}
	
	/**
	 * 是否显示X轴轴线
	 * @return
	 */
	public boolean isShowXAxisLine() {
		return xAxis.getJSONObject("axisLine").getBooleanValue("show");
	}
	
	/**
	 * 是否显示Y轴主要坐标轴轴线
	 * @return
	 */
	public boolean isShowYAxisLine() {
		return yAxis.getJSONObject("axisLine").getBooleanValue("show");
	}
	
	/**
	 * 是否显示Y轴次要坐标轴轴线
	 * @return
	 */
	public boolean isShowYSubAxisLine() {
		return ySubAxis.getJSONObject("axisLine").getBooleanValue("show");
	}
	
}
