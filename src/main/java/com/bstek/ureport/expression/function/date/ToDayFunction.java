package com.bstek.ureport.expression.function.date;

import java.util.Date;
import java.util.List;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.expression.function.Function;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.model.Cell;

public class ToDayFunction implements Function {

	@Override
	public Object execute(List<ExpressionData<?>> dataList, Context context, Cell currentCell) {
		return new Date();
	}

	@Override
	public String name() {
		return "today";
	}

}
