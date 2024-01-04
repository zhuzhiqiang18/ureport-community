package com.bstek.ureport.font.kaiti;

import com.bstek.ureport.font.FontRegister;

/**
 * @author Jacky.gao
 * @since 2014年5月7日
 */
public class KaiTiFontRegister implements FontRegister {

	public String getFontName() {
		return "楷体";
	}

	public String getFontPath() {
		return "kaiti/SIMKAI.TTF";
	}
}
