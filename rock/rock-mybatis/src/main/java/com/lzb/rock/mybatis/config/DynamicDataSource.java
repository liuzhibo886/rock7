package com.lzb.rock.mybatis.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.lzb.rock.base.holder.SpringContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据源切换类 注意线程问题 同时注意事物问题
 * 
 * @author lzb
 * 
 *         2019年3月10日 上午11:20:45
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {

		IDynamicDataSource dynamicDataSource = null;
		if (SpringContextHolder.isNotNull()) {
			try {
				dynamicDataSource = SpringContextHolder.getBean(IDynamicDataSource.class);
				if (dynamicDataSource != null) {
					return dynamicDataSource.determineCurrentLookupKey();
				}
			} catch (Exception e) {
				dynamicDataSource = null;
			}

		}

		return DataSourceContextHolder.getDataSourceKey();
	}
}
