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
package com.bstek.ureport.export.pdf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.bstek.ureport.build.paging.Page;
import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.definition.Alignment;
import com.bstek.ureport.definition.CellStyle;
import com.bstek.ureport.definition.Orientation;
import com.bstek.ureport.definition.Paper;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.export.FullPageData;
import com.bstek.ureport.export.PageBuilder;
import com.bstek.ureport.export.Producer;
import com.bstek.ureport.font.FontBuilder;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.model.Column;
import com.bstek.ureport.model.Image;
import com.bstek.ureport.model.Report;
import com.bstek.ureport.model.Row;
import com.bstek.ureport.provider.ProviderFactory;
import com.bstek.ureport.provider.image.ImageProvider;
import com.bstek.ureport.utils.ImageUtils;
import com.bstek.ureport.utils.UnitUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.SplitCharacter;
import com.itextpdf.text.pdf.PdfChunk;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author Jacky.gao
 * @since 2017年3月10日
 */
public class PdfProducer implements Producer {
	
	@Override
	public void produce(Report report, OutputStream outputStream) {
		Paper paper = report.getPaper();
		int width = paper.getWidth();
		int height = paper.getHeight();
		Rectangle pageSize = new RectangleReadOnly(width, height);
		if (paper.getOrientation().equals(Orientation.landscape)) {
			pageSize = pageSize.rotate();
		}
		int leftMargin = paper.getLeftMargin();
		int rightMargin = paper.getRightMargin();
		int topMargin = paper.getTopMargin();
		int bottomMargin = paper.getBottomMargin();
		Document document = new Document(pageSize, leftMargin, rightMargin, topMargin, bottomMargin);
		try {
			
			Map<String, String> chartImages = report.getChartImages();
			PdfWriter writer = PdfWriter.getInstance(document, outputStream);
			PageHeaderFooterEvent headerFooterEvent = new PageHeaderFooterEvent(report);
			writer.setPageEvent(headerFooterEvent);
			document.open();
			
			// 加载背景图
			String bgImage = paper.getBgImage();
			com.itextpdf.text.Image image = null;
			if(StringUtils.isNotBlank(bgImage) && paper.isPrint()) {
				image = buildPdfImage(bgImage);
				document.add(image);
			}
			List<Column> columns = report.getColumns();
			// 最后一列为人工补的扩展列,需要去掉
			columns.remove(columns.size() - 1);
			List<Integer> columnsWidthList = new ArrayList<Integer>();
			int[] intArr = buildColumnSizeAndTotalWidth(columns, columnsWidthList);
			int colSize = intArr[0];
			int totalWidth = intArr[1];
			int[] columnsWidth = new int[columnsWidthList.size()];
			for (int i = 0; i < columnsWidthList.size(); i++) {
				columnsWidth[i] = columnsWidthList.get(i);
			}
			int pageIndex = 1;
			FullPageData pageData = PageBuilder.buildFullPageData(report);
			List<List<Page>> list = pageData.getPageList();
			if (list.size() > 0) {
				int columnCount = paper.getColumnCount();
				int w = columnCount * totalWidth + (columnCount - 1) * paper.getColumnMargin();
				int size = columnCount + (columnCount - 1);
				int[] widths = new int[size];
				for (int i = 0; i < size; i++) {
					int mode = (i + 1) % 2;
					if (mode == 0) {
						widths[i] = paper.getColumnMargin();
					} else {
						widths[i] = totalWidth;
					}
				}
				float tableHeight = pageSize.getHeight() - paper.getTopMargin() - paper.getBottomMargin();
				Map<Row, Map<Column, Cell>> cellMap = report.getRowColCellMap();
				for (List<Page> pages : list) {
					PdfPTable table = new PdfPTable(size);
					table.setSplitLate(false);
					table.setSplitRows(true);
					table.setLockedWidth(true);
					table.setTotalWidth(w);
					table.setWidths(widths);
					table.setHorizontalAlignment(Element.ALIGN_LEFT);
					int ps = pages.size();
					for (int i = 0; i < ps; i++) {
						if (i > 0) {
							PdfPCell pdfMarginCell = new PdfPCell();
							pdfMarginCell.setBorder(Rectangle.NO_BORDER);
							table.addCell(pdfMarginCell);
						}
						Page page = pages.get(i);

						PdfPTable childTable = new PdfPTable(colSize);
						childTable.setSplitLate(false);
						childTable.setSplitRows(true);
						childTable.setLockedWidth(true);
						childTable.setTotalWidth(totalWidth);
						childTable.setWidths(columnsWidth);
						childTable.setHorizontalAlignment(Element.ALIGN_LEFT);
						List<Row> rows = page.getRows();
						for (Row row : rows) {
							Map<Column, Cell> colMap = cellMap.get(row);
							if (colMap == null) {
								continue;
							}
							for (Column col : columns) {
								if (col.getWidth() < 1) {
									continue;
								}
								Cell cell = colMap.get(col);
								if (cell == null) {
									continue;
								}
								int cellHeight = buildCellHeight(cell, rows);
								PdfPCell pdfcell = buildPdfPCell(cell, cellHeight, chartImages);
								childTable.addCell(pdfcell);
							}
						}
						float childTableHeight = childTable.calculateHeights();
						if (tableHeight > childTableHeight) {
							for (int j = 0; j < columns.size(); j++) {
								PdfPCell lastCell = new PdfPCell();
								lastCell.setBorder(Rectangle.NO_BORDER);
								childTable.addCell(lastCell);
							}
						}
						PdfPCell pdfContainerCell = new PdfPCell(childTable);
						pdfContainerCell.setBorder(Rectangle.NO_BORDER);
						table.addCell(pdfContainerCell);
					}
					if (ps < columnCount) {
						int left = columnCount - ps;
						for (int i = 0; i < left; i++) {
							PdfPCell pdfMarginCell = new PdfPCell();
							pdfMarginCell.setBorder(Rectangle.NO_BORDER);
							table.addCell(pdfMarginCell);
							pdfMarginCell = new PdfPCell();
							pdfMarginCell.setBorder(Rectangle.NO_BORDER);
							table.addCell(pdfMarginCell);
						}
					}
					document.add(table);
					document.newPage();
					if(pageIndex < list.size() && image != null) {
						document.add(image);
					}
					pageIndex++;
				}

			} else {
				List<Page> pages = report.getPages();
				Map<Row, Map<Column, Cell>> cellMap = report.getRowColCellMap();
				for (Page page : pages) {
					PdfPTable table = new PdfPTable(colSize);
					table.setSplitLate(false);
					table.setSplitRows(true);
					table.setLockedWidth(true);
					table.setTotalWidth(totalWidth);
					table.setWidths(columnsWidth);
					table.setHorizontalAlignment(Element.ALIGN_LEFT);
					List<Row> rows = page.getRows();
					for (Row row : rows) {
						Map<Column, Cell> colMap = cellMap.get(row);
						if (colMap == null) {
							continue;
						}
						for (Column col : columns) {
							if (col.getWidth() < 1) {
								continue;
							}
							Cell cell = colMap.get(col);
							if (cell == null) {
								continue;
							}
							int cellHeight = buildCellHeight(cell, rows);
							PdfPCell pdfcell = buildPdfPCell(cell, cellHeight, chartImages);
							table.addCell(pdfcell);
						}
					}
					document.add(table);
					document.newPage();
					if(pageIndex < pages.size()  && image != null) {
						document.add(image);
					}
					pageIndex++;
				}
			}
			document.close();
		} catch (Exception ex) {
			throw new ReportComputeException(ex);
		}
	}

	private int buildCellHeight(Cell cell, List<Row> rows) {
		int height = cell.getRow().getRealHeight();
		int rowSpan = cell.getPageRowSpan();
		if (rowSpan > 0) {
			int pos = rows.indexOf(cell.getRow());
			int start = pos + 1, end = start + rowSpan - 1;
			for (int i = start; i < end; i++) {
				height += rows.get(i).getRealHeight();
			}
		}
		return height;
	}

	private PdfPCell buildPdfPCell(Cell cellInfo, int cellHeight, Map<String, String> chartImages) throws Exception {
		CellStyle style = cellInfo.getCellStyle();
		CellStyle customStyle = cellInfo.getCustomCellStyle();
		CellStyle rowStyle = cellInfo.getRow().getCustomCellStyle();
		CellStyle colStyle = cellInfo.getColumn().getCustomCellStyle();
		PdfPCell cell = newPdfCell(cellInfo, cellHeight, chartImages);
		cell.setPadding(0);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setMinimumHeight(cellHeight); // 设置单元格高度
		cell.setCellEvent(new CellBorderEvent(style, customStyle));
		int rowSpan = cellInfo.getPageRowSpan();
		if (rowSpan > 0) {
			cell.setRowspan(rowSpan);
		}
		int colSpan = cellInfo.getColSpan();
		if (colSpan > 0) {
			cell.setColspan(colSpan);
		}
		Alignment align = style.getAlign();
		if (customStyle != null && customStyle.getAlign() != null) {
			align = customStyle.getAlign();
		}
		if (rowStyle != null && rowStyle.getAlign() != null) {
			align = rowStyle.getAlign();
		}
		if (colStyle != null && colStyle.getAlign() != null) {
			align = colStyle.getAlign();
		}
		if (align != null) {
			if (align.equals(Alignment.left)) {
				cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			} else if (align.equals(Alignment.center)) {
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			} else if (align.equals(Alignment.right)) {
				cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
			}
		}
		Alignment valign = style.getValign();
		if (customStyle != null && customStyle.getValign() != null) {
			valign = customStyle.getValign();
		}
		if (rowStyle != null && rowStyle.getValign() != null) {
			valign = rowStyle.getValign();
		}
		if (colStyle != null && colStyle.getValign() != null) {
			valign = colStyle.getValign();
		}
		if (valign != null) {
			if (valign.equals(Alignment.top)) {
				cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			} else if (valign.equals(Alignment.middle)) {
				cell.setUseAscender(true);
				cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			} else if (valign.equals(Alignment.bottom)) {
				cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
			}
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
		if (StringUtils.isNotEmpty(bgcolor)) {
			String[] colors = bgcolor.split(",");
			cell.setBackgroundColor(
					new BaseColor(
							Integer.valueOf(colors[0].trim()), 
							Integer.valueOf(colors[1].trim()), 
							Integer.valueOf(colors[2].trim())));
		}
		return cell;
	}

	private int[] buildColumnSizeAndTotalWidth(List<Column> columns, List<Integer> list) {
		int count = 0, totalWidth = 0;
		for (int i = 0; i < columns.size(); i++) {
			Column col = columns.get(i);
			int width = col.getWidth();
			if (width < 1) {
				continue;
			}
			count++;
			list.add(width);
			totalWidth += width;
		}
		return new int[] { count, totalWidth };
	}
	
	private PdfPCell newPdfCell(Cell cellInfo, int cellHeight, Map<String, String> chartImages) throws Exception {
		PdfPCell cell = null;
		Object cellData = cellInfo.getFormatData();
		if (cellData instanceof Image) {
			Image img = (Image) cellData;
			cell = new PdfPCell(buildPdfImage(img.getBase64Data(), 0, 0));
		} else if (cellData instanceof ChartData) {
			ChartData chartData = (ChartData) cellData;
			String chartId = chartData.getId();
			String base64Data = chartImages.get(chartId);
			if (base64Data != null) {
				base64Data = base64Data.replaceFirst("data:image/png;base64,", "");
				Image img = new Image(base64Data, chartData.getWidth(), cellHeight);
				cell = new PdfPCell(buildPdfImage(img.getBase64Data(), 0, cellHeight));
				cell.setFixedHeight(cellHeight);
			} else {
				cell = new PdfPCell(new Phrase(""));
				cell.setFixedHeight(cellHeight);
			}
		} else {
			String text = cellData == null ? StringUtils.EMPTY : String.valueOf(cellData);
			Font font = buildCellFont(cellInfo);
			Chunk chunk = new Chunk(text, font);
	        chunk.setSplitCharacter(new OwnerSplitCharacter());
			cell = new PdfPCell(new Phrase(chunk));
		}
		CellStyle style = cellInfo.getCellStyle();
		if (style != null && style.getLineHeight() > 0) {
			cell.setLeading(style.getLineHeight(), style.getLineHeight());
		}
		return cell;
	}
	
	private static class OwnerSplitCharacter implements SplitCharacter {
		
        @Override
        public boolean isSplitCharacter(int i, int i1, int i2, char[] chars, PdfChunk[] pdfChunks) {
            return false;
        }
    }
	
	private com.itextpdf.text.Image buildPdfImage(String url) throws Exception {
		com.itextpdf.text.Image pdfImg = null;
		InputStream input = null;
		try {
			ImageProvider imageProvider = ProviderFactory.getImageProvider(url);
			input = imageProvider.getImage(url);
			byte[] bytes = IOUtils.toByteArray(input);
			pdfImg = com.itextpdf.text.Image.getInstance(bytes);
			pdfImg.setAlignment(com.itextpdf.text.Image.UNDERLYING);
			int width = Float.valueOf(pdfImg.getWidth()).intValue();
			int height = Float.valueOf(pdfImg.getHeight()).intValue();
			width = UnitUtils.pixelToPoint(width);
			height = UnitUtils.pixelToPoint(height);
			pdfImg.scaleToFit(width, height);
		} finally {
			IOUtils.closeQuietly(input);
		}
		return pdfImg;
	}

	private com.itextpdf.text.Image buildPdfImage(String base64Data, int width, int height) throws Exception {
		com.itextpdf.text.Image pdfImg = null;
		InputStream input = ImageUtils.base64DataToInputStream(base64Data);
		try {
			byte[] bytes = IOUtils.toByteArray(input);
			pdfImg = com.itextpdf.text.Image.getInstance(bytes);
			float imgWidth = pdfImg.getWidth();
			float imgHeight = pdfImg.getHeight();
			if (width == 0) {
				width = Float.valueOf(imgWidth).intValue();
			}
			if (height == 0) {
				height = Float.valueOf(imgHeight).intValue();
			}
			width = UnitUtils.pixelToPoint(width - 2);
			height = UnitUtils.pixelToPoint(height - 2);
			pdfImg.scaleToFit(width, height);
		} finally {
			IOUtils.closeQuietly(input);
		}
		return pdfImg;
	}
	
	private Font buildCellFont(Cell cell) {
		CellStyle style = cell.getCellStyle();
		CellStyle customStyle = cell.getCustomCellStyle();
		CellStyle rowStyle = cell.getRow().getCustomCellStyle();
		CellStyle colStyle = cell.getColumn().getCustomCellStyle();
		String fontName = style.getFontFamily();
		if (customStyle != null && StringUtils.isNotBlank(customStyle.getFontFamily())) {
			fontName = customStyle.getFontFamily();
		}
		if (rowStyle != null && StringUtils.isNotBlank(rowStyle.getFontFamily())) {
			fontName = rowStyle.getFontFamily();
		}
		if (colStyle != null && StringUtils.isNotBlank(colStyle.getFontFamily())) {
			fontName = colStyle.getFontFamily();
		}
		int fontSize = style.getFontSize();
		Boolean bold = style.getBold();
		Boolean italic = style.getItalic();
		Boolean	underline = style.getUnderline();
		if (customStyle != null) {
			if (customStyle.getBold() != null) {
				bold = customStyle.getBold();
			}
			if (customStyle.getItalic() != null) {
				italic = customStyle.getItalic();
			}
			if (customStyle.getUnderline() != null) {
				underline = customStyle.getUnderline();
			}
			if (customStyle.getFontSize() > 0) {
				fontSize = customStyle.getFontSize();
			}
		}
		if (rowStyle != null) {
			if (rowStyle.getBold() != null) {
				bold = rowStyle.getBold();
			}
			if (rowStyle.getItalic() != null) {
				italic = rowStyle.getItalic();
			}
			if (rowStyle.getUnderline() != null) {
				underline = rowStyle.getUnderline();
			}
			if (rowStyle.getFontSize() > 0) {
				fontSize = rowStyle.getFontSize();
			}
		}
		if (colStyle != null) {
			if (colStyle.getBold() != null) {
				bold = colStyle.getBold();
			}
			if (colStyle.getItalic() != null) {
				italic = colStyle.getItalic();
			}
			if (colStyle.getUnderline() != null) {
				underline = colStyle.getUnderline();
			}
			if (colStyle.getFontSize() > 0) {
				fontSize = colStyle.getFontSize();
			}
		}
		if (bold == null)
			bold = false;
		if (italic == null)
			italic = false;
		if (underline == null)
			underline = false;
		if (StringUtils.isBlank(fontName)) {
			fontName = "宋体";
		}
		Font font = FontBuilder.getFont(fontName, fontSize, bold, italic, underline);
		String fontColor = style.getForecolor();
		if (customStyle != null && StringUtils.isNotBlank(customStyle.getForecolor())) {
			fontColor = customStyle.getForecolor();
		}
		if (rowStyle != null && StringUtils.isNotBlank(rowStyle.getForecolor())) {
			fontColor = rowStyle.getForecolor();
		}
		if (colStyle != null && StringUtils.isNotBlank(colStyle.getForecolor())) {
			fontColor = colStyle.getForecolor();
		}
		if (StringUtils.isNotEmpty(fontColor)) {
			String[] color = fontColor.split(",");
			Integer r = Integer.valueOf(color[0].trim());
			Integer g = Integer.valueOf(color[1].trim());
			Integer b = Integer.valueOf(color[2].trim());
			font.setColor(r, g, b);
		}
		return font;
	}
}
