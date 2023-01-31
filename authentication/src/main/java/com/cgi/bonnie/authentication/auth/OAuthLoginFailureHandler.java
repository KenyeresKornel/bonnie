package com.cgi.bonnie.authentication.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuthLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        var oauthSuccessCookie = new Cookie("oauthSuccess", "false");
        oauthSuccessCookie.setMaxAge(-1);
        oauthSuccessCookie.setPath("/");
        response.addCookie(oauthSuccessCookie);
        super.onAuthenticationFailure(request, response, exception);
    }

}
