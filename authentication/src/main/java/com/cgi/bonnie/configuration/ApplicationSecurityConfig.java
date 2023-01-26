package com.cgi.bonnie.configuration;

import com.cgi.bonnie.authentication.auth.ApplicationUserService;
import com.cgi.bonnie.authentication.auth.OAuthLoginSuccessHandler;
import com.cgi.bonnie.authentication.security.oauth2.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static com.cgi.bonnie.authentication.security.ApplicationUserRole.ASSEMBLER;


@Configuration
@ComponentScan(basePackages = {"com.cgi.bonnie.authentication"})
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${bonnie.authentication.cors.allowed.origins}")
    private String corsAllowedOrigins;

    @Value("${bonnie.authentication.oauth.redirect.url}")
    private String oauthLoginSuccessUrl;

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;

    private final CustomOAuth2UserService oAuth2UserService;

    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder,
                                     ApplicationUserService applicationUserService,
                                     CustomOAuth2UserService oAuth2UserService,
                                     OAuthLoginSuccessHandler oAuthLoginSuccessHandler) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
        this.oAuth2UserService = oAuth2UserService;
        this.oAuthLoginSuccessHandler = oAuthLoginSuccessHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        oAuthLoginSuccessHandler.setDefaultTargetUrl(oauthLoginSuccessUrl);

        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/logout").permitAll()
                .antMatchers("/api/**").hasAnyRole(ASSEMBLER.name(), "USER")
                .anyRequest().authenticated()
                .and()
                    .oauth2Login()
                    .defaultSuccessUrl(oauthLoginSuccessUrl, true)
                    .userInfoEndpoint()
                    .userService(oAuth2UserService)
                    .and()
                    .successHandler(oAuthLoginSuccessHandler)
                .and()
                .logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/login-form");/*
                .and()
                    .rememberMe()
                    .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
                    .key("somethingverysecured")
                    .rememberMeParameter("remember-me")
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .logoutSuccessUrl("/login");*/
        //TODO: enable after nginx configuration
        /*         http
                    .oauth2Login()
                    .defaultSuccessUrl(oauthLoginSuccessUrl, true)
                    .userInfoEndpoint()
                    .userService(oAuth2UserService)
                .and()
                   .successHandler(oAuthLoginSuccessHandler);
        */
        http.cors().configurationSource(corsConfigurationSource());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider()).userDetailsService(applicationUserService);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public SecurityContextRepository getSecurityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList(corsAllowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
