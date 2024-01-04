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
package com.bstek.ureport.font;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.font.arial.ArialFontRegister;
import com.bstek.ureport.font.comicsansms.ComicSansMSFontRegister;
import com.bstek.ureport.font.couriernew.CourierNewFontRegister;
import com.bstek.ureport.font.fangsong.FangSongFontRegister;
import com.bstek.ureport.font.heiti.HeiTiFontRegister;
import com.bstek.ureport.font.impact.ImpactFontRegister;
import com.bstek.ureport.font.kaiti.KaiTiFontRegister;
import com.bstek.ureport.font.songti.SongTiFontRegister;
import com.bstek.ureport.font.timesnewroman.TimesNewRomanFontRegister;
import com.bstek.ureport.font.yahei.YaheiFontRegister;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;

/**
 * @author Jacky.gao
 * @since 2014年4月22日
 */
public class FontBuilder {

	private static final Map<String, BaseFont> fontMap = new HashMap<String, BaseFont>();

	static {
		// 添加用户自定义字体
		initIdentityFont(new ArialFontRegister());

		initIdentityFont(new ComicSansMSFontRegister());

		initIdentityFont(new CourierNewFontRegister());

		initIdentityFont(new FangSongFontRegister());

		initIdentityFont(new HeiTiFontRegister());

		initIdentityFont(new ImpactFontRegister());

		initIdentityFont(new KaiTiFontRegister());

		initIdentityFont(new SongTiFontRegister());

		initIdentityFont(new TimesNewRomanFontRegister());

		initIdentityFont(new YaheiFontRegister());
	}

	public static Font getFont(String fontName, int fontSize, boolean fontBold, boolean fontItalic, boolean underLine) {
		BaseFont baseFont = fontMap.get(fontName);
		Font font = null;
		if (baseFont != null) {
			font = new Font(baseFont);
		} else {
			font = FontFactory.getFont(fontName);
		}
		font.setSize(fontSize);
		int fontStyle = Font.NORMAL;
		if (fontBold && fontItalic && underLine) {
			fontStyle = Font.BOLD | Font.ITALIC | Font.UNDERLINE;
		} else if (fontBold) {
			if (fontItalic) {
				fontStyle = Font.BOLD | Font.ITALIC;
			} else if (underLine) {
				fontStyle = Font.BOLD | Font.UNDERLINE;
			} else {
				fontStyle = Font.BOLD;
			}
		} else if (fontItalic) {
			if (underLine) {
				fontStyle = Font.ITALIC | Font.UNDERLINE;
			} else if (fontBold) {
				fontStyle = Font.ITALIC | Font.BOLD;
			} else {
				fontStyle = Font.ITALIC;
			}
		} else if (underLine) {
			fontStyle = Font.UNDERLINE;
		}
		font.setStyle(fontStyle);
		return font;
	}
	
	private static void initIdentityFont(FontRegister fontRegister) {
		String fontFamily = fontRegister.getFontName();
		String fontPath =  fontRegister.getFontPath();
		InputStream inputStream = null;
		try {
			String fontName = fontPath.substring(fontPath.lastIndexOf("/") + 1,fontPath.length());
			if (fontName.toLowerCase().endsWith(".ttc")) {
				fontName = fontName + ",0";
			}
			String packageName = FontBuilder.class.getPackage().getName().replaceAll("\\.", "/");
			URL url = FontBuilder.class.getClassLoader().getResource(packageName + "/" + fontPath);
			inputStream = url.openStream();
			byte[] bytes = IOUtils.toByteArray(inputStream);
			BaseFont baseFont = BaseFont.createFont(fontName, BaseFont.IDENTITY_H,BaseFont.EMBEDDED,true,bytes,null);
			baseFont.setSubset(true);
			fontMap.put(fontFamily, baseFont);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ReportComputeException(e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}
}
