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

import com.lzb.rock.base.util.UtilString;


@ReadingConverter
@WritingConverter
public class DecimalStringToObjectIdConverter implements Converter<String, ObjectId> {

	@Override
	public ObjectId convert(String source) {
		if(UtilString.isBlank(source)) {
			return null;
		}
		return new ObjectId(source);
	}



}

