package com.bstek.ureport.parser.impl;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import com.bstek.ureport.definition.FreezeCellDefinition;
import com.bstek.ureport.parser.Parser;

public class FreezeCellParser implements Parser<FreezeCellDefinition>{

	@Override
	public FreezeCellDefinition parse(Element element) {
		FreezeCellDefinition freeze = new FreezeCellDefinition();
		String colIndex = element.attributeValue("col");
		if(StringUtils.isNotBlank(colIndex)) {
			freeze.setCol(Integer.valueOf(colIndex));
		}
		String rowIndex = element.attributeValue("row");
		if(StringUtils.isNotBlank(rowIndex)) {
			freeze.setRow(Integer.valueOf(rowIndex));
		}
		return freeze;
	}
}
