package com.lzb.rock.sharding.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;
import com.lzb.rock.base.util.UtilBean;
import com.lzb.rock.base.util.UtilClass;
import com.lzb.rock.base.util.UtilString;
import com.lzb.rock.sharding.properties.DruidProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据源配置 由于引入多数据源，所以让spring事务的aop要在多数据源切换aop的后面
 */
@Configuration
@Slf4j
@EnableTransactionManagement(order = 2)
@ConditionalOnProperty(prefix = "sharding", value = "enabled", havingValue = "true")
public class BaseDruidDataSourceConfig {

	@Autowired
	ShardingProperties shardingProperties;
	/**
	 * 初始化数据源集合
	 */
	private HashMap<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();

	/**
	 * 数据源配置
	 * 
	 * @return
	 */
	public Map<String, DataSource> dataSource() {

		Map<String, String> dataSourcePro = shardingProperties.getDatasource();
		Set<String> keys = new TreeSet<String>();
		for (String dataSourceKey : dataSourcePro.keySet()) {
			if (StringUtils.isNotBlank(dataSourceKey)) {
				String[] arr = dataSourceKey.split("\\.");
				if (arr != null && arr.length == 2 && StringUtils.isNotBlank(arr[1])) {
					keys.add(arr[0]);
				}
			}
		}
		log.info("====================>多数据源初始化");
		DruidProperties druidProperties = new DruidProperties();
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
			log.info("初始化数据源;datasourcename={};username={};url={}", datasourcename, druidMutiProperties.getUsername(),
					druidMutiProperties.getUrl());
		}

		return dataSourceMap;

	}
}
