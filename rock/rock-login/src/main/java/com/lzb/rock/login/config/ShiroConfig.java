package com.lzb.rock.login.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.lzb.rock.base.holder.SpringContextHolder;
import com.lzb.rock.login.filter.RockFilter;
import com.lzb.rock.login.filter.RockUserFilter;
import com.lzb.rock.login.shiro.ShiroRealm;

import lombok.extern.slf4j.Slf4j;

/**
 * Shiro 配置类 https://www.cnblogs.com/zhaojiatao/p/8482998.html
 * 
 * @author lzb
 * 
 *         2019年4月1日 下午9:18:52
 * 
 */
@Configuration
@DependsOn("springContextHolder")
@Slf4j
public class ShiroConfig {

	@Value("${sessionTimeout:36000}")
	Long sessionTimeout;

	@Value("${sessionValidationInterval:60}")
	Long sessionValidationInterval;

	/**
	 * ShiroFilterFactoryBean 处理拦截资源文件问题。 注意：单独一个ShiroFilterFactoryBean配置是或报错的，以为在
	 * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
	 *
	 * Filter Chain定义说明 1、一个URL可以配置多个Filter，使用逗号分隔 2、当设置多个过滤器时，全部验证通过，才视为通过
	 * 3、部分过滤器可指定参数，如perms，roles
	 *
	 */
	@Bean
	public ShiroFilterFactoryBean shirFilter(@Qualifier("securityManager") SecurityManager securityManager) {

		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();

		shiroFilter.setSecurityManager(securityManager);
		/**
		 * 默认的登陆访问url
		 */
		shiroFilter.setLoginUrl("/login");
		/**
		 * 登陆成功后跳转的url
		 */
		shiroFilter.setSuccessUrl("/");
		/**
		 * 没有权限跳转的url
		 */
		shiroFilter.setUnauthorizedUrl("/error");

		/**
		 * 配置shiro拦截器链
		 *
		 * anon 不需要认证 authc 需要认证 user 验证通过或RememberMe登录的都可以
		 *
		 * 当应用开启了rememberMe时,用户下次访问时可以是一个user,但不会是authc,因为authc是需要重新认证的
		 *
		 * 顺序从上到下,优先级依次降低
		 *
		 */
		Map<String, String> hashMap = new LinkedHashMap<>();
		hashMap.put("/static/**", "anon");
		hashMap.put("/sys/download/**", "anon");
		hashMap.put("/view/**", "anon");
		hashMap.put("/login", "anon");
		hashMap.put("/error/**", "anon");
		hashMap.put("/kaptcha", "anon");
		hashMap.put("**", "user");

		shiroFilter.setFilterChainDefinitionMap(hashMap);
		/**
		 * 覆盖默认的user拦截器(默认拦截器解决不了ajax请求 session超时的问题,若有更好的办法请及时反馈作者)
		 */
		HashMap<String, Filter> myFilters = new LinkedHashMap<>();
		RockUserFilter user = new RockUserFilter();
		RockFilter anon = new RockFilter();

		myFilters.put("user", user);
		// 跳过不校验
		myFilters.put("anon", anon);
		shiroFilter.setFilters(myFilters);
		return shiroFilter;

	}

	/**
	 * 安全管理器
	 */
	public SessionManager sessionManager() {
		log.info("sessionTimeout={};sessionValidationInterval={}", sessionTimeout, sessionValidationInterval);
		MySessionManager sessionManager = new MySessionManager();
		// 设置SessionDao
		sessionManager.setSessionDAO(SpringContextHolder.getBean(SessionDAO.class));

		// 获取所有监听类
		List<SessionListener> list = new ArrayList<>();
		Map<String, SessionListener> beans = SpringContextHolder.getApplicationContext()
				.getBeansOfType(SessionListener.class);
		for (Entry<String, SessionListener> entry : beans.entrySet()) {
			list.add(entry.getValue());
		}
		// 设置session监听器
		sessionManager.setSessionListeners(list);
		// 设置session过期时间
		sessionManager.setGlobalSessionTimeout(sessionTimeout * 1000);
		// 定时查询所有session是否过期的时间
		sessionManager.setSessionValidationInterval(sessionValidationInterval * 1000);
		// 设置cookie
		sessionManager.setSessionIdCookie(rememberMeCookie());
		return sessionManager;
	}

//	/**
//	 * 缓存管理器 使用Ehcache实现
//	 *为分布式，采用sessionDao实现
//	 */
//	@Bean
//	public CacheManager getCacheShiroManager(EhCacheManager ehcache) {
//		EhCacheManager ehCacheManager = new EhCacheManager();
//		ehCacheManager.setCacheManager(ehcache.getCacheManager());
//		return ehCacheManager;
//	}

	@Bean
	public SecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 设置realm.
		securityManager.setRealm(SpringContextHolder.getBean(ShiroRealm.class));
		// 设置本地缓存
//		EhCacheManager ehCacheManager = new EhCacheManager();
//		ehCacheManager.setCacheManager(SpringContextHolder.getBean(CacheManager.class));
//		securityManager.setCacheManager(ehCacheManager);
		// 注入记住我管理器;
		// session直接保存在应用中
		// securityManager.setRememberMeManager(rememberMeManager());

		// 设置SessionManager session保存通过sessionDao，需要实现该接口，自定义缓存
		securityManager.setSessionManager(sessionManager());
		return securityManager;
	}

	/**
	 * Shiro生命周期处理器
	 * 
	 * @return
	 */
	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
	 * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
	 * 不要使用 DefaultAdvisorAutoProxyCreator 会出现二次代理的问题，这里不详述
	 * 
	 * @return
	 */
	/*
	 * @Bean
	 * 
	 * @DependsOn({"lifecycleBeanPostProcessor"}) public
	 * DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
	 * DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new
	 * DefaultAdvisorAutoProxyCreator();
	 * advisorAutoProxyCreator.setProxyTargetClass(true); return
	 * advisorAutoProxyCreator; }
	 */
	/**
	 * 启用shrio授权注解拦截方式，AOP式方法级权限检查
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
		return authorizationAttributeSourceAdvisor;
	}

	/**
	 * cookie对象; 记住密码实现起来也是比较简单的，主要看下是如何实现的。
	 * 
	 * @return
	 */
	@Bean
	public SimpleCookie rememberMeCookie() {
		log.info("rememberMeCookie===========>");
		// 这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
		SimpleCookie simpleCookie = new SimpleCookie("shiro-sessionId");
		// <!-- 记住我cookie生效时间30天 ,单位秒;-->
		simpleCookie.setMaxAge(60 * 60 * 24 * 30);
		return simpleCookie;
	}

	/**
	 * cookie管理对象;
	 * 
	 * @return
	 */
	@Bean
	public CookieRememberMeManager rememberMeManager() {
		log.info("ShiroConfiguration.rememberMeManager()================>");
		CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
		cookieRememberMeManager.setCookie(rememberMeCookie());
		return cookieRememberMeManager;
	}
}