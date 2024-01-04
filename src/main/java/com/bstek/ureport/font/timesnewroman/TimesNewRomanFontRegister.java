package com.bstek.ureport.font.timesnewroman;

import com.bstek.ureport.font.FontRegister;

/**
 * @author Jacky.gao
 * @since 2014年5月7日
 */
public class TimesNewRomanFontRegister implements FontRegister {

	public String getFontName() {
		return "Times New Roman";
	}

	public String getFontPath() {
		return "timesnewroman/TIMES.TTF";
	}
}
