package com.ey.filter.csrf.exception;

import com.ey.filter.csrf.CsrfToken;

public class InvalidCsrfTokenException extends RuntimeException {
    private static final long serialVersionUID = 1751191018361209174L;

    public InvalidCsrfTokenException(CsrfToken expectedAccessToken, String actualAccessToken) {
        super("Invalid CSRF Token '" + actualAccessToken + "' was found on the request parameter '"
                        + expectedAccessToken.getParameterName() + "' or header '" + expectedAccessToken.getHeaderName()
                        + "'.");
    }
}
