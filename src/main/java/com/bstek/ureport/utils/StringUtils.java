package com.bstek.ureport.utils;

import java.net.URLDecoder;

public class StringUtils extends org.apache.commons.lang3.StringUtils{

	private StringUtils() {
		
	}
	
	public static String decode(String value) {
		if (value == null) {
			return value;
		}
		try {
			return URLDecoder.decode(value, "utf-8");
		} catch (Exception ex) {
			return value;
		}
	}
}
