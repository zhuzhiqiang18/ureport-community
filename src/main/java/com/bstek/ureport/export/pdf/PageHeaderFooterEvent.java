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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.bstek.common.utils.StringUtils;
import com.bstek.ureport.build.paging.HeaderFooter;
import com.bstek.ureport.build.paging.Page;
import com.bstek.ureport.definition.Orientation;
import com.bstek.ureport.definition.Paper;
import com.bstek.ureport.font.FontBuilder;
import com.bstek.ureport.model.Report;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author Jacky.gao
 * @since 2014年4月22日
 */
public class PageHeaderFooterEvent extends PdfPageEventHelper {
	private Report report;

	public PageHeaderFooterEvent(Report report) {
		this.report = report;
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		List<Page> pages = report.getPages();
		int pageNumber = writer.getPageNumber();
		if (pageNumber > pages.size()) {
			return;
		}
		
		Page page = pages.get(pageNumber - 1);
		HeaderFooter header = page.getHeader();
		HeaderFooter footer = page.getFooter();
		
		Paper paper = report.getPaper();
		int totalPage = pages.size();
		// 分栏总页数
		if(paper.isColumnEnabled()) {
			int columnCount = paper.getColumnCount();
			totalPage = pages.size() / columnCount + (pages.size() % columnCount == 0 ? 0 : 1);
		}
		
		int width = paper.getWidth();
		if(paper.getOrientation().equals(Orientation.landscape)){
			width = paper.getHeight();
		}
		int leftMargin = paper.getLeftMargin();
		int rightMargin = paper.getRightMargin();
		int height = paper.getHeight();
		if (paper.getOrientation().equals(Orientation.landscape)) {
			height = paper.getWidth();
		}
		Date now = new Date();
		String date = new SimpleDateFormat("yyyy-MM-dd").format(now);
		String time = new SimpleDateFormat("HH:MM:ss").format(now);
		
		if (header != null) {
			int margin = header.getMargin();
			int x = 0;
			int y = height - margin;
			String center = parserHeaderFooter(header.getCenter(), pageNumber, totalPage, date, time);
			Phrase phrase = buildParagraph(header, center);
			int align = Element.ALIGN_LEFT;
			if("left".equals(header.getAlign())) {
				x = leftMargin;
				align = Element.ALIGN_LEFT;
			} else if("center".equals(header.getAlign())) {
				x = width / 2;
				align = Element.ALIGN_CENTER;
			} else {
				x = width - rightMargin;
				align = Element.ALIGN_RIGHT;
			}
			ColumnText.showTextAligned(writer.getDirectContent(), align, phrase, x, y, 0);
		}
		if (footer != null) {
			int margin = footer.getMargin();
			int x = 0;
			int y = margin - 6;
			String center = parserHeaderFooter(footer.getCenter(), pageNumber, totalPage, date, time);
			Phrase phrase = buildParagraph(footer, center);
			int align = Element.ALIGN_LEFT;
			if("left".equals(footer.getAlign())) {
				x = leftMargin;
				align = Element.ALIGN_LEFT;
			} else if("center".equals(footer.getAlign())) {
				x = width / 2;
				align = Element.ALIGN_CENTER;
			} else {
				x = width - rightMargin;
				align = Element.ALIGN_RIGHT;
			}
			ColumnText.showTextAligned(writer.getDirectContent(), align, phrase, x, y, 0);
		}
	}

	private Paragraph buildParagraph(HeaderFooter phf, String text) {
		Font font = FontBuilder.getFont(phf.getFontFamily(), phf.getFontSize(), phf.isBold(), phf.isItalic(),phf.isUnderline());
		String fontColor = phf.getForecolor();
		if (StringUtils.isNotEmpty(fontColor)) {
			String[] color = fontColor.split(",");
			Integer r = Integer.valueOf(color[0].trim());
			Integer g = Integer.valueOf(color[1].trim());
			Integer b = Integer.valueOf(color[2].trim());
			font.setColor(r, g, b);
		}
		Paragraph graph = new Paragraph(text, font);
		return graph;
	}
	
	
	private String parserHeaderFooter(String text, Integer pageNumber, Integer totalPage, String date, String time) {
		if(text != null) {
			if(text.contains("$[PAGE]")) {
				text = text.replaceAll("[$]\\[PAGE\\]", pageNumber.toString());
			}
			if(text.contains("$[PAGES]")) {
				text = text.replaceAll("[$]\\[PAGES\\]", totalPage.toString());
			}
			if(text.contains("$[DATE]")) {
				text = text.replaceAll("[$]\\[DATE\\]", date);
			}
			if(text.contains("$[TIME]")) {
				text = text.replaceAll("[$]\\[TIME\\]", time);
			}
		}
		return text;
	}
}
