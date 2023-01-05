/**
 * 
 */
package com.lzb.rock.mongo.converter;

/**
 * @author lzb
 *	@date  2020-8-1323:45:46
 */

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;


@ReadingConverter
@WritingConverter
public class DecimalObjectIdToStringConverter implements Converter<ObjectId, String> {

	@Override
	public String convert(ObjectId source) {
		return source.toString();
	}


}

