package com.ey.filter.csrf;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ey.filter.csrf.exception.InvalidCsrfTokenException;
import com.ey.filter.csrf.exception.MissingCsrfTokenException;
import com.ey.util.UrlUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * csrf filter
 */
public final class CsrfFilter implements Filter {

    private final Log logger = LogFactory.getLog(getClass());
    private final CsrfTokenRepository tokenRepository;
    private final CsrfSecurityRequestMatcher requireCsrfProtectionMatcher;

    public CsrfFilter() {
        tokenRepository = new HttpSessionCsrfTokenRepository();
        requireCsrfProtectionMatcher = new CsrfSecurityRequestMatcher();
        requireCsrfProtectionMatcher.setExcludeUrls(Arrays.asList("/druid/*", "/login_login"));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
                    throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        request.setAttribute(HttpServletResponse.class.getName(), response);

        CsrfToken csrfToken = this.tokenRepository.loadToken(request);
        final boolean missingToken = csrfToken == null;
        if (missingToken) {
            csrfToken = this.tokenRepository.generateToken(request);
            this.tokenRepository.saveToken(csrfToken, request, response);
        }
        request.setAttribute(CsrfToken.class.getName(), csrfToken);
        request.setAttribute(csrfToken.getParameterName(), csrfToken);

        if (!this.requireCsrfProtectionMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String actualToken = request.getHeader(csrfToken.getHeaderName());
        if (actualToken == null) {
            actualToken = request.getParameter(csrfToken.getParameterName());
        }
        if (!csrfToken.getToken().equals(actualToken)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Invalid CSRF token found for " + UrlUtils.buildFullRequestUrl(request));
            }
            if (missingToken) {
                throw new MissingCsrfTokenException(actualToken);
            } else {
                throw new InvalidCsrfTokenException(csrfToken, actualToken);
            }
        }

        filterChain.doFilter(request, response);
    }

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

    @Override
    public void destroy() {

    }

}
