package com.tecnalia.wicket.security;

import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CookieUtils {

    public static final int REMEMBER_ME_DURATION_IN_DAYS = 30;
    public static final String REMEMBER_ME_LOGIN_COOKIE = "ecotoolLoginCookie";
    public static final String REMEMBER_ME_PASSWORD_COOKIE = "ecotoolPasswordCookie";
	
    public static Cookie loadCookie(Request request, String cookieName) {        
    	List<Cookie> cookies = ((WebRequest) request).getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(cookieName)) {
                return cookie;
            }
        }

        return null;
    }

    public static void saveCookie(Response response, String cookieName, String cookieValue, int expiryTimeInDays) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(expiryTimeInDays));
        cookie.setPath("/");
        ((WebResponse)response).addCookie(cookie);
    }

    public static void removeCookieIfPresent(Request request, Response response, String cookieName) {
        Cookie cookie = loadCookie(request, cookieName);

        if(cookie != null) {
            ((WebResponse)response).clearCookie(cookie);
        }
	}
	
}
