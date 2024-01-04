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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

import com.bstek.ureport.build.paging.HeaderFooter;
import com.bstek.ureport.build.paging.Page;
import com.bstek.ureport.model.Report;

/**
 * @author Jacky.gao
 * @since 2017年4月17日
 */
public class HeaderFooterBuilder {

	public void build(XWPFDocument document, CTSectPr sectPr, Report report) {
		List<Page> pages = report.getPages();
		if (pages != null && pages.size() > 0) {
			Page page = pages.get(0);
			Date now = new Date();
			String date = new SimpleDateFormat("yyyy-MM-dd").format(now);
			String time = new SimpleDateFormat("HH:MM:ss").format(now);
			HeaderFooter h = page.getHeader();
			if (h != null) {
				String content = h.getCenter();
				XWPFHeader header = document.createHeader(HeaderFooterType.DEFAULT);
				XWPFParagraph paragraph = header.createParagraph();
				setParagraphAlignment(h, paragraph);
				buiderParagraph(h, paragraph, content, date, time);
			}
			HeaderFooter f = page.getFooter();
			if (f != null) {
				String content = f.getCenter();
				XWPFFooter header = document.createFooter(HeaderFooterType.DEFAULT);
				XWPFParagraph paragraph = header.createParagraph();
				setParagraphAlignment(f, paragraph);
				buiderParagraph(f, paragraph, content, date, time);
			}
		}
	}

	private void setParagraphAlignment(HeaderFooter hf, XWPFParagraph paragraph) {
		if ("left".equals(hf.getAlign())) {
			paragraph.setAlignment(ParagraphAlignment.LEFT);
		} else if ("center".equals(hf.getAlign())) {
			paragraph.setAlignment(ParagraphAlignment.CENTER);
		} else {
			paragraph.setAlignment(ParagraphAlignment.RIGHT);
		}
	}

	private void buiderParagraph(HeaderFooter hf, XWPFParagraph para, String content, String date, String time) {
		List<String> list = splitHeaderFooterContent(content);
		XWPFRun r1 = null;
		for (int i = 0; i < list.size(); i++) {
			String text = list.get(i);
			r1 = para.createRun();
			if (text.equals("$[PAGE]")) {
				para.getCTP().addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");
			} else if (text.equals("$[PAGES]")) {
				para.getCTP().addNewFldSimple().setInstr("NUMPAGES \\* MERGEFORMAT");
			} else if (text.equals("$[DATE]")) {
				r1.setText(date);
			} else if (text.equals("$[TIME]")) {
				r1.setText(time);
			} else {
				r1.setText(text);
			}
			// 设置样式
			setStyle(r1, hf);
		}
	}

	private void setStyle(XWPFRun run, HeaderFooter hf) {
		if (hf.getFontSize() > 1) {
			run.setFontSize(hf.getFontSize());
		}
		CTRPr rpr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
		CTFonts font = null;
		CTFonts[] fonts = rpr.getRFontsArray();
		if (fonts == null || fonts.length == 0) {
			font = rpr.addNewRFonts();
		} else {
			font = fonts[0];
		}
		String fontName = hf.getFontFamily();
		if (fontName != null) {
			font.setAscii(fontName);
			font.setEastAsia(fontName);
			font.setHAnsi(fontName);
		}
		if (hf.isBold()) {
			run.setBold(true);
		}
		if (hf.isItalic()) {
			run.setItalic(true);
		}
	}

	private List<String> splitHeaderFooterContent(String info) {
		Pattern pattern = Pattern.compile("\\$\\[PAGE\\]|\\$\\[PAGES\\]|\\$\\[DATE\\]|\\$\\[TIME\\]");
		Matcher matcher = pattern.matcher(info);
		List<String> list = new ArrayList<String>();
		int start = 0;
		while (matcher.find()) {
			String text = matcher.group();
			int pos = info.indexOf(text);
			String str = info.substring(start, pos);
			start = pos + text.length();
			list.add(str);
			list.add(text);
		}
		if (start < info.length()) {
			list.add(info.substring(start, info.length()));
		}
		return list;
	}

}
