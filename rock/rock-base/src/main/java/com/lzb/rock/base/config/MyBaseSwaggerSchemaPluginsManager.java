package com.lzb.rock.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;

import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.SyntheticModelProviderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

/**
 * 重写Swagger 支持ApiEnum 注解
 * 
 * @author lzb
 * @date 2020年7月29日下午3:19:07
 */
@Component
@Primary
//@ConditionalOnBean(MyBaseSwaggerApiModelPropertyPropertyBuilder.class)
@ConditionalOnBean(PluginRegistry.class)
public class MyBaseSwaggerSchemaPluginsManager extends SchemaPluginsManager {
	private final PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propertyEnrichers;

	@Autowired
	MyBaseSwaggerApiModelPropertyPropertyBuilder myApiModelPropertyPropertyBuilder;

	public MyBaseSwaggerSchemaPluginsManager(
			@Qualifier("modelPropertyBuilderPluginRegistry") PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propertyEnrichers,
			@Qualifier("modelBuilderPluginRegistry") PluginRegistry<ModelBuilderPlugin, DocumentationType> modelEnrichers,
			@Qualifier("syntheticModelProviderPluginRegistry") PluginRegistry<SyntheticModelProviderPlugin, ModelContext> syntheticModelProviders) {
		super(propertyEnrichers, modelEnrichers, syntheticModelProviders);
		this.propertyEnrichers = propertyEnrichers;
	}

	public ModelProperty property(ModelPropertyContext context) {
		for (ModelPropertyBuilderPlugin enricher : this.propertyEnrichers
				.getPluginsFor(context.getDocumentationType())) {
			enricher.apply(context);
			// log.info("enricher:{}", enricher.getClass());
		}
		myApiModelPropertyPropertyBuilder.apply(context);
		ModelProperty modelProperty = context.getBuilder().build();
		return modelProperty;
	}

}
