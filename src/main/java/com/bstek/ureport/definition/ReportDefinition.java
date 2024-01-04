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
package com.bstek.ureport.definition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bstek.common.utils.StringUtils;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.definition.dataset.DatasetDefinition;
import com.bstek.ureport.definition.dataset.SqlDatasetDefinition;
import com.bstek.ureport.definition.datasource.ApiDatasourceDefinition;
import com.bstek.ureport.definition.datasource.BuildinDatasourceDefinition;
import com.bstek.ureport.definition.datasource.DatasourceDefinition;
import com.bstek.ureport.definition.datasource.JdbcDatasourceDefinition;
import com.bstek.ureport.definition.searchform.Component;
import com.bstek.ureport.definition.searchform.MultipleSelectInputComponent;
import com.bstek.ureport.definition.searchform.RenderContext;
import com.bstek.ureport.definition.searchform.SearchForm;
import com.bstek.ureport.definition.searchform.SelectInputComponent;
import com.bstek.ureport.export.html.SearchFormData;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.model.Column;
import com.bstek.ureport.model.Report;
import com.bstek.ureport.model.Row;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Jacky.gao
 * @since 2016年11月1日
 */
public class ReportDefinition implements Serializable {

	private static final long serialVersionUID = 5934291400824773809L;

	private String reportFullName;

	private Paper paper;

	private CellDefinition rootCell;

	private HeaderFooterDefinition header;

	private HeaderFooterDefinition footer;

	private SearchForm searchForm;

	private List<CellDefinition> cells;

	private List<RowDefinition> rows;

	private List<ColumnDefinition> columns;
	
	private FreezeCellDefinition freeze;

	private List<DatasourceDefinition> datasources;

	@JsonIgnore
	private String style;

	public Report newReport() {
		Report report = new Report();
		report.setReportFullName(reportFullName);
		report.setPaper(paper);
		report.setHeader(header);
		report.setFooter(footer);
		List<Row> reportRows = new ArrayList<Row>();
		List<Column> reportColumns = new ArrayList<Column>();
		report.setRows(reportRows);
		report.setColumns(reportColumns);

		Map<Integer, Row> rowMap = new HashMap<Integer, Row>();
		int headerRowsHeight = 0, footerRowsHeight = 0, titleRowsHeight = 0, summaryRowsHeight = 0;
		for (RowDefinition rowDef : rows) {
			Row newRow = rowDef.newRow(reportRows);
			report.insertRow(newRow, rowDef.getRowNumber());
			rowMap.put(rowDef.getRowNumber(), newRow);
			Band band = rowDef.getBand();
			if (band != null) {
				if (band.equals(Band.headerrepeat)) {
					report.getHeaderRepeatRows().add(newRow);
					headerRowsHeight += newRow.getRealHeight();
				} else if (band.equals(Band.footerrepeat)) {
					report.getFooterRepeatRows().add(newRow);
					footerRowsHeight += newRow.getRealHeight();
				} else if (band.equals(Band.title)) {
					report.getTitleRows().add(newRow);
					titleRowsHeight += newRow.getRealHeight();
				} else if (band.equals(Band.summary)) {
					report.getSummaryRows().add(newRow);
					summaryRowsHeight += newRow.getRealHeight();
				}
			}
		}
		report.setRepeatHeaderRowHeight(headerRowsHeight);
		report.setRepeatFooterRowHeight(footerRowsHeight);
		report.setTitleRowsHeight(titleRowsHeight);
		report.setSummaryRowsHeight(summaryRowsHeight);
		Map<Integer, Column> columnMap = new HashMap<Integer, Column>();
		for (ColumnDefinition columnDef : columns) {
			Column newColumn = columnDef.newColumn(reportColumns);
			report.insertColumn(newColumn, columnDef.getColumnNumber());
			columnMap.put(columnDef.getColumnNumber(), newColumn);
		}
		Map<CellDefinition, Cell> cellMap = new HashMap<CellDefinition, Cell>();
		for (CellDefinition cellDef : cells) {
			Cell cell = cellDef.newCell();
			cellMap.put(cellDef, cell);
			Row targetRow = rowMap.get(cellDef.getRowNumber());
			cell.setRow(targetRow);
			targetRow.getCells().add(cell);
			Column targetColumn = columnMap.get(cellDef.getColumnNumber());
			cell.setColumn(targetColumn);
			targetColumn.getCells().add(cell);

			if (cellDef.getLeftParentCell() == null && cellDef.getTopParentCell() == null) {
				report.setRootCell(cell);
			}
			report.addCell(cell);
		}
		for (CellDefinition cellDef : cells) {
			Cell targetCell = cellMap.get(cellDef);
			CellDefinition leftParentCellDef = cellDef.getLeftParentCell();
			if (leftParentCellDef != null) {
				targetCell.setLeftParentCell(cellMap.get(leftParentCellDef));
			} else {
				targetCell.setLeftParentCell(null);
			}
			CellDefinition topParentCellDef = cellDef.getTopParentCell();
			if (topParentCellDef != null) {
				targetCell.setTopParentCell(cellMap.get(topParentCellDef));
			} else {
				targetCell.setTopParentCell(null);
			}
		}
		for (CellDefinition cellDef : cells) {
			Cell targetCell = cellMap.get(cellDef);
			List<CellDefinition> rowChildrenCellDefinitions = cellDef.getRowChildrenCells();
			for (CellDefinition childCellDef : rowChildrenCellDefinitions) {
				Cell childCell = cellMap.get(childCellDef);
				if(targetCell == childCell.getLeftParentCell()) {
					targetCell.addRowChild(childCell);
				}
				targetCell.addRowChildName(childCell.getName());
			}
			List<CellDefinition> columnChildrenCellDefinitions = cellDef.getColumnChildrenCells();
			for (CellDefinition childCellDef : columnChildrenCellDefinitions) {
				Cell childCell = cellMap.get(childCellDef);
				if(targetCell == childCell.getTopParentCell()) {
					targetCell.addColumnChild(childCell);
				}
				targetCell.addColumnChildName(childCell.getName());
			}
		}
		return report;
	}

	public String getStyle() {
		if (style == null) {
			style = buildStyle();
		}
		return style;
	}
	
	public void setToken(String token) {
		if(datasources != null && datasources.size() > 0) {
			for (DatasourceDefinition dsDef : datasources) {
				if (dsDef instanceof ApiDatasourceDefinition) {
					ApiDatasourceDefinition ds = (ApiDatasourceDefinition) dsDef;
					ds.setToken(token);
				}
			}
		}
	}

	private String buildStyle() {
		StringBuffer sb = new StringBuffer();
		for (CellDefinition cell : cells) {
			CellStyle cellStyle = cell.getCellStyle();
			sb.append("._" + cell.getName() + "{");
			int colWidth = getColumnWidth(cell.getColumnNumber(), cell.getColSpan());
			sb.append("width:" + colWidth + "pt;");
			Alignment align = cellStyle.getAlign();
			if (align != null) {
				sb.append("text-align:" + align.name() + ";");
			}
			Alignment valign = cellStyle.getValign();
			if (valign != null) {
				sb.append("vertical-align:" + valign.name() + ";");
			}
			float lineHeight = cellStyle.getLineHeight();
			if (lineHeight > 0) {
				sb.append("line-height:" + lineHeight + ";");
			}
			String bgcolor = cellStyle.getBgcolor();
			if (StringUtils.isNotBlank(bgcolor)) {
				sb.append("background-color:rgb(" + bgcolor + ");");
			}
			String fontFamilty = cellStyle.getFontFamily();
			if (StringUtils.isNotBlank(fontFamilty)) {
				sb.append("font-family:" + fontFamilty + ";");
			}
			int fontSize = cellStyle.getFontSize();
			sb.append("font-size:" + fontSize + "pt;");
			String foreColor = cellStyle.getForecolor();
			if (StringUtils.isNotBlank(foreColor)) {
				sb.append("color:rgb(" + foreColor + ");");
			}
			Boolean bold = cellStyle.getBold(), italic = cellStyle.getItalic(), underline = cellStyle.getUnderline();
			if (bold != null && bold) {
				sb.append("font-weight:bold;");
			}
			if (italic != null && italic) {
				sb.append("font-style:italic;");
			}
			if (underline != null && underline) {
				sb.append("text-decoration:underline;");
			}
			Border border = cellStyle.getLeftBorder();
			if (border != null) {
				sb.append("border-left:" + border.getStyle().name() + " " + border.getWidth() + "px rgb("
						+ border.getColor() + ");");
			}
			border = cellStyle.getRightBorder();
			if (border != null) {
				sb.append("border-right:" + border.getStyle().name() + " " + border.getWidth() + "px rgb("
						+ border.getColor() + ");");
			}
			border = cellStyle.getTopBorder();
			if (border != null) {
				sb.append("border-top:" + border.getStyle().name() + " " + border.getWidth() + "px rgb("
						+ border.getColor() + ");");
			}
			border = cellStyle.getBottomBorder();
			if (border != null) {
				sb.append("border-bottom:" + border.getStyle().name() + " " + border.getWidth() + "px rgb("
						+ border.getColor() + ");");
			}
			sb.append("}");
		}
		return sb.toString();
	}

	/**
	 * 表单联动刷新
	 * 
	 * @param item
	 * @param parameters 参数
	 * @return
	 */
	public Map<String, Object> refreshSearchForm(Map<String, Object> item, Map<String, Object> params) {
		// 需要查询数据的组件
		Map<String, Component> datasetComponent = new HashMap<String, Component>();
		List<Component> items = searchForm.getComponents();
		String currentUuid = StringUtils.toString(item.get("uuid"));
		if (items != null && items.size() > 0) {
			for (Component component : items) {
				String dataset = getComponentDataSetName(component);
				String uuid = component.getId();
				if(StringUtils.isNotBlank(dataset) && !currentUuid.equals(uuid)) {
					datasetComponent.put(dataset, component);
				}
			}
		}
		// 获取相关联所有数据集
		List<Dataset> dataList = new ArrayList<Dataset>();
		if (datasources != null && datasources.size() > 0) {
			String currenBindParameter = StringUtils.toString(item.get("bindParameter"));
			for (DatasourceDefinition datasourceDefinition : datasources) {
				List<DatasetDefinition> datasets = datasourceDefinition.getDatasets();
				if (datasets != null && datasets.size() > 0) {
					List<DatasetDefinition> newDatasetDefinition = new ArrayList<DatasetDefinition>();
					if(datasourceDefinition instanceof JdbcDatasourceDefinition) {
						JdbcDatasourceDefinition datasource = (JdbcDatasourceDefinition) datasourceDefinition;
						for (DatasetDefinition datasetDefinition : datasets) {
							SqlDatasetDefinition sqlDatasetDefinition = (SqlDatasetDefinition) datasetDefinition;
							if(datasetComponent.get(datasetDefinition.getName()) != null && sqlDatasetDefinition.hasParameter(currenBindParameter) ) {
								newDatasetDefinition.add(datasetDefinition);
							}
						}
						// 重新设置需要查询的数据集
						datasource.setDatasets(newDatasetDefinition);
						dataList.addAll(datasource.buildDatasets(params));
					} else if(datasourceDefinition instanceof BuildinDatasourceDefinition) {
						BuildinDatasourceDefinition datasource = (BuildinDatasourceDefinition) datasourceDefinition;
						for (DatasetDefinition datasetDefinition : datasets) {
							SqlDatasetDefinition sqlDatasetDefinition = (SqlDatasetDefinition) datasetDefinition;
							if(datasetComponent.get(datasetDefinition.getName()) != null && sqlDatasetDefinition.hasParameter(currenBindParameter)) {
								newDatasetDefinition.add(datasetDefinition);
							}
						}
						// 重新设置需要查询的数据集
						datasource.setDatasets(newDatasetDefinition);
						dataList.addAll(datasource.buildDatasets(params));
					}
				}
			}	
		}
		// 刷新相关联表单数据
		Map<String, Object> result = new HashMap<String, Object>();
		for (Dataset dataset : dataList) {
			String name = dataset.getName();
			Component component = datasetComponent.get(name);
			if(component != null) {
				String uuid = component.getId();
				result.put(uuid,searchForm.refersh(component, dataset));
			}
		}
		return result;
	}
	
	/**
	 * 获取组件数据集名称
	 * @param component 组件信息
	 * @return 数据集名称
	 */
	private String getComponentDataSetName(Component component) {
		if(component instanceof SelectInputComponent) {
			SelectInputComponent selectInputComponent = (SelectInputComponent) component;
			return selectInputComponent.getDataset();
			
		} else if(component instanceof MultipleSelectInputComponent) {
			MultipleSelectInputComponent multipleSelectInputComponent = (MultipleSelectInputComponent) component;
			return multipleSelectInputComponent.getDataset();
		}
		return null;
	}

	public SearchFormData buildSearchFormData(Map<String, Dataset> datasetMap, Map<String, Object> parameters) {
		if (searchForm == null) {
			return null;
		}
		Map<String, Object> defaultParameters = getDefaultParameters(parameters);
		if(parameters != null) {
			defaultParameters.putAll(parameters);
		}
		RenderContext context = new RenderContext(datasetMap, defaultParameters);
		SearchFormData data = searchForm.builder(context);
		return data;
	}
	
	private Map<String, Object> getDefaultParameters(Map<String, Object> parameters) {
		Map<String, Object> defaultParameters = new HashMap<String, Object>();
		if (datasources != null && datasources.size() > 0) {
			for (DatasourceDefinition datasourceDefinition : datasources) {
				List<DatasetDefinition> datasets = datasourceDefinition.getDatasets();
				if (datasets != null && datasets.size() > 0) {
					for (DatasetDefinition datasetDefinition : datasets) {
						if (datasetDefinition instanceof SqlDatasetDefinition) {
							SqlDatasetDefinition sqlDatasetDefinition = (SqlDatasetDefinition) datasetDefinition;
							Map<String, Object> param = sqlDatasetDefinition.buildParameters(parameters);
							defaultParameters.putAll(param);
						}
					}
				}
			}
		}
		return defaultParameters;
	}

	private int getColumnWidth(int columnNumber, int colSpan) {
		int width = 0;
		if (colSpan > 0)
			colSpan--;
		int start = columnNumber, end = start + colSpan;
		for (int i = start; i <= end; i++) {
			for (ColumnDefinition col : columns) {
				if (col.getColumnNumber() == i) {
					width += col.getWidth();
				}
			}
		}
		return width;
	}

	public String getReportFullName() {
		return reportFullName;
	}

	public void setReportFullName(String reportFullName) {
		this.reportFullName = reportFullName;
	}

	public Paper getPaper() {
		return paper;
	}

	public void setPaper(Paper paper) {
		this.paper = paper;
	}

	public CellDefinition getRootCell() {
		return rootCell;
	}

	public void setRootCell(CellDefinition rootCell) {
		this.rootCell = rootCell;
	}

	public HeaderFooterDefinition getHeader() {
		return header;
	}

	public void setHeader(HeaderFooterDefinition header) {
		this.header = header;
	}

	public HeaderFooterDefinition getFooter() {
		return footer;
	}

	public void setFooter(HeaderFooterDefinition footer) {
		this.footer = footer;
	}

	public SearchForm getSearchForm() {
		return searchForm;
	}

	public void setSearchForm(SearchForm searchForm) {
		this.searchForm = searchForm;
	}

	public List<RowDefinition> getRows() {
		return rows;
	}

	public void setRows(List<RowDefinition> rows) {
		this.rows = rows;
	}

	public List<ColumnDefinition> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnDefinition> columns) {
		this.columns = columns;
	}

	public List<CellDefinition> getCells() {
		return cells;
	}

	public void setCells(List<CellDefinition> cells) {
		this.cells = cells;
	}

	public void setDatasources(List<DatasourceDefinition> datasources) {
		this.datasources = datasources;
	}

	public List<DatasourceDefinition> getDatasources() {
		return datasources;
	}

	public FreezeCellDefinition getFreeze() {
		return freeze;
	}

	public void setFreeze(FreezeCellDefinition freeze) {
		this.freeze = freeze;
	}
}
