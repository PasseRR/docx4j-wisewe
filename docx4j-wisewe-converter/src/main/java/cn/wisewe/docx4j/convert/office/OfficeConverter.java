package cn.wisewe.docx4j.convert.office;

import cn.wisewe.docx4j.convert.ConvertException;
import cn.wisewe.docx4j.convert.base.AbstractConverter;

import javax.annotation.Generated;
import java.util.function.Function;


/**
 * office文件转换器
 * @author xiehai
 * @date 2022/03/25 12:48
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
@Generated({})
public abstract class OfficeConverter<T extends OfficeConverter<?, U>, U extends OfficeFileHandler> extends AbstractConverter<T, U> {
    protected OfficeConverter(
        Function<String, ? extends ConvertException> messageConvertExceptionFunction,
        Function<Exception, ? extends ConvertException> exceptionConvertExceptionFunction) {
        super(messageConvertExceptionFunction, exceptionConvertExceptionFunction);
    }
}
