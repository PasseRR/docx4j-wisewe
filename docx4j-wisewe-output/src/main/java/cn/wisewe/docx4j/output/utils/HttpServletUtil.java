package cn.wisewe.docx4j.output.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http servlet工具
 * @author xiehai
 * @date 2020/12/15 18:53
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public interface HttpServletUtil {
    /**
     * 获取mvc中线程变量{@link org.springframework.web.context.request.RequestAttributes}
     * @return {@link ServletRequestAttributes}
     */
    static ServletRequestAttributes getCurrentRequestAttributes() {
        return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()));
    }

    /**
     * 获得当前线程中的{@link HttpServletRequest}
     * @return {@link HttpServletRequest}
     */
    static HttpServletRequest getCurrentRequest() {
        return getCurrentRequestAttributes().getRequest();
    }

    /**
     * 获得当前线程中的{@link HttpServletResponse}
     * @return {@link HttpServletResponse}
     */
    static HttpServletResponse getCurrentResponse() {
        return getCurrentRequestAttributes().getResponse();
    }
}
