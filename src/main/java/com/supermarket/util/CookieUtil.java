package com.supermarket.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private static final String EMAIL_COOKIE = "remembered_email";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7 days

    public void rememberEmail(HttpServletResponse response, String email) {
        Cookie cookie = new Cookie(EMAIL_COOKIE, email);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void clearEmail(HttpServletResponse response) {
        Cookie cookie = new Cookie(EMAIL_COOKIE, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public String getRememberedEmail(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (EMAIL_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }
}
