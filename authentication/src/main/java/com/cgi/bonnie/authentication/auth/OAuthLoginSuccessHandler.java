package com.cgi.bonnie.authentication.auth;

import com.cgi.bonnie.authentication.security.oauth2.CustomOAuth2User;
import com.cgi.bonnie.businessrules.Role;
import com.cgi.bonnie.businessrules.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuthLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        var principal = authentication.getPrincipal();
        if (principal instanceof CustomOAuth2User customOauth2User) {
            var email = customOauth2User.getEmail();
            if (userService.findByEmail(email) == null) {
                userService.createUser(customOauth2User.getName(), customOauth2User.getEmail(), null, Role.ASSEMBLER);
            }
        }
        // TODO have an oauth failure handler to unset this
        var oauthSuccessCookie = new Cookie("oauthSuccess", "true");
        oauthSuccessCookie.setMaxAge(-1);
        oauthSuccessCookie.setPath("/");
        response.addCookie(oauthSuccessCookie);
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
