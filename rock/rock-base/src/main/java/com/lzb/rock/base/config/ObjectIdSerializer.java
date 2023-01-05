package com.lzb.rock.base.config;

import java.io.IOException;
import java.lang.reflect.Type;

import org.bson.types.ObjectId;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.lzb.rock.base.util.UtilString;

/**
 * fastjson objectId 输出转换
 * 
 * @author lzb
 * @date 2020年8月18日上午11:36:50
 */
public class ObjectIdSerializer implements ObjectSerializer {

	public final static ObjectIdSerializer instance = new ObjectIdSerializer();

	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
			throws IOException {
		SerializeWriter out = serializer.out;

		if (fieldType == ObjectId.class) {
			if (object != null && UtilString.isNotBlank(object.toString())
					&& !"NULL".equals(object.toString().toUpperCase())) {
				out.writeString(object.toString());
			} else {
				out.writeNull();
			}
			return;
		}
		if (object != null || object instanceof ObjectId) {
			out.writeString(object.toString());
			return;
		}
	}

}
