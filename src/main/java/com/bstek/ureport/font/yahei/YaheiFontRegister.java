package com.bstek.ureport.font.yahei;

import com.bstek.ureport.font.FontRegister;

/**
 * @author Jacky.gao
 * @since 2014年5月7日
 */
public class YaheiFontRegister implements FontRegister {

	public String getFontName() {
		return "微软雅黑";
	}

	public String getFontPath() {
		return "yahei/msyh.ttc";
	}
}
