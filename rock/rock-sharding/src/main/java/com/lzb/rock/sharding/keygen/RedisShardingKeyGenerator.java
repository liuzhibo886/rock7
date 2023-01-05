package com.lzb.rock.sharding.keygen;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;

import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.exception.BusException;
import com.lzb.rock.base.holder.SpringContextHolder;
import com.lzb.rock.base.util.UtilString;
import com.lzb.rock.redis.mapper.RedisMapper;

import lombok.Getter;
import lombok.Setter;

/**
 * 本地自增，适用测试
 * 
 * @author lzb
 *
 */
public class RedisShardingKeyGenerator implements ShardingKeyGenerator {

	@Getter
	@Setter
	private Properties properties = new Properties();

	@Override
	public String getType() {
		return "REDIS";
	}

	@Override
	public Comparable<?> generateKey() {
		RedisMapper redisMapper = SpringContextHolder.getBean(RedisMapper.class);
		if (redisMapper == null) {
			throw new BusException(ResultEnum.RUNTIME_ERR, "RedisMapper 未实例化");
		}
		return redisMapper.increment(redisKey(), 1L);
	}

	/**
	 * redis 自增主键key
	 * 
	 * @return
	 */
	private String redisKey() {
		String idColumn = "";
		String logicTable = "";
		if (getProperties() != null) {
			idColumn = getProperties().getProperty("idColumn", "");
			logicTable = getProperties().getProperty("logicTable", "");
		}

		String key = "SHARDING_KEY";
		if (UtilString.isNotBlank(logicTable)) {
			key = key + "_" + logicTable;
		}
		if (UtilString.isNotBlank(idColumn)) {
			key = key + "_" + idColumn;
		}
		if (UtilString.isBlank(idColumn)) {
			key = "_DEFAULT";
		}
		key = key.toUpperCase();
		return key;
	}

}
