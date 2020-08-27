package com.liuweiwei.shiro.config;

import com.liuweiwei.shiro.shiro.CustomRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA
 *
 * @author liuweiwei 505831001@qq.com
 * @since 2018-04-06
 */
@Configuration
public class ShiroConfig {
    /**
     * 3. {@code SecurityManager} 执行跨单个应用程序的所有主题(即用户)的所有安全操作。
     *
     * @param customRealm
     * @return SecurityManager
     */
    @Bean
    public SecurityManager securityManager(CustomRealm customRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 1. 设置自定义Realm
        securityManager.setRealm(customRealm);
        System.out.println("<<Spring Boot 启动加载配置>> - Web 安全管理类注入成功：" + securityManager.toString());
        return securityManager;
    }

    /**
     * 4. {@link org.springframework.beans.factory.FactoryBean} 用于在基于spring的web应用程序中定义主Shiro过滤器。
     *
     * @param securityManager
     * @return ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilter(SecurityManager securityManager) {
        // 2.2 设置链定义的链名到链定义映射，以用于创建被Shiro筛选器拦截的筛选器链。
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/guest/**", "anon");
        filterChainDefinitionMap.put("/user/**", "roles[user]");
        filterChainDefinitionMap.put("/admin/**", "roles[admin]");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/**", "authc");

        // 2. 设置<Shiro>Filter
        ShiroFilterFactoryBean filter = new ShiroFilterFactoryBean();
        filter.setSecurityManager(securityManager);
        filter.setLoginUrl("/notLogin");
        filter.setUnauthorizedUrl("/notRole");
        filter.setFilterChainDefinitionMap(filterChainDefinitionMap);
        System.out.println("<<Spring Boot 启动加载配置>> - Shiro 拦截器工厂类注入成功：" + filter.toString());
        return filter;
    }
}