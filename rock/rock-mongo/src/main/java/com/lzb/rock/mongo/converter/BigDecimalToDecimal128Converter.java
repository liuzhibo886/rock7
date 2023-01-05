/**
 * 
 */
package com.lzb.rock.mongo.converter;

/**
 * @author lzb
 *	@date  2020-8-1323:45:22
 */
import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
 
import java.math.BigDecimal;
 
@ReadingConverter
@WritingConverter
public class BigDecimalToDecimal128Converter implements Converter<BigDecimal, Decimal128> {
	 
    public Decimal128 convert(BigDecimal bigDecimal) {
        return new Decimal128(bigDecimal);
    }
    
}
