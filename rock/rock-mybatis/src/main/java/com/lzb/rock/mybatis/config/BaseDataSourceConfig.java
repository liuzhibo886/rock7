package com.lzb.rock.mybatis.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;
import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.exception.BusException;
import com.lzb.rock.base.util.UtilBean;
import com.lzb.rock.base.util.UtilClass;
import com.lzb.rock.base.util.UtilString;
import com.lzb.rock.mybatis.properties.DruidProperties;
import com.lzb.rock.mybatis.properties.MysqlMutiProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据源配置 由于引入多数据源，所以让spring事务的aop要在多数据源切换aop的后面
 */
@Configuration
@Slf4j
@EnableTransactionManagement(order = 2)
public class BaseDataSourceConfig {

	@Autowired
	DruidProperties druidProperties;

	@Value("${mybatis.mutiDatasourceOnOff:false}")
	boolean mutiDatasourceOnOff;

	@Value("${mybatis.defaultDatasource:}")
	String defaultDatasource;

	@Autowired
	MysqlMutiProperties mysqlMutiProperties;
	/**
	 * 初始化数据源集合
	 */
	public static HashMap<Object, Object> dataSourceMap = new HashMap<Object, Object>();

	/**
	 * 数据源配置，可能多个数据源 当前使用mycat，每一线程独立使用一个数据源 此处创建多个数据源
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingClass("com.lzb.rock.sharding.config.DataShardingSourceConfig")
	public DataSource dataSource() {

		// 多数据源
		if (mutiDatasourceOnOff) {
			if (StringUtils.isBlank(defaultDatasource)) {
				throw new BusException(ResultEnum.DATA_SOURCE_ERR, "默认数据源为空");
			}
			DynamicDataSource dynamicDataSource = new DynamicDataSource();

			Map<String, String> dataSourcePro = mysqlMutiProperties.getDatasource();
			Set<String> keys = new TreeSet<String>();
			for (String dataSourceKey : dataSourcePro.keySet()) {
				if (StringUtils.isNotBlank(dataSourceKey)) {
					String[] arr = dataSourceKey.split("\\.");
					if (arr != null && arr.length == 2 && StringUtils.isNotBlank(arr[1])) {
						keys.add(arr[0]);
					}
				}
			}
			log.info("====================>启用多数据源初始化");
			for (String datasourcename : keys) {
				Field[] fields = UtilClass.getDeclaredFields(DruidProperties.class);
				DruidProperties druidMutiProperties = UtilBean.copyProperties(DruidProperties.class, druidProperties);
				for (Field field : fields) {
					String value = dataSourcePro.get(datasourcename + "." + field.getName());
					if (UtilString.isNotBlank(value)) {
						Object fieldValue = UtilClass.getJavaBeanByString(value, field.getType());
						UtilClass.setFieldValue(field.getName(), fieldValue, druidMutiProperties);
					}
				}
				DruidDataSource dataSource = new DruidDataSource();
				druidMutiProperties.config(dataSource);
				dataSourceMap.put(datasourcename, dataSource);
				log.info("初始化数据源;datasourcename={};username={};url={}", datasourcename,
						druidMutiProperties.getUsername(), druidMutiProperties.getUrl());
			}
			// 判断默认数据源是否存在

			if (dataSourceMap.get(defaultDatasource) == null) {
				throw new BusException(ResultEnum.DATA_SOURCE_ERR, "默认数据源:" + defaultDatasource + "未初始化");
			}
			// 设置默认数据源
			dynamicDataSource.setDefaultTargetDataSource(dataSourceMap.get(defaultDatasource));
			dynamicDataSource.setTargetDataSources(dataSourceMap);
			// dynamicDataSource.setResolvedDataSources(dataSourceMap);

			return dynamicDataSource;
		} else {// 单数据源

			log.info("====================>启用单数据源");
			DruidDataSource dataSource = new DruidDataSource();
			druidProperties.config(dataSource);
			return dataSource;
		}

	}

	/**
	 * 配置事物管理器
	 */
//	@Bean
	public SpringManagedTransactionFactory transactionManager() {
		SpringManagedTransactionFactory transactionFactory = new SpringManagedTransactionFactory();
		return transactionFactory;
	}

}
