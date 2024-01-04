package com.bstek.shiro;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfig {
	
    @Bean
    public ShiroFilterFactoryBean factory(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        // 认证过滤器
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("token", new TokenFilter());
        factoryBean.setFilters(filterMap);
        factoryBean.setSecurityManager(securityManager);
        
        // 设置无权限跳转链接
        factoryBean.setUnauthorizedUrl("/unauthorized/无权限");
        
        // 过滤规则配置
        Map<String, String> filterRuleMap = new LinkedHashMap<String, String>();
        
        filterRuleMap.put("/sys/user/digest", "anon");
        filterRuleMap.put("/sys/user/login", "anon");
        filterRuleMap.put("/login", "anon");
        filterRuleMap.put("/", "anon");
        filterRuleMap.put("/favicon.ico", "anon");
        filterRuleMap.put("/index.html", "anon");
        filterRuleMap.put("/static/**", "anon");
        filterRuleMap.put("/resource/images/**", "anon");
        
        filterRuleMap.put("/**", "token");
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        
        return factoryBean;
    }

    @Bean
    public SecurityManager securityManager(TokenRealm tokenRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置自定义 realm
        securityManager.setRealm(tokenRealm);
        
        //关闭shiro自带的session
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        return securityManager;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}
