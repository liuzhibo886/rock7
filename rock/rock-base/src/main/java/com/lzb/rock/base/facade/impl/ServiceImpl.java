package com.lzb.rock.base.facade.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.lzb.rock.base.facade.IService;

/**
 * IService 实现类（ 泛型：M 是 mapper 对象，T 是实体 ， PK 是主键泛型 ）
 *
 * @author hubin
 * @since 2018-06-23
 */
@SuppressWarnings("unchecked")
public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {

	protected Log log = LogFactory.getLog(getClass());

	@Autowired
	protected M baseMapper;

	@Override
	public M getBaseMapper() {
		return baseMapper;
	}

	protected Class<T> currentModelClass() {
		return (Class<T>) ReflectionKit.getSuperClassGenericType(getClass(), 1);
	}

	/**
	 * 批量操作 SqlSession
	 */
	protected SqlSession sqlSessionBatch() {
		return SqlHelper.sqlSessionBatch(currentModelClass());
	}

	/**
	 * 释放sqlSession
	 *
	 * @param sqlSession session
	 */
	protected void closeSqlSession(SqlSession sqlSession) {
		SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(currentModelClass()));
	}

	/**
	 * 获取 SqlStatement
	 *
	 * @param sqlMethod ignore
	 * @return ignore
	 */
	protected String sqlStatement(SqlMethod sqlMethod) {
		return SqlHelper.table(currentModelClass()).getSqlStatement(sqlMethod.getMethod());
	}

	@Override
	public Integer insert(T entity) {
		return baseMapper.insert(entity);
	}

	@Override
	public Integer deleteById(Serializable id) {
		return baseMapper.deleteById(id);
	}

	@Override
	public Integer deleteByMap(Map<String, Object> columnMap) {
		Assert.notEmpty(columnMap, "error: columnMap must not be empty");
		return baseMapper.deleteByMap(columnMap);
	}

	@Override
	public Integer delete(Wrapper<T> wrapper) {
		return baseMapper.delete(wrapper);
	}

	@Override
	public Integer updateById(T entity) {
		return baseMapper.updateById(entity);
	}

	@Override
	public Integer update(T entity, Wrapper<T> updateWrapper) {
		return baseMapper.update(entity, updateWrapper);
	}

	@Override
	public T getById(Serializable id) {
		return baseMapper.selectById(id);
	}

	@Override
	public Collection<T> listByIds(Collection<? extends Serializable> idList) {
		return baseMapper.selectBatchIds(idList);
	}

	@Override
	public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
		if (throwEx) {
			return baseMapper.selectOne(queryWrapper);
		}
		return SqlHelper.getObject(log, baseMapper.selectList(queryWrapper));
	}

	@Override
	public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
		return SqlHelper.getObject(log, baseMapper.selectMaps(queryWrapper));
	}

	@Override
	public int count(Wrapper<T> queryWrapper) {
		return SqlHelper.retCount(baseMapper.selectCount(queryWrapper));
	}

	@Override
	public List<T> list(Wrapper<T> queryWrapper) {
		return baseMapper.selectList(queryWrapper);
	}

	@Override
	public Page<T> page(Page<T> page, Wrapper<T> queryWrapper) {
		return (Page<T>) baseMapper.selectPage(page, queryWrapper);
	}

	@Override
	public List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper) {
		return baseMapper.selectMaps(queryWrapper);
	}

	@Override
	public <V> List<V> listObjs(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
		return baseMapper.selectObjs(queryWrapper).stream().filter(Objects::nonNull).map(mapper)
				.collect(Collectors.toList());
	}

	@Override
	public Page<Map<String, Object>> pageMaps(Page<T> page, Wrapper<T> queryWrapper) {
		return (Page<Map<String, Object>>) baseMapper.selectMapsPage(page, queryWrapper);
	}

	@Override
	public <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
		return SqlHelper.getObject(log, listObjs(queryWrapper, mapper));
	}
}
