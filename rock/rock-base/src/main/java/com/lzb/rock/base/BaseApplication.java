package com.lzb.rock.base;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.swagger.annotations.Api;

/**
 * 启动基类
 * 
 * @author lzb 2018年2月8日 上午11:41:55
 */
@Api(tags = { "默认接口" })
@RestController
public abstract class BaseApplication extends BaseControllerExceptionAdvice {
	
	//http://127.0.0.1:8090/swagger-ui.html#
	//http://127.0.0.1:8090/actuator/prometheus
	//http://127.0.0.1:8090/actuator/health

	@GetMapping("/home")
	@ResponseBody
	public String home() {
		return "home";
	}

	@GetMapping("/info")
	@ResponseBody
	public String info() {
		return "info";
	}

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public RibbonRest initializationRibbonRest() {
		RibbonRest ribbonRest = new RibbonRest();
		return ribbonRest;
	}
}
