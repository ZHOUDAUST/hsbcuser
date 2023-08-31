package com.hsbc.user.annotation;

import lombok.Getter;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义url匹配逻辑
 */
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {

    private final static Pattern VERSION_PREFIX_PATTERN = Pattern.compile(".*v(\\d+).*");

    @Getter
    private int apiVersion;

    ApiVersionCondition(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public ApiVersionCondition combine(ApiVersionCondition apiVersionCondition) {
//        当方法级别和类级别都有ApiVersion注解时，二者将进行合并（ApiVersionRequestCondition.combine）。最终将提取请求URL中版本号，与注解上定义的版本号进行比对，判断url是否符合版本要求
        return new ApiVersionCondition(apiVersionCondition.getApiVersion());
    }

    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        Matcher m = VERSION_PREFIX_PATTERN.matcher(request.getRequestURI());
        if (m.find()) {
            Integer version = Integer.valueOf(m.group(1));
            if (version >= this.apiVersion) {
                return this;
            }
        }
        return null;
    }

    @Override
    public int compareTo(ApiVersionCondition apiVersionCondition, HttpServletRequest request) {
        //优先匹配最新的版本号
        return apiVersionCondition.getApiVersion() - this.apiVersion;
    }
}
