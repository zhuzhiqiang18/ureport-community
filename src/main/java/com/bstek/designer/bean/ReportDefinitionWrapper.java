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
package com.bstek.designer.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bstek.ureport.definition.CellDefinition;
import com.bstek.ureport.definition.ColumnDefinition;
import com.bstek.ureport.definition.FreezeCellDefinition;
import com.bstek.ureport.definition.HeaderFooterDefinition;
import com.bstek.ureport.definition.Paper;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.definition.RowDefinition;
import com.bstek.ureport.definition.datasource.DatasourceDefinition;
import com.bstek.ureport.definition.searchform.SearchForm;

/**
 * @author Jacky.gao
 * @since 2017年1月29日
 */
public class ReportDefinitionWrapper {

	private String reportFullName;

	private Paper paper;

	private HeaderFooterDefinition header;

	private HeaderFooterDefinition footer;

	private SearchForm searchForm;

	private List<RowDefinition> rows;

	private List<ColumnDefinition> columns;
	
	private FreezeCellDefinition freeze;

	private List<DatasourceDefinition> datasources;

	private Map<String, CellDefinition> cellsMap = new HashMap<String, CellDefinition>();

	public ReportDefinitionWrapper(ReportDefinition report) {
		this.reportFullName = report.getReportFullName();
		this.paper = report.getPaper();
		this.header = report.getHeader();
		this.footer = report.getFooter();
		this.searchForm = report.getSearchForm();
		this.rows = report.getRows();
		this.columns = report.getColumns();
		this.freeze = report.getFreeze();
		this.datasources = report.getDatasources();
		for (CellDefinition cell : report.getCells()) {
			cellsMap.put(cell.getRowNumber() + "," + cell.getColumnNumber(), cell);
		}
	}

	public String getReportFullName() {
		return reportFullName;
	}

	public List<ColumnDefinition> getColumns() {
		return columns;
	}

	public List<DatasourceDefinition> getDatasources() {
		return datasources;
	}

	public HeaderFooterDefinition getFooter() {
		return footer;
	}

	public HeaderFooterDefinition getHeader() {
		return header;
	}

	public Paper getPaper() {
		return paper;
	}

	public SearchForm getSearchForm() {
		return searchForm;
	}

	public void setSearchForm(SearchForm searchForm) {
		this.searchForm = searchForm;
	}

	public Map<String, CellDefinition> getCellsMap() {
		return cellsMap;
	}

	public List<RowDefinition> getRows() {
		return rows;
	}

	public FreezeCellDefinition getFreeze() {
		return freeze;
	}

	public void setFreeze(FreezeCellDefinition freeze) {
		this.freeze = freeze;
	}
	
	
}
