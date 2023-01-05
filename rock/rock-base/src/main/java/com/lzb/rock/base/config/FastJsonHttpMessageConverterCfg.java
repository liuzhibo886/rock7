package com.lzb.rock.base.config;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.util.TypeUtils;

/**
 * FastJsonHttpMessageConverter 配置公共类，所有方法采用同一个配置
 * https://blog.csdn.net/u010246789/article/details/52539576
 * 
 * @author lzb
 *
 *         2018年11月8日 上午8:51:11 此类不能用spring 实例化，不生效，问题待研究
 */
public class FastJsonHttpMessageConverterCfg {

	private FastJsonHttpMessageConverterCfg() {
	};

	private static FastJsonHttpMessageConverter fastJsonHttpMessageConverter;

	/**
	 * 暂时未写单列 等后期项目新开的时候可以考虑 每次JSON的时候都会调用，，需要考虑单列
	 * 
	 * @return
	 */
	public static FastJsonHttpMessageConverter getInstance() {
		if (fastJsonHttpMessageConverter != null) {
			return fastJsonHttpMessageConverter;
		}
		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
		// 序列化值
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		/**
		 * 此处设置日期格式化，会让JSONField注解失效 fastJsonConfig.setDateFormat("yyyy-MM-dd
		 * HH:mm:ss");
		 */
		List<SerializerFeature> serializerFeaturesList = new ArrayList<>();
		/**
		 * 结果是否格式化
		 */
		// serializerFeaturesList.add(SerializerFeature.PrettyFormat);
		/**
		 * 消除对同一对象循环引用的问题，默认为false
		 */
		serializerFeaturesList.add(SerializerFeature.DisableCircularReferenceDetect);
		/**
		 * 输出去null值
		 */
		serializerFeaturesList.add(SerializerFeature.WriteMapNullValue);
		/**
		 * List字段如果为null,输出为[],而非null
		 */
		serializerFeaturesList.add(SerializerFeature.WriteNullListAsEmpty);

		/**
		 * 空值输出为空串
		 */
//		serializerFeaturesList.add(SerializerFeature.WriteNullStringAsEmpty);
		/**
		 * Boolean字段如果为null,输出为false,而非null
		 */
		serializerFeaturesList.add(SerializerFeature.WriteNullBooleanAsFalse);
		/**
		 * 设置日期格式化 yyyy-MM-dd HH:mm:ss
		 */
		serializerFeaturesList.add(SerializerFeature.WriteDateUseDateFormat);

		/**
		 * 序列化输出类信息
		 */
//		serializerFeaturesList.add(SerializerFeature.WriteClassName);

		/**
		 * 设置市区格式 yyyy-MM-ddTHH:mm:ss.SSS+08:00
		 */
		// serializerFeaturesList.add(SerializerFeature.UseISO8601DateFormat);

		SerializerFeature[] serializerFeatures = new SerializerFeature[serializerFeaturesList.size()];

		serializerFeaturesList.toArray(serializerFeatures);
		fastJsonConfig.setSerializerFeatures(serializerFeatures);
		/**
		 * 处理首字母大小写问题
		 */
//		 TypeUtils.compatibleWithJavaBean = true;
		/**
		 * 处理中文乱码问题
		 */

		List<MediaType> fastMediaTypes = new ArrayList<>();
		// fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		fastMediaTypes.add(MediaType.APPLICATION_JSON);
		fastMediaTypes.add(MediaType.TEXT_HTML);
		fastConverter.setSupportedMediaTypes(fastMediaTypes);

		/**
		 * 处理objectId 输出问题
		 */
		SerializeConfig serializeConfig = SerializeConfig.getGlobalInstance();
		serializeConfig.put(ObjectId.class, ObjectIdSerializer.instance);

		ParserConfig parserConfig = ParserConfig.getGlobalInstance();
		parserConfig.putDeserializer(ObjectId.class, ObjectIdDeserSerializer.instance);

		fastJsonConfig.setSerializeConfig(serializeConfig);
		fastJsonConfig.setParserConfig(parserConfig);

		fastConverter.setFastJsonConfig(fastJsonConfig);

		// ParserConfig.deserializers

		fastJsonHttpMessageConverter = fastConverter;
		return fastJsonHttpMessageConverter;

	}

	public static void main(String[] args) {
		FastJsonHttpMessageConverterCfg.getInstance();
	}
}
