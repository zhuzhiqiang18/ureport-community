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
package com.bstek.ureport;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.exception.ConvertException;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.model.Report;

/**
 * @author Jacky.gao
 * @since 2016年11月12日
 */
public class Utils  {
	
	private static boolean debug = true;

	public static boolean isDebug() {
		return Utils.debug;
	}

	public static String format(double value,Integer decimal) {
		if (decimal != null) {
			return String.format("%." + decimal.intValue() + "f", value);
		}
		return String.valueOf(value);
	}
	
	public static void logToConsole(String msg) {
		if (Utils.debug) {
			System.out.println(msg);
		}
	}

	public static List<Cell> fetchTargetCells(Cell cell, Context context, String cellName) {
		while (!context.isCellPocessed(cellName)) {
			context.getReportBuilder().buildCell(context, null);
		}
		List<Cell> leftCells = fetchCellsByLeftParent(context, cell, cellName);
		List<Cell> topCells = fetchCellsByTopParent(context, cell, cellName);
		if (leftCells != null && topCells != null) {
			int leftSize = leftCells.size(), topSize = topCells.size();
			if (leftSize == 1 || topSize == 0) {
				return leftCells;
			}
			if (topSize == 1 || leftSize == 0) {
				return topCells;
			}
			if (leftSize == 0 && topSize == 0) {
				return new ArrayList<Cell>();
			}
			List<Cell> list = new ArrayList<Cell>();
			if (leftSize <= topSize) {
				for (Cell c : leftCells) {
					if (topCells.contains(c)) {
						list.add(c);
					}
				}
			} else {
				for (Cell c : topCells) {
					if (leftCells.contains(c)) {
						list.add(c);
					}
				}
			}
			return list;
		} else if (leftCells != null && topCells == null) {
			return leftCells;
		} else if (leftCells == null && topCells != null) {
			return topCells;
		} else {
			Report report = context.getReport();
			return report.getCellsMap().get(cellName);
		}
	}

	private static List<Cell> fetchCellsByLeftParent(Context context, Cell cell, String cellName) {
		Cell leftParentCell = cell.getLeftParentCell();
		if (leftParentCell == null) {
			return null;
		}
		if (leftParentCell.getName().equals(cellName)) {
			List<Cell> list = new ArrayList<Cell>();
			list.add(leftParentCell);
			return list;
		}
//		Map<String, List<Cell>> childrenCellsMap = leftParentCell.getRowChildrenCellsMap();
//		List<Cell> targetCells = childrenCellsMap.get(cellName);
//		if (targetCells != null) {
//			return targetCells;
//		}
		List<String> cellNames = leftParentCell.getNewCellNames();
		if(leftParentCell.hasRowChildByCellName(cellName) || cellNames.contains(cellName)) {
			List<Cell> targetCells = new ArrayList<Cell>();
			leftParentCellFindChildren(targetCells, leftParentCell, cellName);
			return targetCells;
		}
		return fetchCellsByLeftParent(context, leftParentCell, cellName);
	}
	
	/**
	 * 查找当前单元格所有匹配单元格名称的下级单元格
	 * @param children
	 * @param cell
	 * @param cellName
	 */
	private static void leftParentCellFindChildren(List<Cell> children,Cell cell, String cellName) {
		if(cell != null) {
			Map<String, List<Cell>> childrenCellsMap = cell.getRowChildrenCellsMap();
			if(childrenCellsMap != null) {
				List<Cell> targetCells = childrenCellsMap.get(cellName);
				if (targetCells != null) {
					children.addAll(targetCells);
				} else {
					for (Map.Entry<String, List<Cell>> entry : childrenCellsMap.entrySet()) {
						List<Cell> cells = entry.getValue();
						for (Cell child : cells) {
							leftParentCellFindChildren(children, child, cellName);
						}
					}
				}
			}
		}
	}
	

	private static List<Cell> fetchCellsByTopParent(Context context, Cell cell, String cellName) {
		Cell topParentCell = cell.getTopParentCell();
		if (topParentCell == null) {
			return null;
		}
		if (topParentCell.getName().equals(cellName)) {
			List<Cell> list = new ArrayList<Cell>();
			list.add(topParentCell);
			return list;
		}
//		Map<String, List<Cell>> childrenCellsMap = topParentCell.getColumnChildrenCellsMap();
//		List<Cell> targetCells = childrenCellsMap.get(cellName);
//		if (targetCells != null) {
//			return targetCells;
//		}
		List<String> cellNames = topParentCell.getNewCellNames();
		if(topParentCell.hasColumnChildByCellName(cellName)  || cellNames.contains(cellName)) {
			List<Cell> targetCells = new ArrayList<Cell>();
			topParentCellFindChildren(targetCells, topParentCell, cellName);
			return targetCells;
		}
		return fetchCellsByTopParent(context, topParentCell, cellName);
	}
	
	private static void topParentCellFindChildren(List<Cell> children,Cell cell, String cellName) {
		if(cell != null) {
			Map<String, List<Cell>> childrenCellsMap = cell.getColumnChildrenCellsMap();
			if(childrenCellsMap != null) {
				List<Cell> targetCells = childrenCellsMap.get(cellName);
				if (targetCells != null) {
					children.addAll(targetCells);
				} else {
					for (Map.Entry<String, List<Cell>> entry : childrenCellsMap.entrySet()) {
						List<Cell> cells = entry.getValue();
						for (Cell child : cells) {
							topParentCellFindChildren(children, child, cellName);
						}
					}
				}
			}
		}
	}

	public static Object getProperty(Object obj, String property) {
		if (obj == null)
			return null;
		try {
			if (obj instanceof Map && property.indexOf(".") == -1) {
				Map<?, ?> map = (Map<?, ?>) obj;
				return map.get(property);
			}
			return PropertyUtils.getProperty(obj, property);
		} catch (Exception ex) {
			throw new ReportComputeException(ex);
		}
	}

	public static Date toDate(Object obj) {
		if (obj instanceof Date) {
			return (Date) obj;
		} else if (obj instanceof String) {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return sd.parse(obj.toString());
			} catch (Exception ex) {
				sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					return sd.parse(obj.toString());
				} catch (Exception e) {
					throw new ReportComputeException("Can not convert " + obj + " to Date.");
				}
			}
		}
		throw new ReportComputeException("Can not convert " + obj + " to Date.");
	}

	public static BigDecimal toBigDecimal(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof BigDecimal) {
			return (BigDecimal) obj;
		} else {
			String val = String.valueOf(obj).trim();
			if (StringUtils.isBlank(val)) {
				return new BigDecimal(0);
			}
			try {
				return new BigDecimal(val);
			} catch (Exception ex) {
				throw new ConvertException("Can not convert " + obj + " to BigDecimal.");
			}
		}
	}
	
	public static Integer toInteger(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Integer) {
			return (Integer) obj;
		} else if (obj instanceof String) {
			if (obj.toString().trim().equals("")) {
				return 0;
			}
			try {
				String str = obj.toString().trim();
				return new Integer(str);
			} catch (Exception ex) {
				throw new ConvertException("Can not convert " + obj + " to Integer.");
			}
		}
		throw new ConvertException("Can not convert " + obj + " to Integer.");
	}

	public void setDebug(boolean debug) {
		Utils.debug = debug;
	}
}
