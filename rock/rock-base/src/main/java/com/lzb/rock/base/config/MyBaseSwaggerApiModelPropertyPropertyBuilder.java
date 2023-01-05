package com.lzb.rock.base.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.lzb.rock.base.aop.annotation.RelEnum;
import com.lzb.rock.base.facade.IBaseResultEnum;

import springfox.documentation.service.AllowableListValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.schema.ApiModelBuilder;
import springfox.documentation.swagger.schema.ApiModelPropertyPropertyBuilder;

/**
 * 重写Swagger 支持RelEnum 注解
 * 
 * @author lzb
 * @date 2020年7月29日下午3:19:48
 */
@Component
@ConditionalOnBean(ApiModelBuilder.class)
//@Profile({ "loc", "dev", "test" })
public class MyBaseSwaggerApiModelPropertyPropertyBuilder extends ApiModelPropertyPropertyBuilder {

	public MyBaseSwaggerApiModelPropertyPropertyBuilder(DescriptionResolver descriptions) {
		super(descriptions);
	}

	@Override
	public void apply(ModelPropertyContext context) {
		AnnotatedField field = context.getBeanPropertyDefinition().get().getField();
		RelEnum relEnum = field.getAnnotation(RelEnum.class);
		if (relEnum != null) {
			try {
				Class<? extends IBaseResultEnum> clazz = relEnum.value();
				IBaseResultEnum[] enums = clazz.getEnumConstants();
				List<String> values = new ArrayList<>();
				for (IBaseResultEnum obj : enums) {
					values.add(obj.getCode() + "=" + obj.getMsg());
				}
				AllowableListValues AllowableValues = new AllowableListValues(values, "");
				context.getBuilder().allowableValues(AllowableValues);
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	}

	@Override
	public boolean supports(DocumentationType delimiter) {
		return true;
	}

}
