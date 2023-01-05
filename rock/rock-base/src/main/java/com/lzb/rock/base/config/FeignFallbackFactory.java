package com.lzb.rock.base.config;

import org.springframework.stereotype.Component;

import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.model.Result;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FeignFallbackFactory implements FallbackFactory<Result<Void>> {

	@Override
	public Result<Void> create(Throwable cause) {
		
		log.info(cause.getMessage());
		
		return Result.newInstance(ResultEnum.HYSTRIX_ERR);
	}


}
