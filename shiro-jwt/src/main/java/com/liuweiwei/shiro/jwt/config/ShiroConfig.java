package com.liuweiwei.shiro.jwt.config;

import com.liuweiwei.shiro.jwt.filter.JWTFilter;
import com.liuweiwei.shiro.jwt.shiro.CustomRealm;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA
 * 1. 先走 filter
 * 2. 然后 filter 如果检测到请求头存在 token
 * 3. 则用 token 去 login
 * 4. 再走 Realm 去验证
 *
 * @author liuweiwei 505831001@qq.com
 * @since 2018-04-06
 */
@Configuration
public class ShiroConfig {
    /**
     * 1. 用于Spring的Bean post处理器，
     * 自动调用Shiro对象上的init()和/或destroy()方法，
     * 该对象实现{@link org.apache.shiro.util.Initializable}或{@link org.apache.shiro.util.Destroyable}接口，值得大家的尊敬。
     *
     * @return LifecycleBeanPostProcessor
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        System.out.println("<<Spring Boot 启动加载配置>> - 1. Bean生命周期后处理器注入成功");
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 2. 基于当前{@code BeanFactory}中所有候选{@code Advisor}创建AOP代理的实现。
     * 这个类是完全通用的；
     * 它不包含处理任何特定方面(如池化方面)的特殊代码。
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题https://zhuanlan.zhihu.com/p/29161098
        advisorAutoProxyCreator.setProxyTargetClass(true);
        System.out.println("<<Spring Boot 启动加载配置>> - 2. Advisor默认自动代理创建器注入成功：" + advisorAutoProxyCreator.toString());
        return advisorAutoProxyCreator;
    }

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
        // 1.1 关闭Shiro自带的session详情见文档http://shiro.apache.org/session-management.html#SessionManagement-StatelessApplications%28Sessionless%29
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        System.out.println("<<Spring Boot 启动加载配置>> - 3. Web安全管理类注入成功：" + securityManager.toString());
        return securityManager;
    }

    /**
     * 4. {@link org.springframework.beans.factory.FactoryBean} 用于在基于spring的web应用程序中定义主Shiro过滤器。
     *
     * @param securityManager
     * @return ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean factory(SecurityManager securityManager) {
        // 2.1 设置在创建{@link 过滤器链自定义}时可用的过滤器的过滤器名-过滤器映射。
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("jwt", new JWTFilter());
        // 2.2 设置链定义的链名到链定义映射，以用于创建被Shiro筛选器拦截的筛选器链。
        Map<String, String> filterChainDefinitionMap = new HashMap<>();
        filterChainDefinitionMap.put("/**", "jwt");
        filterChainDefinitionMap.put("/unauthorized/**", "anon");

        // 2. 设置<Shiro>Filter
        ShiroFilterFactoryBean filter = new ShiroFilterFactoryBean();
        filter.setFilters(filters);
        filter.setSecurityManager(securityManager);
        filter.setLoginUrl("/notLogin");
        filter.setUnauthorizedUrl("/unauthorized/无权限");
        filter.setFilterChainDefinitionMap(filterChainDefinitionMap);
        System.out.println("<<Spring Boot 启动加载配置>> - 4. Shiro拦截器工厂类注入成功：" + filter.toString());
        return filter;
    }

    /**
     * 5. 如果方法有任何Shiro注释，返回true，否则返回false。
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor attributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        System.out.println("<<Spring Boot 启动加载配置>> - 5. Advisor授权属性来源注入成功：" + attributeSourceAdvisor.toString());
        attributeSourceAdvisor.setSecurityManager(securityManager);
        return attributeSourceAdvisor;
    }
}
