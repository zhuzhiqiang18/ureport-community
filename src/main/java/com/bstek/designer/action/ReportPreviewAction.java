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
package com.bstek.designer.action;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bstek.common.exception.ReportDesignException;
import com.bstek.common.utils.StringUtils;
import com.bstek.designer.bean.PreviewPageParameters;
import com.bstek.designer.bean.PreviewParameters;
import com.bstek.designer.bean.PreviewSearchForm;
import com.bstek.shiro.JWTUtil;
import com.bstek.shiro.TokenFilter;
import com.bstek.ureport.Utils;
import com.bstek.ureport.build.paging.Page;
import com.bstek.ureport.definition.Paper;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.exception.ReportException;
import com.bstek.ureport.export.ExportUtils;
import com.bstek.ureport.export.PageBuilder;
import com.bstek.ureport.export.ProducerEnum;
import com.bstek.ureport.export.ReportRender;
import com.bstek.ureport.export.SinglePageData;
import com.bstek.ureport.export.html.HtmlProducer;
import com.bstek.ureport.export.html.HtmlReport;
import com.bstek.ureport.export.html.SearchFormData;
import com.bstek.ureport.model.Report;
import com.bstek.ureport.provider.ProviderFactory;
import com.bstek.ureport.provider.report.ReportProvider;
import com.github.benmanes.caffeine.cache.Cache;

/**
 * @author Jacky.gao
 * @since 2017年2月15日
 */
@RestController
@RequestMapping("/ureport")
public class ReportPreviewAction {

	@Resource
	private Cache<String, Report> caffeineCache;
	
	private ReportRender reportRender = new ReportRender();

	private HtmlProducer htmlProducer = new HtmlProducer();

	@RequestMapping("/loadData")
	@ResponseBody
	public HtmlReport loadData(HttpServletRequest request, @RequestBody PreviewPageParameters pageParameters) {
		PreviewParameters params = pageParameters.getParams();
		ReportDefinition reportDefinition = getReportDefinition(request, params);
		Map<String, Object> parameters = params.getQuery();
		HtmlReport htmlReport = new HtmlReport();
		Paper paper = reportDefinition.getPaper();
		Report report = null;
		String html = "";
		if (paper.isPageEnabled()) { // 分页
			String reportName = params.getReportName();
			// 通过读取文件分页，需要判断文件是否被修改，防止加载修改前的缓存数据
			if (StringUtils.isNotBlank(reportName)) {
				ReportProvider reportProvider = ProviderFactory.getFileReportProvider();
				try (InputStream in = reportProvider.loadReport("file:" + reportName)){
					String uuid = DigestUtils.md5Hex(in);
					params.setSign(uuid);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String key = DigestUtils.md5Hex(params.toString());
			report = caffeineCache.getIfPresent(key);
			if (report == null) {
				report = reportRender.render(reportDefinition, parameters);
				//caffeineCache.put(key, report);
			}
			int pageIndex = pageParameters.getPageIndex();
			SinglePageData pageData = PageBuilder.buildSinglePageData(pageIndex, report);
			List<Page> pages = pageData.getPages();
			html = htmlProducer.produce(report.getContext(), pages, pageData.getColumnMargin(), false);
			htmlReport.setTotalPage(pageData.getTotalPages());
			htmlReport.setPageIndex(pageIndex);
			if (reportDefinition.getPaper().isColumnEnabled()) {
				htmlReport.setColumn(reportDefinition.getPaper().getColumnCount());
			}
		} else {
			report = reportRender.render(reportDefinition, parameters);
			html = htmlProducer.produce(report);
		}
		htmlReport.setChartDatas(report.getContext().getChartDataMap().values());
		htmlReport.setContent(html);
		htmlReport.setStyle(reportDefinition.getStyle());
		SearchFormData searchFormData = reportDefinition.buildSearchFormData(report.getContext().getDatasetMap(),parameters);
		htmlReport.setSearchFormData(searchFormData);
		htmlReport.setReportAlign(report.getPaper().getHtmlReportAlign().name());
		htmlReport.setBgImage(report.getPaper().getBgImage());
		htmlReport.setFreeze(reportDefinition.getFreeze());
		return htmlReport;
	}
	
	@RequestMapping("/refreshForm")
	public Map<String, Object> previewRefreshForm(HttpServletRequest request, @RequestBody PreviewSearchForm form) {
		ReportDefinition reportDefinition = getReportDefinition(request, form);
		return reportDefinition.refreshSearchForm(form.getItem(), form.getQuery());
	}
	
	@RequestMapping("/print")
	public void buildPrint(HttpServletRequest request, @RequestBody PreviewParameters reportParameters,HttpServletResponse response) {
		long start = System.currentTimeMillis();
		ReportDefinition reportDefinition = getReportDefinition(request, reportParameters);
		Report report = reportRender.render(reportDefinition, reportParameters.getQuery());
		Map<String, String> chartImages = reportParameters.getChartImages();
		report.setChartImages(chartImages);
		try {
			OutputStream out = response.getOutputStream();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/pdf; charset=UTF-8");
			// 创建下载文件
			ProducerEnum p = ProducerEnum.PDF;
			String downFileName = StringUtils.randomFileName() + StringUtils.getFileSuffix(p);
			response.setHeader("Content-Disposition","attachment; filename=" + URLEncoder.encode(downFileName, "UTF-8"));
			response.setHeader("code", "20000");
			ExportUtils.export(out, report, p);
			long end = System.currentTimeMillis();
			Utils.logToConsole("获取打印文件耗时:" + (end - start) + "ms");
		} catch (Exception ex) {
			throw new ReportException(ex);
		}
	}

	@RequestMapping("/download/{type}")
	public void download(HttpServletRequest request, @PathVariable String type, @RequestBody PreviewParameters reportParameters,HttpServletResponse response) {
		ReportDefinition reportDefinition = getReportDefinition(request, reportParameters);
		Report report = reportRender.render(reportDefinition, reportParameters.getQuery());
		Map<String, String> chartImages = reportParameters.getChartImages();
		report.setChartImages(chartImages);
		try {
			OutputStream out = response.getOutputStream();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/octet-stream; charset=UTF-8");
			// 创建下载文件
			ProducerEnum p = ProducerEnum.valueOf(type.toUpperCase());
			String downFileName = StringUtils.randomFileName() + StringUtils.getFileSuffix(p);
			response.setHeader("Content-Disposition","attachment; filename=" + URLEncoder.encode(downFileName, "UTF-8"));
			response.setHeader("code", "20000");
			long start = System.currentTimeMillis();
			ExportUtils.export(out, report, p);
			long end = System.currentTimeMillis();
			Utils.logToConsole("导出 " + type + " 报表耗时:" + (end - start) + "ms");
		} catch (Exception ex) {
			throw new ReportException(ex);
		}
	}
	
	@RequestMapping("/pdf/{reportName}")
	public void buildPdf(HttpServletRequest request, HttpServletResponse response, @PathVariable String reportName) {
		PreviewParameters reportParameters = buildPreviewParameters(request);
		reportParameters.setReportName(reportName);
		download(request, "PDF", reportParameters,response);
	}
	
	@RequestMapping("/word/{reportName}")
	public void buildWord(HttpServletRequest request, HttpServletResponse response, @PathVariable String reportName) {
		PreviewParameters reportParameters = buildPreviewParameters(request);
		reportParameters.setReportName(reportName);
		download(request, "WORD", reportParameters,response);
	}
	
	@RequestMapping("/excel/{reportName}")
	public void buildExcel(HttpServletRequest request, HttpServletResponse response, @PathVariable String reportName) {
		PreviewParameters reportParameters = buildPreviewParameters(request);
		reportParameters.setReportName(reportName);
		download(request, "EXCEL", reportParameters,response);
	}
	
	private ReportDefinition getReportDefinition(HttpServletRequest request,PreviewParameters params) {
		ReportDefinition reportDefinition = null;
		String reportName = params.getReportName();
		String content = params.getContent();
		if (StringUtils.isNotBlank(reportName)) {
			reportDefinition = reportRender.getReportDefinition("file:" + reportName);
		} else if (StringUtils.isNotBlank(content)) {
			content = StringUtils.decode(content);
			System.out.println(content);
			reportDefinition = reportRender.getReportDefinition(content, "utf-8");
		} else {
			throw new ReportComputeException("Report file can not be null.");
		}
		if (reportDefinition == null) {
			throw new ReportDesignException("Report data has expired,can not do preview.");
		}
		Map<String, Object> parameters = params.getQuery();
		if(parameters == null) {
			parameters = new HashMap<String, Object>();
			params.setQuery(parameters);
		}
		Map<String, String> component = params.getComponent();
		if(component == null) {
			component = new HashMap<String, String>();
			params.setComponent(component);
		}
		// 相同的低优先级的参数会被高优先级参数覆盖
		// 参数优先级， token参数 > 页面表单参数 > url链接参数 > 数据集默认参数
		String token = request.getHeader(TokenFilter.X_TOKEN);
		if(org.apache.commons.lang3.StringUtils.isBlank(token)) {
			Cookie[] cookies = request.getCookies();
    		if(cookies != null && cookies.length > 0) {
        		for (Cookie cookie : cookies) {
            		String name = cookie.getName();
            		if(TokenFilter.X_TOKEN.equals(name)) {
            			token = cookie.getValue();
            			break;
            		}
    			}
        	}
		}
		parameters.putAll(JWTUtil.getParams(token));
		reportDefinition.setToken(token);
		return reportDefinition;
	}
	
	private PreviewParameters buildPreviewParameters(HttpServletRequest req){
		PreviewParameters previewParameters = new PreviewParameters();
		Map<String, Object> parameters = previewParameters.getQuery();
		if(parameters == null) {
			parameters = new HashMap<String, Object>();
			previewParameters.setQuery(parameters);
		}
		Enumeration<?> enumeration=req.getParameterNames();
		while(enumeration.hasMoreElements()){
			Object obj = enumeration.nextElement();
			if(obj == null){
				continue;
			}
			String name = String.valueOf(obj);
			parameters.put(name, req.getParameter(name));
		}
		return previewParameters;
	}
}
