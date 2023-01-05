package com.lzb.rock.mybatis.config;

/**
 * 数据源切换类接口
 *
 * @author Administrator
 *
 * @date 2019年11月19日 下午12:18:00
 */
public interface IDynamicDataSource {
	Object determineCurrentLookupKey();
}
