//package com.example.demo.login.config;
//
//import org.apache.shiro.mgt.SecurityManager;
//import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
//import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
//import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.Properties;
//
///**
// * 这里的java配置相当于spring MVC中的xml配置
// */
//@Configuration
//public class ShiroConfig
//{
//    @Bean
//    public ShiroFilterFactoryBean shiroFilter(SecurityManager manager)
//    {
//        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
//
//        // 必须设置 SecurityManager
//        bean.setSecurityManager(manager);
//
//        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
//        bean.setLoginUrl("/login");
//
//        bean.setSuccessUrl("/index");
//
//        //未授权界面;
//        bean.setUnauthorizedUrl("/error");
//
//        //拦截器.
//        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
//
//        // 配置不会被拦截的链接 顺序判断
////        filterChainDefinitionMap.put("/static/**", "anon");
//
//        //配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
//        filterChainDefinitionMap.put("/logout", "logout");
//
//        //<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
//        //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
//
//        filterChainDefinitionMap.put("/**", "authc");
//
//        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
//        return bean;
//    }
//
//    @Bean
//    public MyShiroRealm myShiroRealm() {
//        MyShiroRealm myShiroRealm = new MyShiroRealm();
//        return myShiroRealm;
//    }
//
//    @Bean(name = "securityManager")
//    public SecurityManager securityManager(MyShiroRealm myShiroRealm){
//        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
//        securityManager.setRealm(myShiroRealm);
//        return securityManager;
//    }
//
//    /**
//     *  开启shiro aop注解支持.
//     *  使用代理方式;所以需要开启代码支持;
//     * @param securityManager
//     * @return
//     */
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
//        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
//        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
//        return authorizationAttributeSourceAdvisor;
//    }
//
//    @Bean(name="simpleMappingExceptionResolver")
//    public SimpleMappingExceptionResolver
//    createSimpleMappingExceptionResolver() {
//        SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();
//        Properties mappings = new Properties();
//        mappings.setProperty("DatabaseException", "error");//数据库异常处理
//        mappings.setProperty("UnauthorizedException","403");
//        r.setExceptionMappings(mappings);  // None by default
//        r.setDefaultErrorView("error");    // No default
//        r.setExceptionAttribute("ex");     // Default is "exception"
//        //r.setWarnLogCategory("example.MvcLogger");     // No default
//        return r;
//    }
//}
