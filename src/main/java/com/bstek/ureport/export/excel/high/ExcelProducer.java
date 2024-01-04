package com.bstek.ureport.export.excel.high;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bstek.ureport.Utils;
import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.export.Producer;
import com.bstek.ureport.export.excel.high.chart.ExcelAreaChart;
import com.bstek.ureport.export.excel.high.chart.ExcelAreaStackedChart;
import com.bstek.ureport.export.excel.high.chart.ExcelBarChart;
import com.bstek.ureport.export.excel.high.chart.ExcelBarStackedChart;
import com.bstek.ureport.export.excel.high.chart.ExcelBubbleChart;
import com.bstek.ureport.export.excel.high.chart.ExcelChart;
import com.bstek.ureport.export.excel.high.chart.ExcelComboChart;
import com.bstek.ureport.export.excel.high.chart.ExcelDoughnutChart;
import com.bstek.ureport.export.excel.high.chart.ExcelHistogramChart;
import com.bstek.ureport.export.excel.high.chart.ExcelHistogramStackedChart;
import com.bstek.ureport.export.excel.high.chart.ExcelLineChart;
import com.bstek.ureport.export.excel.high.chart.ExcelLineStackedChart;
import com.bstek.ureport.export.excel.high.chart.ExcelPieChart;
import com.bstek.ureport.export.excel.high.chart.ExcelScatterChart;
import com.bstek.ureport.export.word.chart.ChartOption;
import com.bstek.ureport.model.Column;
import com.bstek.ureport.model.Image;
import com.bstek.ureport.model.Report;
import com.bstek.ureport.model.Row;
import com.bstek.ureport.utils.ImageUtils;
import com.bstek.ureport.utils.UnitUtils;

public class ExcelProducer implements Producer {

	@Override
	public void produce(Report report, OutputStream outputStream) {
		CellStyleContext cellStyleContext = new CellStyleContext();
		List<Row> rows = report.getRows();
		XSSFWorkbook wb = new XSSFWorkbook();
		SXSSFWorkbook sWb = new SXSSFWorkbook(wb, 100000);
		try {
			CreationHelper creationHelper = wb.getCreationHelper();
			List<Column> columns = report.getColumns();
			// 最后一列为人工补的扩展列,需要去掉
			columns.remove(columns.size() - 1);
			Map<Row, Map<Column, com.bstek.ureport.model.Cell>> cellMap = report.getRowColCellMap();
			int columnSize = columns.size();
			SXSSFSheet sheet = sWb.createSheet("Sheet1");
			sheet.createDrawingPatriarch();
			XSSFDrawing drawing = sheet.getDrawingPatriarch();
			int rowNumber = 0;
			int chartIndex = 2;
			for (Row r : rows) {
				int realHeight = r.getRealHeight();
				if (realHeight < 1) {
					continue;
				}
				if (r.isForPaging()) {
					return;
				}
				org.apache.poi.ss.usermodel.Row row = sheet.getRow(rowNumber);
				if (row == null) {
					row = sheet.createRow(rowNumber);
				}
				Map<Column, com.bstek.ureport.model.Cell> colCell = cellMap.get(r);
				int skipCol = 0;
				for (int i = 0; i < columnSize; i++) {
					Column col = columns.get(i);
					int w = col.getWidth();
					if (w < 1) {
						skipCol++;
						continue;
					}
					double colWidth = UnitUtils.pointToPixel(w) * 37.5;
					int colNum = i - skipCol;
					sheet.setColumnWidth(colNum, (short) colWidth);
					org.apache.poi.ss.usermodel.Cell cell = row.getCell(colNum);
					if (cell != null) {
						continue;
					}
					cell = row.createCell(colNum);
					com.bstek.ureport.model.Cell cellInfo = null;
					if (colCell != null) {
						cellInfo = colCell.get(col);
					}
					if (cellInfo == null) {
						continue;
					}
					if (cellInfo.isForPaging()) {
						continue;
					}
					XSSFCellStyle style = cellStyleContext.produceXSSFCellStyle(sWb, cellInfo);
					int colSpan = cellInfo.getColSpan();
					int rowSpan = cellInfo.getRowSpan();
					int rowStart = rowNumber;
					int rowEnd = rowSpan;
					if (rowSpan == 0) {
						rowEnd++;
					}
					rowEnd += rowNumber;
					int colStart = i;
					int colEnd = colSpan;
					if (colSpan == 0) {
						colEnd++;
					}
					colEnd += i;
					for (int j = rowStart; j < rowEnd; j++) {
						org.apache.poi.ss.usermodel.Row rr = sheet.getRow(j);
						if (rr == null) {
							rr = sheet.createRow(j);
						}
						for (int c = colStart; c < colEnd; c++) {
							Cell cc = rr.getCell(c - skipCol);
							if (cc == null) {
								cc = rr.createCell(c - skipCol);
							}
							cc.setCellStyle(style);
						}
					}
					if (colSpan > 0 || rowSpan > 0) {
						if (rowSpan > 0) {
							rowSpan--;
						}
						if (colSpan > 0) {
							colSpan--;
						}
						CellRangeAddress cellRegion = new CellRangeAddress(rowNumber, (rowNumber + rowSpan),
								i - skipCol, (i - skipCol + colSpan));
						sheet.addMergedRegion(cellRegion);
					}
					Object obj = cellInfo.getFormatData();
					if (obj != null) {
						if (obj instanceof String) {
							cell.setCellValue((String) obj);
						} else if (obj instanceof Number) {
							BigDecimal bigDecimal = Utils.toBigDecimal(obj);
							cell.setCellValue(bigDecimal.doubleValue());
						} else if (obj instanceof Boolean) {
							cell.setCellValue((Boolean) obj);
						} else if (obj instanceof Image) {
							Image img = (Image) obj;
							InputStream inputStream = ImageUtils.base64DataToInputStream(img.getBase64Data());
							BufferedImage bufferedImage = ImageIO.read(inputStream);
							int width = bufferedImage.getWidth();
							int height = bufferedImage.getHeight();
							IOUtils.closeQuietly(inputStream);
							inputStream = ImageUtils.base64DataToInputStream(img.getBase64Data());

							int leftMargin = 0, topMargin = 0;
							int wholeWidth = getWholeWidth(columns, i, cellInfo.getColSpan());
							int wholeHeight = getWholeHeight(rows, rowNumber, cellInfo.getRowSpan());
							HorizontalAlignment align = style.getAlignment();
							if (align.equals(HorizontalAlignment.CENTER)) {
								leftMargin = (wholeWidth - width) / 2;
							} else if (align.equals(HorizontalAlignment.RIGHT)) {
								leftMargin = wholeWidth - width;
							}
							VerticalAlignment valign = style.getVerticalAlignment();
							if (valign.equals(VerticalAlignment.CENTER)) {
								topMargin = (wholeHeight - height) / 2;
							} else if (valign.equals(VerticalAlignment.BOTTOM)) {
								topMargin = wholeHeight - height;
							}

							try {
								XSSFClientAnchor anchor = (XSSFClientAnchor) creationHelper.createClientAnchor();
								byte[] bytes = IOUtils.toByteArray(inputStream);
								int pictureFormat = buildImageFormat(img);
								int pictureIndex = wb.addPicture(bytes, pictureFormat);
								anchor.setCol1(i);
								anchor.setCol2(i + colSpan);
								anchor.setRow1(rowNumber);
								anchor.setRow2(rowNumber + rowSpan);
								anchor.setDx1(leftMargin * Units.EMU_PER_PIXEL);
								anchor.setDx2(width * Units.EMU_PER_PIXEL);
								anchor.setDy1(topMargin * Units.EMU_PER_PIXEL);
								anchor.setDy2(height * Units.EMU_PER_PIXEL);
								drawing.createPicture(anchor, pictureIndex);
							} finally {
								IOUtils.closeQuietly(inputStream);
							}
						} else if (obj instanceof ChartData) {
							
							ClientAnchor anchor = drawing.createAnchor(2* Units.EMU_PER_PIXEL, 2* Units.EMU_PER_PIXEL, 0, 0, colStart, rowStart, colEnd, rowEnd);
							XSSFChart chart = drawing.createChart(anchor);
							chart.getCTChartSpace().addNewSpPr().addNewLn().addNewNoFill();
							ChartOption options = new ChartOption((ChartData) obj);
							String type = options.getType();
							ExcelChart excelChart = null;
							if ("bar".equals(type)) {
								excelChart = new ExcelBarChart();
							} else if("bar-stacked".equals(type)) {
								excelChart = new ExcelBarStackedChart();
							} else if ("histogram".equals(type)) {
								excelChart = new ExcelHistogramChart();
							} else if ("histogram-stacked".equals(type)) {
								excelChart = new ExcelHistogramStackedChart();
							} else if ("line".equals(type)) {
								excelChart = new ExcelLineChart();
							} else if ("line-stacked".equals(type)) {
								excelChart = new ExcelLineStackedChart();
							} else if ("area".equals(type)) {
								excelChart = new ExcelAreaChart();
							} else if ("area-stacked".equals(type)) {
								excelChart = new ExcelAreaStackedChart();
							} else if ("pie".equals(type)) {
								excelChart = new ExcelPieChart();
							} else if ("doughnut".equals(type)) {
								excelChart = new ExcelDoughnutChart();
							} else if ("radar".equals(type)) {
								excelChart = new ExcelDoughnutChart();
							} else if ("scatter".equals(type)) {
								excelChart = new ExcelScatterChart();
							} else if ("bubble".equals(type)) {
								excelChart = new ExcelBubbleChart();
							} else if ("combo".equals(type)) {
								excelChart = new ExcelComboChart();
							}
							if (excelChart != null) {
								String sheetName = "Sheet" + chartIndex;
								chartIndex++;
								XSSFSheet chartSheet = wb.createSheet(sheetName);
								excelChart.bulider(chartSheet, chart, options);
							}
						} else if (obj instanceof Date) {
							cell.setCellValue((Date) obj);
						}
					}
				}
				row.setHeight((short) UnitUtils.pointToTwip(r.getRealHeight()));
				rowNumber++;
			}
			sheet.setRowBreak(rowNumber - 1);
			sWb.write(outputStream);
		} catch (Exception ex) {
			throw new ReportComputeException(ex);
		} finally {
			sWb.dispose();
		}
	}

	private int getWholeWidth(List<Column> columns, int colNumber, int colSpan) {
		Column col = columns.get(colNumber);
		int start = colNumber + 1;
		int end = colNumber + colSpan;
		int w = col.getWidth();
		for (int i = start; i < end; i++) {
			Column c = columns.get(i);
			w += c.getWidth();
		}
		w = UnitUtils.pointToPixel(w);
		return w;
	}

	private int getWholeHeight(List<Row> rows, int rowNumber, int rowSpan) {
		Row row = rows.get(rowNumber);
		int start = rowNumber + 1, end = rowNumber + rowSpan;
		int h = row.getRealHeight();
		for (int i = start; i < end; i++) {
			Row r = rows.get(i);
			h += r.getRealHeight();
		}
		h = UnitUtils.pointToPixel(h);
		return h;
	}

	private int buildImageFormat(Image img) {
		int type = Workbook.PICTURE_TYPE_PNG;
		String path = img.getPath();
		if (path == null) {
			return type;
		}
		path = path.toLowerCase();
		if (path.endsWith("jpg") || path.endsWith("jpeg")) {
			type = Workbook.PICTURE_TYPE_JPEG;
		}
		return type;
	}
}
