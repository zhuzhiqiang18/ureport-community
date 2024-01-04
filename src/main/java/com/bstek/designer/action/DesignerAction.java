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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bstek.common.exception.ParamErrorException;
import com.bstek.common.exception.ReportDesignException;
import com.bstek.designer.bean.ReportDefinitionWrapper;
import com.bstek.designer.excel.ExcelParserUtils;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.dsl.ReportParserLexer;
import com.bstek.ureport.dsl.ReportParserParser;
import com.bstek.ureport.dsl.ReportParserParser.DatasetContext;
import com.bstek.ureport.exception.ReportException;
import com.bstek.ureport.export.ReportRender;
import com.bstek.ureport.expression.ErrorInfo;
import com.bstek.ureport.expression.ScriptErrorListener;
import com.bstek.ureport.provider.ProviderFactory;
import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import com.bstek.ureport.utils.StringUtils;

/**
 * @author Jacky.gao
 * @since 2017年1月25日
 */
@RestController
@RequestMapping("/designer")
public class DesignerAction {

	private final String fileStoreDir = MessageFormat.format("{0}/resource/images", System.getProperty("user.dir"));
	
	private ReportRender reportRender = new ReportRender();

	/**
	 * 表达式校验
	 * @param content 表达式内容
	 * @return
	 */
	@RequestMapping("/scriptValidation")
	public List<ErrorInfo> scriptValidation(String content) {
		content = StringUtils.decode(content);
		ANTLRInputStream antlrInputStream = new ANTLRInputStream(content);
		ReportParserLexer lexer = new ReportParserLexer(antlrInputStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		ReportParserParser parser = new ReportParserParser(tokenStream);
		ScriptErrorListener errorListener = new ScriptErrorListener();
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		parser.expression();
		List<ErrorInfo> infos = errorListener.getInfos();
		return infos;
	}

	/**
	 * 解析表达式中的数据集
	 * @param expr
	 * @return
	 */
	@RequestMapping("/parseDataSet")
	public Map<String, String> parseDatasetName(String expr) {
		ANTLRInputStream antlrInputStream = new ANTLRInputStream(expr);
		ReportParserLexer lexer = new ReportParserLexer(antlrInputStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		ReportParserParser parser = new ReportParserParser(tokenStream);
		parser.removeErrorListeners();
		
		Map<String, String> result = new HashMap<String, String>();
		DatasetContext ctx = parser.dataset();
		if (ctx != null) {
			TerminalNode node = ctx.Identifier();
			if(node != null) {
				String datasetName = ctx.Identifier().getText();
				result.put("datasetName", datasetName);
			}
		}
		return result;
	}

	/**
	 * 加载报表内容
	 * @param request
	 * @return
	 */
	@RequestMapping("/get")
	public ReportDefinitionWrapper loadReport(HttpServletRequest request)  {
		String file = request.getParameter("file");
		if (file == null) {
			throw new ReportDesignException("Report file can not be null.");
		}
		ReportDefinition reportDef = reportRender.parseReport(file);
		return new ReportDefinitionWrapper(reportDef);
	}

	/**
	 * 删除报表模板
	 * @param request
	 * @return
	 */
	@RequestMapping("/remove")
	public int deleteReportFile(HttpServletRequest request) {
		String file = request.getParameter("file");
		if (file == null) {
			throw new ReportDesignException("Report file can not be null.");
		}
		ReportProvider targetReportProvider = ProviderFactory.getReportProvider(file);
		if (targetReportProvider == null) {
			throw new ReportDesignException("File [" + file + "] not found available report provider.");
		}
		targetReportProvider.deleteReport(file);
		return 1;
	}

	/**
	 * 保存报表模板
	 * @param reportFile
	 * @return
	 */
	@RequestMapping("/save")
	public int saveReportFile(@RequestBody ReportFile reportFile) {
		String fileName = reportFile.getName();
		String content = reportFile.getContent();
		content = StringUtils.decode(content);
		ReportDefinition reportDef = reportRender.getReportDefinition(content, "utf-8");
		if(reportDef != null) {
			ReportProvider targetReportProvider = ProviderFactory.getReportProvider(fileName);
			if (targetReportProvider == null) {
				throw new ReportDesignException("File [" + fileName + "] not found available report provider.");
			}
			String oldName = reportFile.getOldName();
			if (oldName.startsWith("classpath:") || !fileName.equals(oldName)) {
				List<ReportFile> files = targetReportProvider.getReportFiles();
				for (ReportFile f : files) {
					boolean isSameName = f.getName().equals(fileName.replaceFirst(targetReportProvider.getPrefix(), ""));
					if(isSameName) {
						throw new ParamErrorException("名称已存在");
					}
				}
			}
			targetReportProvider.saveReport(fileName, content);
		}
		return 1;
	}

	/**
	 * 查看所有报表模板
	 * @return
	 */
	@RequestMapping("/getList")
	public List<ReportFile> loadReportProviders() {
		ReportProvider reportProvider = ProviderFactory.getFileReportProvider();
		return reportProvider.getReportFiles();
	}
	
	/**
	 * 导入excel模板
	 * @param file
	 * @return
	 */
	@RequestMapping("/import/excel")
	public ReportDefinitionWrapper importExcel(@RequestParam("file") MultipartFile file) {
		ReportDefinition report = ExcelParserUtils.parser(file);
		if (report != null) {
			report.setReportFullName("classpath:templates/template.ureport.xml");
			return new ReportDefinitionWrapper(report);
		}
		throw new RuntimeException("未识别文件");
	}
	
	/**
	 * 导入图片
	 * @param file
	 * @return
	 */
	@RequestMapping("/import/image")
	public int importImage(@RequestParam("file") MultipartFile file) {
		String name = file.getOriginalFilename();
		if (!name.endsWith(".jpg") && !name.endsWith(".jpeg") && !name.endsWith(".png")) {
			throw new RuntimeException("不支持此格式图片");
		}
		File parentFile = new File(fileStoreDir);
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		String fullPath = fileStoreDir + "/" + name;
		try (InputStream inputStream = file.getInputStream();
			FileOutputStream outputStream = new FileOutputStream(new File(fullPath))){
			IOUtils.copy(inputStream, outputStream);
			return 1;
		} catch (Exception e) {
			throw new ReportException(e);
		}
	}
	
	/**
	 * 查看所有图片
	 * @return
	 */
	@RequestMapping("/image/files")
	public List<String> getReportFiles(HttpServletRequest request) {
		String scheme = request.getScheme();
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		String contextPath = request.getContextPath();
		String path = MessageFormat.format("{0}://{1}:{2,number,#}/resource/images/", scheme, serverName, serverPort);
		if(com.bstek.common.utils.StringUtils.isNotBlank(contextPath)) {
			path = MessageFormat.format("{0}://{1}:{2,number,#}/{3}/resource/images/", scheme, serverName, serverPort,contextPath);
		}
		File file = new File(fileStoreDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		List<String> list = new ArrayList<String>();
		for (File f : file.listFiles()) {
			list.add(path + f.getName());
		}
		return list;
	}
}
