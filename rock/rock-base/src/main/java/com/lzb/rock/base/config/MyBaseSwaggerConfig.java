package com.lzb.rock.base.config;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import lombok.extern.slf4j.Slf4j;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * http://127.0.0.1:8080/swagger
 */
@Configuration
@Slf4j
public class MyBaseSwaggerConfig {

	/**
	 * 开启swagger2文档的环境
	 */
	List<String> enables = new ArrayList<String>(3) {
		{
			this.add("loc");
			this.add("dev");
			this.add("test");
		}
	};

	@Value("${spring.profiles.active}")
	String active;

	/**
	 * 扫描该包下的所有需要在Swagger中展示的API，@ApiIgnore注解标注的除外
	 * http://127.0.0.1:15027/swagger-ui.html
	 * https://blog.csdn.net/u010963948/article/details/72476854
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(Docket.class)
	public Docket createRestApi() {// 创建API基本信息
		Docket docket = new Docket(DocumentationType.SWAGGER_2);

		docket.apiInfo(apiInfo());
		log.info("active==>{}", active);
		// 是否开启，false 关闭
		if (enables.contains(active)) {
			docket.enable(true);
		} else {
			docket.enable(false);
			return docket;
		}

//		docket = docket.additionalModels(typeResolver.resolve(LightUpRespNetty.class),
//				typeResolver.resolve(BonfireAddWoodNetty.class), typeResolver.resolve(BonfireUserRefNetty.class),
//				typeResolver.resolve(BonfireUserSendMsgNetty.class), typeResolver.resolve(LightUpCallNetty.class),
//				typeResolver.resolve(NettyMsg.class), typeResolver.resolve(BonfireSeekNetty.class));

		/**
		 * objectId 替换为String
		 */
		docket.directModelSubstitute(ObjectId.class, String.class);

		ApiSelectorBuilder apiSelectorBuilder = docket.select();

		// 扫描路径
		apiSelectorBuilder.apis(apis());
		apiSelectorBuilder.paths(PathSelectors.any());
		// 屏蔽乱七八糟接口
		apiSelectorBuilder.paths(Predicates.not(PathSelectors.regex("/error.*")));
		apiSelectorBuilder.build();

		docket.globalOperationParameters(getOperationParameters());
		
//		JWT
//		docket.securitySchemes(securitySchemes());
//		docket.securityContexts(securityContexts());

		return docket;
	}

	/**
	 * 设置扫描包路径
	 * 
	 * @return
	 */
	public Predicate<RequestHandler> apis() {
		Predicate<RequestHandler> selector = Predicates.or(RequestHandlerSelectors.basePackage("com.lzb.rock"));
		return selector;
	}

	/**
	 * 创建API的基本信息，这些信息会在Swagger UI中进行显示
	 */
	public ApiInfo apiInfo() {

		ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder();
		// 联系人
		Contact contact = new Contact("lzb", "", "");
		// 标题
		apiInfoBuilder.title("demo 标题");
		// 描述
		apiInfoBuilder.description("demo");
		// 联系人
		apiInfoBuilder.contact(contact);
		// 版本
		apiInfoBuilder.version("1.0.0");

		return apiInfoBuilder.build();
	}

	/**
	 * 配置全局默认参数
	 * 
	 * @date 2020年7月17日下午8:04:25
	 * @return
	 */
	public List<Parameter> getOperationParameters() {
		ParameterBuilder tokenBuilder = new ParameterBuilder();
		Parameter tokenParameter = tokenBuilder.name("aes-token").description("aes-token")
				.modelRef(new ModelRef("string")).parameterType("header").required(true).defaultValue("").build();

//		ParameterBuilder versionBuilder = new ParameterBuilder();
//		Parameter versionParameter = versionBuilder.name("x-client-version").description("app版本")
//				.modelRef(new ModelRef("string")).parameterType("header").required(true).defaultValue("1.0").build();
//
//		ParameterBuilder platformBuilder = new ParameterBuilder();
//		Parameter platformParameter = platformBuilder.name("x-client-platform").description("平台")
//				.modelRef(new ModelRef("string")).parameterType("header").required(true).defaultValue("android")
//				.build();

		List<Parameter> operationParameters = new ArrayList<Parameter>();
//		operationParameters.add(tokenParameter);
//		operationParameters.add(versionParameter);
//		operationParameters.add(platformParameter);
		return operationParameters;
	}
	
	
	
	private List<ApiKey> securitySchemes() {
		List<ApiKey> apiKeys = new ArrayList<>();
		apiKeys.add(new ApiKey("Authorization", "Authorization", "header"));
		return apiKeys;
	}

	private List<SecurityContext> securityContexts() {
		List<SecurityContext> securityContexts = new ArrayList<>();
		securityContexts.add(SecurityContext.builder().securityReferences(defaultAuth())
				.forPaths(PathSelectors.regex("^(?!auth).*$")).build());
		return securityContexts;
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		List<SecurityReference> securityReferences = new ArrayList<>();
		securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
		return securityReferences;
	}


}
