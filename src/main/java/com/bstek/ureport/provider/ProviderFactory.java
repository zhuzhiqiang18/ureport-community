package com.bstek.ureport.provider;

import com.bstek.ureport.provider.image.DefaultImageProvider;
import com.bstek.ureport.provider.image.HttpImageProvider;
import com.bstek.ureport.provider.image.HttpsImageProvider;
import com.bstek.ureport.provider.image.ImageProvider;
import com.bstek.ureport.provider.report.ReportProvider;
import com.bstek.ureport.provider.report.classpath.ClasspathReportProvider;
import com.bstek.ureport.provider.report.file.FileReportProvider;

public class ProviderFactory {
	
	private static final ReportProvider fileReportProvider = new FileReportProvider();

	private static final ReportProvider classpathReportProvider = new ClasspathReportProvider();
	
	private static final ImageProvider defaultImageProvider = new DefaultImageProvider();
	
	private static final ImageProvider httpImageProvider = new HttpImageProvider();
	
	private static final ImageProvider httpsImageProvider = new HttpsImageProvider();

	public static ReportProvider getReportProvider(String fileName) {
		if (fileName.startsWith(fileReportProvider.getPrefix())) {
			return fileReportProvider;
		}
		if (fileName.startsWith(classpathReportProvider.getPrefix())) {
			return classpathReportProvider;
		}
		return null;
	}
	
	public static ImageProvider getImageProvider(String path) {
		if (defaultImageProvider.support(path)) {
			return defaultImageProvider;
		}
		if (httpImageProvider.support(path)) {
			return httpImageProvider;
		}
		if (httpsImageProvider.support(path)) {
			return httpsImageProvider;
		}
		return null;
	}
	
	public static ReportProvider getFileReportProvider() {
		return fileReportProvider;
	}
}
