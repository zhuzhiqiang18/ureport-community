package com.bstek.common.config;

import java.text.MessageFormat;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfig implements WebMvcConfigurer {
 
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	String path = System.getProperty("user.dir");
    	//映射图片文件资源路径
    	String locationsPath = MessageFormat.format("file:{0}/resource/images/", path);
        registry.addResourceHandler("/resource/images/**").addResourceLocations(locationsPath);
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	// 允许跨域访问字体
    	registry.addMapping("/css/fonts/element-icons.woff");
    }
}