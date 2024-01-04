package com.bstek.ureport.expression.function.date;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.expression.function.Function;
import com.bstek.ureport.expression.model.DateAddType;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.expression.model.data.ObjectListExpressionData;
import com.bstek.ureport.model.Cell;

public class DateAddFunction implements Function {

	@Override
	public Object execute(List<ExpressionData<?>> dataList, Context context, Cell currentCell) {
		if(dataList != null && dataList.size() >= 3) {
			Date date = getFirstParameter(dataList.get(0));
			DateAddType type = getSecondParameter(dataList.get(1));
			int count = getThirdParameter(dataList.get(2));
			if(dataList.size() == 3) {
				return getValue(date, type, count);
			} else {
				Boolean end = getFourthParameter(dataList.get(3));
				if(end == null) {
					return getValue(date, type, count);
				}
				return getValue(date, type, count, end);
			}
		} else {
			int size = dataList == null ? 0 : dataList.size();
			throw new ReportComputeException("Function [dateadd] need 3 or 4 parameter but got " + size);
		}
	}
	
	//"dateadd('2023-01-01 00:00:00','month',1)"
	private Date getValue(Date date, DateAddType type, int count) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if(DateAddType.YEAR == type) {
			calendar.add(Calendar.YEAR, count);
		} else if(DateAddType.MONTH == type) {
			calendar.add(Calendar.MONTH, count);
		} else if(DateAddType.DAY == type) {
			calendar.add(Calendar.DATE, count);
		} else if(DateAddType.WEEK == type) {
			calendar.add(Calendar.DATE, count - 7);
		}
		return calendar.getTime();
	}
	
	private Date getValue(Date date, DateAddType type, int count, boolean end) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if(DateAddType.YEAR == type) {
			if(end) {// 结束时间
				calendar.set(Calendar.MONTH, 11);
				calendar.set(Calendar.DATE, 31);
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
			} else { // 开始时间
				calendar.set(Calendar.MONTH, 0);
				calendar.set(Calendar.DATE, 1);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
			}
			calendar.add(Calendar.YEAR, count);
		} else if(DateAddType.MONTH == type) {
			if(end) {// 结束时间
				calendar.set(Calendar.DAY_OF_MONTH, 0);
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
				count++;
			} else {// 开始时间
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
			}
			calendar.add(Calendar.MONTH, count);
		} else if(DateAddType.DAY == type) {
			if(end) {// 结束时间
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
			} else { // 开始时间
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
			}
			calendar.add(Calendar.DATE, count);
		} else if(DateAddType.WEEK == type) {
			if(end) {// 结束时间
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				calendar.add(Calendar.DATE, count * 7 + 6);
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
			} else { // 开始时间
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				calendar.add(Calendar.DATE, count * 7);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
			}
		}
		return calendar.getTime();
	}
	
	private Date getFirstParameter(ExpressionData<?> data) {
		if(data instanceof ObjectListExpressionData) {
			ObjectListExpressionData listData = (ObjectListExpressionData)data;
			List<?> list = listData.getData();
			if(list == null || list.size() != 1) {
				throw new ReportComputeException("Function [dateadd] first parameter need a data of Date.");
			}
			Object obj = list.get(0);
			if(obj == null) {
				throw new ReportComputeException("Function [dateadd] first parameter can not be null.");
			}
			return Utils.toDate(obj);
		} 
		if(data instanceof ObjectExpressionData) {
			ObjectExpressionData objData = (ObjectExpressionData)data;
			Object obj = objData.getData();
			if(obj == null){
				throw new ReportComputeException("Function [dateadd] first parameter can not be null.");
			}
			return Utils.toDate(obj);
		}
		throw new ReportComputeException("Function [dateadd] first parameter need a data of Date.");
	}
	
	private DateAddType getSecondParameter(ExpressionData<?> data) {
		if(data instanceof ObjectListExpressionData) {
			ObjectListExpressionData listData = (ObjectListExpressionData)data;
			List<?> list = listData.getData();
			if(list == null || list.size() != 1) {
				throw new ReportComputeException("Function [dateadd] second parameter need a String of [YEAR,MONTH,DAY,WEEK].");
			}
			Object obj = list.get(0);
			if(obj == null) {
				throw new ReportComputeException("Function [dateadd] second parameter can not be null.");
			}
			try {
				return DateAddType.valueOf(obj.toString().trim().toUpperCase());
			} catch (Exception e) {
				throw new ReportComputeException("Function [dateadd] second parameter need a String of [YEAR,MONTH,DAY,WEEK].");
			}
		} 
		if(data instanceof ObjectExpressionData) {
			ObjectExpressionData objData = (ObjectExpressionData)data;
			Object obj = objData.getData();
			if(obj == null){
				throw new ReportComputeException("Function [dateadd] second parameter can not be null.");
			}
			try {
				return DateAddType.valueOf(obj.toString().trim().toUpperCase());
			} catch (Exception e) {
				throw new ReportComputeException("Function [dateadd] second parameter need a String of [YEAR,MONTH,DAY,WEEK].");
			}
		}
		throw new ReportComputeException("Function [dateadd] second parameter can not be null.");
	}
	
	private int getThirdParameter(ExpressionData<?> data) {
		if(data instanceof ObjectListExpressionData) {
			ObjectListExpressionData listData = (ObjectListExpressionData)data;
			List<?> list = listData.getData();
			if(list == null || list.size() != 1) {
				throw new ReportComputeException("Function [dateadd] third parameter need a integer.");
			}
			Object obj = list.get(0);
			if(obj == null) {
				throw new ReportComputeException("Function [dateadd] third parameter can not be null.");
			}
			try {
				return Integer.parseInt(obj.toString().trim());
			} catch (Exception e) {
				throw new ReportComputeException("Function [dateadd] third parameter need a integer.");
			}
		} 
		if(data instanceof ObjectExpressionData) {
			ObjectExpressionData objData = (ObjectExpressionData)data;
			Object obj = objData.getData();
			if(obj == null){
				throw new ReportComputeException("Function [dateadd] third parameter can not be null.");
			}
			try {
				return Integer.parseInt(obj.toString().trim());
			} catch (Exception e) {
				throw new ReportComputeException("Function [dateadd] third parameter need a integer.");
			}
		}
		throw new ReportComputeException("Function [dateadd] third parameter can not be null.");
	}
	
	private Boolean getFourthParameter(ExpressionData<?> data) {
		if(data instanceof ObjectListExpressionData) {
			ObjectListExpressionData listData = (ObjectListExpressionData)data;
			List<?> list = listData.getData();
			if(list == null) {
				return null;
			}
			if(list.size() != 1) {
				throw new ReportComputeException("Function [dateadd] fourth parameter need a boolean.");
			}
			Object obj = list.get(0);
			if(obj == null) {
				return null;
			}
			try {
				return Boolean.valueOf(obj.toString().trim());
			} catch (Exception e) {
				throw new ReportComputeException("Function [dateadd] third parameter need a boolean.");
			}
		} 
		if(data instanceof ObjectExpressionData) {
			ObjectExpressionData objData = (ObjectExpressionData)data;
			Object obj = objData.getData();
			if(obj == null){
				return null;
			}
			try {
				return Boolean.valueOf(obj.toString().trim());
			} catch (Exception e) {
				throw new ReportComputeException("Function [dateadd] third parameter need a boolean.");
			}
		}
		return null;
	}

	@Override
	public String name() {
		return "dateadd";
	}
}
