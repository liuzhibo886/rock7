package com.lzb.rock.sharding.config;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.exception.BusException;
import com.lzb.rock.base.util.UtilClass;
import com.lzb.rock.base.util.UtilString;
import com.lzb.rock.sharding.aop.annotation.ShardingBroadcast;
import com.lzb.rock.sharding.aop.annotation.ShardingDatabaseRule;
import com.lzb.rock.sharding.aop.annotation.ShardingRule;
import com.lzb.rock.sharding.aop.annotation.ShardingTableRule;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author lzb
 * @Date 2021-1-3 18:29:43
 *
 */
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "sharding", value = "enabled", havingValue = "true")
public class DataShardingSourceConfig {

	@Value("${sharding.basePackages}")
	String basePackages;

	@Autowired
	BaseDruidDataSourceConfig baseDataSourceConfig;

	@Autowired(required = false)
	List<MasterSlaveRuleConfiguration> masterSlaveRuleConfiguration;

	// https://shardingsphere.apache.org/document/legacy/4.x/document/cn/manual/sharding-jdbc/configuration/config-java/#%E6%95%B0%E6%8D%AE%E5%88%86%E7%89%87
	// https://shardingsphere.apache.org/document/legacy/4.x/document/cn/manual/sharding-jdbc/configuration/config-java/#%E6%95%B0%E6%8D%AE%E5%88%86%E7%89%87
	@Bean
	public DataSource getShardingDataSource() throws Exception {
		if (UtilString.isBlank(basePackages)) {
			throw new BusException(ResultEnum.PAEAM_ERR, "basePackages 不能为空");
		}

		// 数据分片配置规则
		ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();

		// 分片规则列表
		List<Class<?>> ShardingRules = UtilClass.getClassByAnnotation(basePackages, ShardingRule.class);
		for (Class<?> each : ShardingRules) {
			ShardingRule shardingRule = each.getAnnotation(ShardingRule.class);
			ShardingDatabaseRule databaseRule = each.getAnnotation(ShardingDatabaseRule.class);
			ShardingTableRule tableRule = each.getAnnotation(ShardingTableRule.class);
			TableRuleConfiguration tableRuleConfigs = getTableRuleConfiguration(shardingRule, databaseRule, tableRule);
			// 绑定分库规则
			shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfigs);
			// 绑定表规则列表
			shardingRuleConfig.getBindingTableGroups().add(shardingRule.logicTable());
		}

		// 未配置分片规则的表将通过默认数据源定位,设置了就报错
//		if (UtilString.isNotEmpty(defaultDataSourceName)) {
//			shardingRuleConfig.setDefaultDataSourceName(defaultDataSourceName);
//		}

		// 广播表规则列表
		List<Class<?>> shardingBroadcasts = UtilClass.getClassByAnnotation(basePackages, ShardingBroadcast.class);
		for (Class<?> each : shardingBroadcasts) {
			ShardingBroadcast sardingBroadcast = each.getAnnotation(ShardingBroadcast.class);
			if (sardingBroadcast != null) {
				if (UtilString.isNotBlank(sardingBroadcast.logicTable())) {
					shardingRuleConfig.getBroadcastTables().add(sardingBroadcast.logicTable());
					shardingRuleConfig.getTableRuleConfigs()
							.add(getBroadcasTableRuleConfiguration(sardingBroadcast.logicTable(),
									sardingBroadcast.idColumn(), sardingBroadcast.actualDataNodes()));

				} else {
					throw new BusException(ResultEnum.PAEAM_ERR, "广播表表名不能为空," + each.getName());
				}
			}
		}
		// 默认分库策略
//		shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(
//				new InlineShardingStrategyConfiguration("tenant_id", "ds_${tenant_id % 3}"));

		// 默认分表策略
		// shardingRuleConfig.setDefaultTableShardingStrategyConfig(new
		// StandardShardingStrategyConfiguration("order_id", new
		// ModuloShardingTableAlgorithm()));

		// 默认自增列值生成器配置，缺省将使用org.apache.shardingsphere.core.keygen.generator.impl.SnowflakeKeyGenerator
		// shardingRuleConfig.setDefaultKeyGeneratorConfig(new SnowflakeKeyGenerator);

		// 读写分离规则，缺省表示不使用读写分离
		if (masterSlaveRuleConfiguration != null && masterSlaveRuleConfiguration.size() > 0) {
			shardingRuleConfig.setMasterSlaveRuleConfigs(masterSlaveRuleConfiguration);
			log.info("sharding 启用读写分离");
		}

		// 数据脱敏
		// shardingRuleConfig.setEncryptRuleConfig(getEncryptRuleConfiguration());

		Properties properties = new Properties();
		// 显示SQL
		properties.put("sql.show", true);
		// 当使用inline分表策略时，是否允许范围查询，默认值: false
		properties.put("allow.range.query.with.inline.sharding", true);

		Map<String, DataSource> dataSourceMap = baseDataSourceConfig.dataSource();
		DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig,
				properties);
		return dataSource;
	}

	/**
	 * 自增列值生成器
	 * 
	 * @return
	 */
	private static KeyGeneratorConfiguration getKeyGeneratorConfiguration(String logicTable, String idColumn) {
		Properties pro = new Properties();
		pro.put("idColumn", idColumn);
		pro.put("logicTable", logicTable);
		KeyGeneratorConfiguration result = new KeyGeneratorConfiguration("REDIS", idColumn, pro);
//		KeyGeneratorConfiguration result = new KeyGeneratorConfiguration("SNOWFLAKE", idColumn,pro);
		return result;
	}

	/**
	 * 广播表规则
	 * 
	 * @param logicTable
	 * @param idColumn
	 * @param actualDataNodes
	 * @param shardingColumn
	 * @param algorithmExpression
	 * @return
	 */
	TableRuleConfiguration getBroadcasTableRuleConfiguration(String logicTable, String idColumn,
			String actualDataNodes) {
		TableRuleConfiguration result = new TableRuleConfiguration(logicTable, actualDataNodes);
		// 自增列值生成器配置，缺省表示使用默认自增主键生成器
		result.setKeyGeneratorConfig(getKeyGeneratorConfiguration(logicTable, idColumn));
		return result;
	}

	/**
	 * 分库表规则
	 * 
	 * @param logicTable
	 * @param idColumn
	 * @param actualDataNodes
	 * @param shardingColumn
	 * @param algorithmExpression
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	TableRuleConfiguration getTableRuleConfiguration(ShardingRule shardingRule, ShardingDatabaseRule databaseRule,
			ShardingTableRule tableRule) throws Exception {
		TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration(shardingRule.logicTable(),
				shardingRule.actualDataNodes());

		// 分库策略
		if (databaseRule != null) {
			if (databaseRule.preciseShardingAlgorithm() == null || databaseRule.preciseShardingAlgorithm().length < 1) {
				tableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration(
						databaseRule.ruleColumn(), databaseRule.algorithmExpression()));
			} else {
				// 精确分片处理
				Class<? extends PreciseShardingAlgorithm<?>>[] shardingAlgorithmArr = databaseRule
						.preciseShardingAlgorithm();

				// PreciseShardingAlgorithm<?> shardingAlgorithm =
				// shardingAlgorithmArr[0].newInstance();

				String className = shardingAlgorithmArr[0].getName();
				PreciseShardingAlgorithm<?> shardingAlgorithm = UtilClass.newInstance(className);

				// 范围分片处理
				Class<? extends RangeShardingAlgorithm<?>>[] rangeShardingAlgorithmArr = databaseRule
						.rangeShardingAlgorithm();

				if (rangeShardingAlgorithmArr == null || rangeShardingAlgorithmArr.length < 1) {
					tableRuleConfig.setDatabaseShardingStrategyConfig(
							new StandardShardingStrategyConfiguration(databaseRule.ruleColumn(), shardingAlgorithm));
				} else {
					RangeShardingAlgorithm<?> rangeShardingAlgorithm = rangeShardingAlgorithmArr[0].newInstance();
					tableRuleConfig.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration(
							databaseRule.ruleColumn(), shardingAlgorithm, rangeShardingAlgorithm));

				}

			}
		}

		// 分表策略
		if (tableRule != null) {

			if (tableRule.preciseShardingAlgorithm() == null || tableRule.preciseShardingAlgorithm().length < 1) {

				if (UtilString.isBlank(tableRule.algorithmExpression())) {
					log.error("表:{},ShardingTableRule注解，algorithmExpression为空", shardingRule.logicTable());
					throw new BusException(ResultEnum.PAEAM_ERR, "ruleColumn 不能为空");
				}

				tableRuleConfig.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration(
						tableRule.ruleColumn(), tableRule.algorithmExpression()));
			} else {
				Class<? extends PreciseShardingAlgorithm<?>>[] shardingAlgorithmArr = tableRule
						.preciseShardingAlgorithm();

				PreciseShardingAlgorithm<?> shardingAlgorithm = shardingAlgorithmArr[0].newInstance();

				Class<? extends RangeShardingAlgorithm<?>>[] rangeShardingAlgorithmArr = tableRule
						.rangeShardingAlgorithm();
				if (rangeShardingAlgorithmArr == null || rangeShardingAlgorithmArr.length < 1) {
					// 配置=和IN 使用BETWEEN时全局扫描
					tableRuleConfig.setTableShardingStrategyConfig(
							new StandardShardingStrategyConfiguration(tableRule.ruleColumn(), shardingAlgorithm));
				} else {
					// 配置=和IN 和 BETWEEN ，使用BETWEEN 时使用
					RangeShardingAlgorithm<?> rangeShardingAlgorithm = rangeShardingAlgorithmArr[0].newInstance();
					tableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration(
							tableRule.ruleColumn(), shardingAlgorithm, rangeShardingAlgorithm));
				}

			}

		}

		// 自增列值生成器配置，缺省表示使用默认自增主键生成器
		tableRuleConfig.setKeyGeneratorConfig(
				getKeyGeneratorConfiguration(shardingRule.logicTable(), shardingRule.idColumn()));

		return tableRuleConfig;
	}

//	TableRuleConfiguration getTestTableRuleConfiguration(String keyColumn) {

//		TableRuleConfiguration result = new TableRuleConfiguration("demo_test", "ds_${[0,1,2]}.demo_test");
//		result.setTableShardingStrategyConfig(
//				new InlineShardingStrategyConfiguration("tenant_id", "ds_${tenant_id % 3}"));
//		// 自增列值生成器配置，缺省表示使用默认自增主键生成器
//		result.setKeyGeneratorConfig(getKeyGeneratorConfiguration(keyColumn));
//		return result;
//	}

}
