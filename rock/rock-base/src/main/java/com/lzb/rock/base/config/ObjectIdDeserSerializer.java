package com.lzb.rock.base.config;

import java.lang.reflect.Type;

import org.bson.types.ObjectId;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.lzb.rock.base.util.UtilString;

/**
 * fastjson objectId 输出转换
 * 
 * @author lzb
 * @date 2020年8月18日上午11:36:50
 */
public class ObjectIdDeserSerializer implements ObjectDeserializer {

	public final static ObjectDeserializer instance = new ObjectIdDeserSerializer();

	@SuppressWarnings("unchecked")
	@Override
	public ObjectId deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {

		if (parser != null && clazz == ObjectId.class) {
			Object value = parser.parse();
			if (value == null || UtilString.isBlank(value.toString())
					|| "NULL".equals(value.toString().toUpperCase())) {
				return null;
			}
			return new ObjectId(value.toString());
		}
		return null;
	}

	@Override
	public int getFastMatchToken() {
		return 0;
	}

}
