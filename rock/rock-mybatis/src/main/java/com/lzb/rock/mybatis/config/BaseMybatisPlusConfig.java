package com.lzb.rock.mybatis.config;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

/**
 * 扫描dao或者是Mapper接口
 * 
 * @author lzb
 * @Date 2019年7月31日 下午5:03:28
 */
@Configuration
@MapperScan("com.lzb.rock.**.mapper.**")
public class BaseMybatisPlusConfig {

	@Autowired(required = false)
	List<TenantHandler> tenantHandlers;

	/**
	 * mybatis-plus 分页插件
	 */
	@Bean
	public PaginationInterceptor paginationInterceptor() {
		PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
		paginationInterceptor.setDialectType("mysql");

		if (tenantHandlers != null && tenantHandlers.size() > 0) {
			// 创建SQL解析器集合
			List<ISqlParser> sqlParserList = new ArrayList<>();
			for (TenantHandler tenantHandler : tenantHandlers) {
				// 创建租户SQL解析器
				TenantSqlParser tenantSqlParser = new TenantSqlParser();
				// 设置租户处理器
				tenantSqlParser.setTenantHandler(tenantHandler);
				sqlParserList.add(tenantSqlParser);
			}
			//移除默认限制单页500条数据
//			paginationInterceptor.setLimit(-1);
			paginationInterceptor.setSqlParserList(sqlParserList);
		}

		return paginationInterceptor;
	}

	/**
	 * 乐观锁mybatis插件
	 */
	@Bean
	public OptimisticLockerInterceptor optimisticLockerInterceptor() {
		return new OptimisticLockerInterceptor();
	}

	/*
	 * 开启逻辑删除(3.1.1开始不再需要这一步)：
	 */
//	@Bean
//	public ISqlInjector sqlInjector() {
//		return new LogicSqlInjector();
//	}
}
