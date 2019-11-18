package com.ey.filter.csrf;

import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * 跨域安全请求url匹配.
 */
public class CsrfSecurityRequestMatcher {
    private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

    private PathMatcher matcher = new AntPathMatcher();

    /**
     * 需要排除的url列表
     */
    private List<String> excludeUrls;

    public List<String> getExcludeUrls() {
        return excludeUrls;
    }

    public void setExcludeUrls(List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    public boolean matches(HttpServletRequest httpServletRequest) {
        boolean isFilter=true;
        boolean isPostMethod=!allowedMethods.matcher(httpServletRequest.getMethod()).matches();
        if(isPostMethod){
            if (excludeUrls != null && excludeUrls.size() > 0) {
                String servletPath = httpServletRequest.getServletPath();
                for (String url : excludeUrls) {
                    if (matcher.match(url, servletPath)) {
                        isFilter= false;
                    }
                }
            }
        }else {
            isFilter= false;
        }
        return isFilter;
    }
}
