/*******************************************************************************
 * Copyright 2017 Bstek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.export.word.high;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFChart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

import com.bstek.ureport.build.paging.Page;
import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.definition.Alignment;
import com.bstek.ureport.definition.Border;
import com.bstek.ureport.definition.BorderStyle;
import com.bstek.ureport.definition.CellStyle;
import com.bstek.ureport.definition.Orientation;
import com.bstek.ureport.definition.Paper;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.export.Producer;
import com.bstek.ureport.export.word.DxaUtils;
import com.bstek.ureport.export.word.chart.ChartOption;
import com.bstek.ureport.export.word.chart.WordAreaChart;
import com.bstek.ureport.export.word.chart.WordAreaStackedChart;
import com.bstek.ureport.export.word.chart.WordBarChart;
import com.bstek.ureport.export.word.chart.WordBarStackedChart;
import com.bstek.ureport.export.word.chart.WordBubbleChart;
import com.bstek.ureport.export.word.chart.WordChart;
import com.bstek.ureport.export.word.chart.WordComboChart;
import com.bstek.ureport.export.word.chart.WordDoughnutChart;
import com.bstek.ureport.export.word.chart.WordHistogramChart;
import com.bstek.ureport.export.word.chart.WordHistogramStackedChart;
import com.bstek.ureport.export.word.chart.WordLineChart;
import com.bstek.ureport.export.word.chart.WordLineStackedChart;
import com.bstek.ureport.export.word.chart.WordPieChart;
import com.bstek.ureport.export.word.chart.WordRadarChart;
import com.bstek.ureport.export.word.chart.WordScatterChart;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.model.Column;
import com.bstek.ureport.model.Image;
import com.bstek.ureport.model.Report;
import com.bstek.ureport.model.Row;
import com.bstek.ureport.provider.ProviderFactory;
import com.bstek.ureport.provider.image.ImageProvider;
import com.bstek.ureport.utils.ImageUtils;
import com.bstek.ureport.utils.UnitUtils;

/**
 * @author Jacky.gao
 * @since 2015年5月20日
 */
public class WordProducer implements Producer {
	
	private HeaderFooterBuilder headerFooterBuilder = new HeaderFooterBuilder();
	
	@Override
	public void produce(Report report, OutputStream outputStream) {
		XWPFDocument document = new XWPFDocument();
		try {
			CTSectPr sectpr = document.getDocument().getBody().addNewSectPr();
			if (!sectpr.isSetPgSz()) {
				sectpr.addNewPgSz();
			}
			CTPageSz pageSize = sectpr.getPgSz();
			Paper paper = report.getPaper();
			Orientation orientation = paper.getOrientation();
			if (orientation.equals(Orientation.landscape)) {
				pageSize.setOrient(STPageOrientation.LANDSCAPE);
				pageSize.setH(BigInteger.valueOf(DxaUtils.points2dxa(paper.getWidth())));
				pageSize.setW(BigInteger.valueOf(DxaUtils.points2dxa(paper.getHeight())));
			} else {
				pageSize.setOrient(STPageOrientation.PORTRAIT);
				pageSize.setW(BigInteger.valueOf(DxaUtils.points2dxa(paper.getWidth())));
				pageSize.setH(BigInteger.valueOf(DxaUtils.points2dxa(paper.getHeight())));
			}
			
			String bgImage = paper.getBgImage();
			
//			int columnCount = paper.getColumnCount();
//			if (paper.isColumnEnabled() && columnCount > 0) {
//				CTColumns cols = CTColumns.Factory.newInstance();
//				cols.setNum(new BigInteger(String.valueOf(columnCount)));
//				int columnMargin = paper.getColumnMargin();
//				cols.setSpace(new BigInteger(String.valueOf(DxaUtils.points2dxa(columnMargin))));
//				sectpr.setCols(cols);
//			}
			CTPageMar pageMar = sectpr.addNewPgMar();
			pageMar.setLeft(BigInteger.valueOf(DxaUtils.points2dxa(paper.getLeftMargin())));
			pageMar.setRight(BigInteger.valueOf(DxaUtils.points2dxa(paper.getRightMargin())));
			pageMar.setTop(BigInteger.valueOf(DxaUtils.points2dxa(paper.getTopMargin())));
			pageMar.setBottom(BigInteger.valueOf(DxaUtils.points2dxa(paper.getBottomMargin())));
			List<Column> columns = report.getColumns();
			// 最后一列为人工补的扩展列,需要去掉
			columns.remove(columns.size() - 1);
			int intArr[] = buildColumnSizeAndTotalWidth(columns);
			int totalColumn = intArr[0];
			int tableWidth = intArr[1];
			List<Page> pages = report.getPages();
			Map<Row, Map<Column, Cell>> cellMap = report.getRowColCellMap();
			int totalPages = pages.size();
			int pageIndex = 1;
			int m = 0;
			int[] colWidths = new int[columns.size()]; 
			for (Column col : columns) {
				colWidths[m] = col.getWidth();
				m++;
			}
			for (Page page : pages) {
				List<Row> rows = page.getRows();
				int n = 0;
				int[] rowHeights = new int[rows.size()]; 
				for (Row row : rows) {
					rowHeights[n] = row.getHeight();
					n++;
				}
				int totalRow = rows.size();
				XWPFTable table = document.createTable(totalRow, totalColumn);
				// 兼容WPS
				CTTblGrid grid = table.getCTTbl().addNewTblGrid();
				for (Column col : columns) {
					grid.addNewGridCol().setW(BigInteger.valueOf(DxaUtils.points2dxa(col.getWidth())));
				}
				CTTbl ctt = table.getCTTbl();
				ctt.getTblPr().unsetTblBorders();
				ctt.getTblPr().addNewTblLayout().setType(STTblLayoutType.FIXED);
				ctt.addNewTblPr().addNewTblW().setW(BigInteger.valueOf(DxaUtils.points2dxa(tableWidth)));
				for (int rowNumber = 0; rowNumber < rows.size(); rowNumber++) {
					Row row = rows.get(rowNumber);
					int height = row.getRealHeight();
					XWPFTableRow tableRow = table.getRow(rowNumber);
					tableRow.setHeight(DxaUtils.points2dxa(height));
					Map<Column, Cell> colCell = cellMap.get(row);
					if (colCell == null) {
						continue;
					}
					int skipCol = 0;
					for (Column col : columns) {
						int width = col.getWidth();
						if (width < 1) {
							skipCol++;
							continue;
						}
						int colNumber = col.getColumnNumber() - 1 - skipCol;
						Cell cell = colCell.get(col);
						if (cell == null) {
							continue;
						}
						XWPFTableCell tableCell = tableRow.getCell(colNumber);
						if (tableCell == null) {
							continue;
						}
						if (cell.getColSpan() > 0) {
							for (int i = colNumber + 1; i < colNumber + cell.getColSpan(); i++) {
								width += colWidths[i];
							}
						}
						tableCell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(DxaUtils.points2dxa(width)));
						buildTableCellStyle(table, tableCell, cell, rowNumber, colNumber, colWidths, rowHeights);
					    // 加载套打背景图
						if(rowNumber == 0 && colNumber == 0 && StringUtils.isNotBlank(bgImage) && paper.isPrint()) {
							XWPFRun run = getXWPFRun(tableCell);
							if(run != null) {
				    			addBackgroundPicture(run, bgImage);
				    		}
					    }
					}
				}
				if (pageIndex < totalPages) {
					XWPFParagraph paragraph = document.createParagraph();
					XWPFRun run = paragraph.createRun();
					run.setFontSize(0);
					run.addBreak(BreakType.PAGE);
				}
				pageIndex++;
			}
			// 加载页眉页脚
			headerFooterBuilder.build(document, sectpr, report);
			document.write(outputStream);
		} catch (Exception ex) {
			throw new ReportComputeException(ex);
		} finally {
			try {
				document.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private XWPFRun getXWPFRun(XWPFTableCell cell) {
		List<XWPFParagraph> list = cell.getParagraphs();
    	if(list != null && list.size() > 0) {
    		XWPFParagraph paragraph = list.get(0);
    		List<XWPFRun> runs = paragraph.getRuns();
    		if(runs != null && runs.size() > 0) {
    			return runs.get(0);
    		}
    	}
    	return null;
	}

	private int[] buildColumnSizeAndTotalWidth(List<Column> columns) {
		int count = 0, totalWidth = 0;
		for (int i = 0; i < columns.size(); i++) {
			Column col = columns.get(i);
			int width = col.getWidth();
			if (width < 1) {
				continue;
			}
			count++;
			totalWidth += width;
		}
		return new int[] { count, totalWidth };
	}

	private void buildTableCellStyle(XWPFTable table, XWPFTableCell tableCell, Cell cell, int rowNumber,int columnNumber,
			int[] colWidths, int[] rowHeights) {
		CellStyle style = cell.getCellStyle();
		CellStyle customStyle = cell.getCustomCellStyle();
		CellStyle rowStyle = cell.getRow().getCustomCellStyle();
		CellStyle colStyle = cell.getColumn().getCustomCellStyle();
		CTTcPr cellProperties = tableCell.getCTTc().addNewTcPr();
		Border leftBorder = style.getLeftBorder();
		Border rightBorder = style.getRightBorder();
		Border topBorder = style.getTopBorder();
		Border bottomBorder = style.getBottomBorder();
		if (customStyle != null) {
			if (customStyle.getLeftBorder() != null) {
				leftBorder = customStyle.getLeftBorder();
			}
			if (customStyle.getRightBorder() != null) {
				rightBorder = customStyle.getRightBorder();
			}
			if (customStyle.getTopBorder() != null) {
				topBorder = customStyle.getTopBorder();
			}
			if (customStyle.getBottomBorder() != null) {
				bottomBorder = customStyle.getBottomBorder();
			}
		}
		int rowSpan = cell.getPageRowSpan();
		int colSpan = cell.getColSpan();
		if (leftBorder != null) {
			if (rowSpan > 0) {
				int start = rowNumber;
				int end = start + rowSpan;
				for (int i = start; i < end; i++) {
					XWPFTableCell c = table.getRow(i).getCell(columnNumber);
					buildCellBorder(leftBorder, c, 1);
				}
			} else {
				buildCellBorder(leftBorder, tableCell, 1);
			}
		}
		if (rightBorder != null) {
			int lastCol = columnNumber;
			if (colSpan > 0) {
				lastCol += colSpan - 1;
			}
			if (rowSpan > 0) {
				int start = rowNumber;
				int end = start + rowSpan;
				for (int i = start; i < end; i++) {
					//微软
					XWPFTableCell c = table.getRow(i).getCell(lastCol);
					buildCellBorder(rightBorder, c, 2);
					if(lastCol != columnNumber) {
						//WPS
						c = table.getRow(i).getCell(columnNumber);
						buildCellBorder(rightBorder, c, 2);
					}
				}
			} else {
				//微软
				XWPFTableCell c = table.getRow(rowNumber).getCell(lastCol);
				buildCellBorder(rightBorder, c, 2);
				if(lastCol != columnNumber) {
					//WPS
					c = table.getRow(rowNumber).getCell(columnNumber);
					buildCellBorder(rightBorder, c, 2);
				}
			}
		}
		if (topBorder != null) {
			if (colSpan > 0) {
				int start = columnNumber;
				int end = start + colSpan;
				for (int i = start; i < end; i++) {
					XWPFTableCell c = table.getRow(rowNumber).getCell(i);
					buildCellBorder(topBorder, c, 3);
				}
			} else {
				buildCellBorder(topBorder, tableCell, 3);
			}
		}
		if (bottomBorder != null) {
			int lastRow = rowNumber;
			if (rowSpan > 0) {
				lastRow += rowSpan - 1;
			}
			if (colSpan > 0) {
				int start = columnNumber;
				int end = start + colSpan;
				for (int i = start; i < end; i++) {
					XWPFTableCell c = table.getRow(lastRow).getCell(i);
					buildCellBorder(bottomBorder, c, 4);
				}
			} else {
				XWPFTableCell c = table.getRow(lastRow).getCell(columnNumber);
				buildCellBorder(bottomBorder, c, 4);
			}
		}
		List<XWPFParagraph> paras = tableCell.getParagraphs();
		XWPFParagraph para = null;
		if (paras != null && paras.size() > 0) {
			para = paras.get(0);
		} else {
			para = tableCell.addParagraph();
		}
		List<XWPFRun> runs = para.getRuns();
		XWPFRun run = null;
		if (runs != null && runs.size() > 0) {
			run = runs.get(0);
		} else {
			run = para.createRun();
		}
		Object value = cell.getFormatData();
		if (value instanceof String) {
			run.setText(String.valueOf(value));
		} else if (value instanceof Number) {
			run.setText(String.valueOf(value));
		} else if (value instanceof Boolean) {
			run.setText(String.valueOf(value));
		} else if (value instanceof ChartData) {
			try {
				int width = 0;
				for (int m = columnNumber; m < colWidths.length && m < columnNumber + colSpan; m++) {
					width = width + colWidths[m];
				}
				int height = 0;
				for (int m = rowNumber; m < rowHeights.length && m < rowNumber + rowSpan; m++) {
					height = height + rowHeights[m];
				}
				width = Units.toEMU(width);
				height = Units.toEMU(height);

				XWPFDocument document = run.getDocument();
				XWPFChart chart = document.createChart(run, width, height);
				chart.getCTChartSpace().addNewSpPr().addNewLn().addNewNoFill();
				ChartOption options = new ChartOption((ChartData) value);
				String type = options.getType();
				WordChart wordChart = null;
				if ("bar".equals(type)) {
					wordChart = new WordBarChart();
				} else if("bar-stacked".equals(type)) {
					wordChart = new WordBarStackedChart();
				} else if("histogram".equals(type)) {
					wordChart = new WordHistogramChart();
				} else if("histogram-stacked".equals(type)) {
					wordChart = new WordHistogramStackedChart();
				} else if("line".equals(type)) {
					wordChart = new WordLineChart();
				} else if("line-stacked".equals(type)) { 
					wordChart = new WordLineStackedChart();
				} else if("area".equals(type)) {
					wordChart = new WordAreaChart();
				} else if("area-stacked".equals(type)) { 
					wordChart = new WordAreaStackedChart();
				} else if("pie".equals(type)) {
					wordChart = new WordPieChart();
				} else if("doughnut".equals(type)) {
					wordChart = new WordDoughnutChart();
				} else if("radar".equals(type)) {
					wordChart = new WordRadarChart();
				} else if("scatter".equals(type)) {
					wordChart = new WordScatterChart();
				} else if("bubble".equals(type)) {
					wordChart = new WordBubbleChart();
				} else if("combo".equals(type)) {
					wordChart = new WordComboChart();
				}
				if (wordChart != null) {
					wordChart.bulider(chart, options);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (value instanceof Image) {
			Image img = (Image) value;
			String path = img.getPath();
			String imageType = "png";
			if (StringUtils.isNotBlank(path)) {
				path = path.toLowerCase();
				if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
					imageType = "jpeg";
				} else if (path.endsWith(".gif")) {
					imageType = "gif";
				}
			}
			String base64Data = img.getBase64Data();
			if (StringUtils.isNotBlank(base64Data)) {
				InputStream inputStream = null;
				try {
					inputStream = ImageUtils.base64DataToInputStream(base64Data);
					BufferedImage bufferedImage = ImageIO.read(inputStream);
					int width = bufferedImage.getWidth();
					int height = bufferedImage.getHeight();
					IOUtils.closeQuietly(inputStream);
					inputStream = ImageUtils.base64DataToInputStream(base64Data);
					width = UnitUtils.pixelToPoint(width);
					height = UnitUtils.pixelToPoint(height);
					if (imageType.equals("jpeg")) {
						run.addPicture(inputStream, XWPFDocument.PICTURE_TYPE_JPEG,
								"ureport-" + rowNumber + "-" + columnNumber + ".jpg", Units.toEMU(width),
								Units.toEMU(height));
					} else if (imageType.equals("png")) {
						run.addPicture(inputStream, XWPFDocument.PICTURE_TYPE_PNG,
								"ureport-" + rowNumber + "-" + columnNumber + ".png", Units.toEMU(width),
								Units.toEMU(height));
					} else if (imageType.equals("gif")) {
						run.addPicture(inputStream, XWPFDocument.PICTURE_TYPE_GIF,
								"ureport-" + rowNumber + "-" + columnNumber + ".gif", Units.toEMU(width),
								Units.toEMU(height));
					}
				} catch (Exception ex) {
					throw new ReportComputeException(ex);
				} finally {
					IOUtils.closeQuietly(inputStream);
				}
			}
		} else if (value instanceof Date) {
			Date date = (Date) value;
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			run.setText(sd.format(date));
		}
		String fontFamily = style.getFontFamily();
		if (customStyle != null && StringUtils.isNotBlank(customStyle.getFontFamily())) {
			fontFamily = customStyle.getFontFamily();
		}
		if (rowStyle != null && StringUtils.isNotBlank(rowStyle.getFontFamily())) {
			fontFamily = rowStyle.getFontFamily();
		}
		if (colStyle != null && StringUtils.isNotBlank(colStyle.getFontFamily())) {
			fontFamily = colStyle.getFontFamily();
		}
		if (StringUtils.isNotBlank(fontFamily)) {
			run.setFontFamily(fontFamily);
		}
		int fontSize = style.getFontSize();
		if (customStyle != null && customStyle.getFontSize() > 0) {
			fontSize = customStyle.getFontSize();
		}
		if (rowStyle != null && rowStyle.getFontSize() > 0) {
			fontSize = rowStyle.getFontSize();
		}
		if (colStyle != null && colStyle.getFontSize() > 0) {
			fontSize = colStyle.getFontSize();
		}
		if (fontSize > 0) {
			run.setFontSize(fontSize);
		}
		boolean bold = style.getBold() == null ? false : style.getBold();
		if (customStyle != null && customStyle.getBold() != null) {
			bold = customStyle.getBold();
		}
		if (rowStyle != null && rowStyle.getBold() != null) {
			bold = rowStyle.getBold();
		}
		if (colStyle != null && colStyle.getBold() != null) {
			bold = colStyle.getBold();
		}
		if (bold) {
			run.setBold(true);
		}
		boolean italic = style.getItalic() == null ? false : style.getItalic();
		if (customStyle != null && customStyle.getItalic() != null) {
			italic = customStyle.getItalic();
		}
		if (rowStyle != null && rowStyle.getItalic() != null) {
			italic = rowStyle.getItalic();
		}
		if (colStyle != null && colStyle.getItalic() != null) {
			italic = colStyle.getItalic();
		}
		if (italic) {
			run.setItalic(true);
		}
		boolean underline = style.getUnderline() == null ? false : style.getUnderline();
		if (customStyle != null && customStyle.getUnderline() != null) {
			underline = customStyle.getUnderline();
		}
		if (rowStyle != null && rowStyle.getUnderline() != null) {
			underline = rowStyle.getUnderline();
		}
		if (colStyle != null && colStyle.getUnderline() != null) {
			underline = colStyle.getUnderline();
		}
		if (underline) {
			run.setUnderline(UnderlinePatterns.SINGLE);
		}
		String bgcolor = style.getBgcolor();
		if (customStyle != null && StringUtils.isNotBlank(customStyle.getBgcolor())) {
			bgcolor = customStyle.getBgcolor();
		}
		if (rowStyle != null && StringUtils.isNotBlank(rowStyle.getBgcolor())) {
			bgcolor = rowStyle.getBgcolor();
		}
		if (colStyle != null && StringUtils.isNotBlank(colStyle.getBgcolor())) {
			bgcolor = colStyle.getBgcolor();
		}
		if (bgcolor != null) {
			CTShd ctshd = cellProperties.addNewShd();
			ctshd.setFill(toHex(bgcolor.split(",")));
		}
		String forecolor = style.getForecolor();
		if (customStyle != null && StringUtils.isNotBlank(customStyle.getForecolor())) {
			forecolor = customStyle.getForecolor();
		}
		if (rowStyle != null && StringUtils.isNotBlank(rowStyle.getForecolor())) {
			forecolor = rowStyle.getForecolor();
		}
		if (colStyle != null && StringUtils.isNotBlank(colStyle.getForecolor())) {
			forecolor = colStyle.getForecolor();
		}
		if (forecolor != null) {
			run.setColor(toHex(forecolor.split(",")));
		}
		Alignment align = style.getAlign();
		if (customStyle != null && customStyle.getAlign() != null) {
			align = customStyle.getAlign();
		}
		if (rowStyle != null && rowStyle.getAlign() != null) {
			align = rowStyle.getAlign();
		}
		if (align != null) {
			if (align.equals(Alignment.left)) {
				para.setAlignment(ParagraphAlignment.LEFT);
			} else if (align.equals(Alignment.right)) {
				para.setAlignment(ParagraphAlignment.RIGHT);
			} else if (align.equals(Alignment.center)) {
				para.setAlignment(ParagraphAlignment.CENTER);
			}
		}
		if (style.getLineHeight() > 0) {
			para.setSpacingBetween(style.getLineHeight());
		}
		align = style.getValign();
		if (customStyle != null && customStyle.getValign() != null) {
			align = customStyle.getValign();
		}
		if (rowStyle != null && rowStyle.getValign() != null) {
			align = rowStyle.getValign();
		}
		if (colStyle != null && colStyle.getValign() != null) {
			align = colStyle.getValign();
		}
		CTVerticalJc verticalAlign = null;
		if (align != null) {
			verticalAlign = cellProperties.addNewVAlign();
			if (align.equals(Alignment.top)) {
				verticalAlign.setVal(STVerticalJc.TOP);
			} else if (align.equals(Alignment.middle)) {
				verticalAlign.setVal(STVerticalJc.CENTER);
			} else if (align.equals(Alignment.bottom)) {
				verticalAlign.setVal(STVerticalJc.BOTTOM);
			}
		}
		/*
		 * if (value instanceof ChartData) {
		 * para.setAlignment(ParagraphAlignment.CENTER); if(verticalAlign == null) {
		 * verticalAlign = cellProperties.addNewVAlign(); }
		 * verticalAlign.setVal(STVerticalJc.CENTER); }
		 */
		int startCol = columnNumber;
		int startRow = rowNumber;
		int endRow = startRow, endCol = startCol;
		if (colSpan > 0) {
			endCol = startCol + colSpan - 1;
		}
		if (rowSpan > 0) {
			endRow = startRow + rowSpan - 1;
		}
		if (startCol != endCol) {
			if (rowSpan > 0) {
				for (int i = startRow; i <= endRow; i++) {
					mergeCellsHorizontal(table, i, startCol, endCol);
				}
			} else {
				mergeCellsHorizontal(table, startRow, startCol, endCol);
			}
		}
		if (startRow != endRow) {
			if (colSpan > 0) {
				for (int i = startCol; i <= endCol; i++) {
					mergeCellsVertically(table, i, startRow, endRow);
				}
			} else {
				mergeCellsVertically(table, startCol, startRow, endRow);
			}
		}
	}

	private void mergeCellsHorizontal(XWPFTable table, int row, int startCol, int endCol) {
		for (int cellIndex = startCol; cellIndex <= endCol; cellIndex++) {
			XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
			if (cellIndex == startCol) {
				cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
			} else {
				cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
			}
		}
	}

	private void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
		for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
			XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
			if (rowIndex == fromRow) {
				cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
			} else {
				cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
			}
		}
	}

	private void buildCellBorder(Border border, XWPFTableCell tableCell, int type) {
		CTTcPr cellPropertie = tableCell.getCTTc().getTcPr();
		if (cellPropertie == null) {
			cellPropertie = tableCell.getCTTc().addNewTcPr();
		}
		CTTcBorders borders = cellPropertie.getTcBorders();
		if (borders == null) {
			borders = cellPropertie.addNewTcBorders();
		}
		BorderStyle borderStyle = border.getStyle();
		CTBorder ctborder = null;
		if (type == 1) {
			ctborder = borders.addNewLeft();
		} else if (type == 2) {
			ctborder = borders.addNewRight();
		} else if (type == 3) {
			ctborder = borders.addNewTop();
		} else if (type == 4) {
			ctborder = borders.addNewBottom();
		}
		if (borderStyle.equals(BorderStyle.dashed)) {
			ctborder.setVal(STBorder.DASHED);
		} else if (borderStyle.equals(BorderStyle.doublesolid)) {
			ctborder.setVal(STBorder.DOUBLE);
		} else {
			ctborder.setVal(STBorder.SINGLE);
		}
		int borderWidth = border.getWidth();
		if (borderWidth > 1) {
			ctborder.setSz(BigInteger.valueOf(DxaUtils.points2dxa(borderWidth)));
		}
		String color = border.getColor();
		if (StringUtils.isNotBlank(color)) {
			ctborder.setColor(toHex(color.split(",")));
		}
	}

	private String toHex(String rgb[]) {
		StringBuffer sb = new StringBuffer();
		String R = Integer.toHexString(Integer.valueOf(rgb[0].trim()));
		String G = Integer.toHexString(Integer.valueOf(rgb[1].trim()));
		String B = Integer.toHexString(Integer.valueOf(rgb[2].trim()));
		R = R.length() == 1 ? "0" + R : R;
		G = G.length() == 1 ? "0" + G : G;
		B = B.length() == 1 ? "0" + B : B;
		sb.append(R);
		sb.append(G);
		sb.append(B);
		return sb.toString();
	}
	
	private void addBackgroundPicture(XWPFRun run, String url) throws Exception {
		InputStream input = null;
		try {
			ImageProvider imageProvider = ProviderFactory.getImageProvider(url);
			// 获取图片高宽
			input = imageProvider.getImage(url);
			BufferedImage bufferedImage = ImageIO.read(input);
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();
			width = UnitUtils.pixelToPoint(width);
			height = UnitUtils.pixelToPoint(height);
			IOUtils.closeQuietly(input);
			
			// 插入图片
			input = imageProvider.getImage(url);
			run.addPicture(input, 5, "", Units.toEMU(width),Units.toEMU(height));
			IOUtils.closeQuietly(input);
			
			// 2. 获取到图片数据
			CTDrawing[] drawingArray = run.getCTR().getDrawingArray();
	        CTDrawing drawing = drawingArray[drawingArray.length-1];
	        CTInline[] inlineArray = drawing.getInlineArray();
	        CTGraphicalObject graphicalobject = inlineArray[inlineArray.length-1].getGraphic();
	        //拿到新插入的图片替换添加CTAnchor 设置浮动属性 删除inline属性
	        CTAnchor anchor = getAnchorWithGraphic(graphicalobject, "",
	                Units.toEMU(width), Units.toEMU(height),//图片大小
	                Units.toEMU(0), Units.toEMU(0), true);//相对当前段落位置 需要计算段落已有内容的左偏移
	        drawing.setAnchorArray(new CTAnchor[]{anchor});//添加浮动属性
	        drawing.removeInline(0);//删除行内属性
			
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
	
	/**
     * @param ctGraphicalObject 图片数据
     * @param deskFileName      图片描述
     * @param width             宽
     * @param height            高
     * @param leftOffset        水平偏移 left
     * @param topOffset         垂直偏移 top
     * @param behind            文字上方，文字下方
     * @return
     * @throws Exception
     */
    private CTAnchor getAnchorWithGraphic(CTGraphicalObject ctGraphicalObject,
                                                String deskFileName, int width, int height,
                                                int leftOffset, int topOffset, boolean behind) {
        String anchorXML =
                "<wp:anchor xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\" "
                        + "simplePos=\"0\" relativeHeight=\"0\" behindDoc=\"" + ((behind) ? 1 : 0) + "\" locked=\"0\" layoutInCell=\"1\" allowOverlap=\"1\">"
                        + "<wp:simplePos x=\"0\" y=\"0\"/>"
                        + "<wp:positionH relativeFrom=\"column\">"
                        + "<wp:posOffset>" + leftOffset + "</wp:posOffset>"
                        + "</wp:positionH>"
                        + "<wp:positionV relativeFrom=\"paragraph\">"
                        + "<wp:posOffset>" + topOffset + "</wp:posOffset>" 
                        + "</wp:positionV>"
                        + "<wp:extent cx=\"" + width + "\" cy=\"" + height + "\"/>"
                        + "<wp:effectExtent l=\"0\" t=\"0\" r=\"0\" b=\"0\"/>"
                        + "<wp:wrapNone/>"
                        + "<wp:docPr id=\"1\" name=\"Drawing 0\" descr=\"" + deskFileName + "\"/><wp:cNvGraphicFramePr/>"
                        + "</wp:anchor>";
 
        CTDrawing drawing = null;
        try {
            drawing = CTDrawing.Factory.parse(anchorXML);
        } catch (XmlException e) {
            e.printStackTrace();
        }
        CTAnchor anchor = drawing.getAnchorArray(0);
        anchor.setGraphic(ctGraphicalObject);
        return anchor;
    }
}
