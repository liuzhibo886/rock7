package com.lzb.rock.base.factory;

import org.springframework.context.annotation.Configuration;

import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.model.Result;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author lzb
 * @Date 2019年7月31日 下午4:57:50 Feign Fallback配置类
 */
@Configuration
@Slf4j
public class MyFallbackFactory implements FallbackFactory<Result<Void>> {

	@Override
	public Result<Void> create(Throwable cause) {
		log.debug("create =====>");
		Result<Void> result = new Result<Void>(ResultEnum.REST_ERR);
		return result;
	}

}
