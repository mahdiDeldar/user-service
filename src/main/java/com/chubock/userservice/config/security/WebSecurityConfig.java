package com.chubock.userservice.config.security;

import com.chubock.userservice.component.JWTTokenUtil;
import com.chubock.userservice.config.security.jwt.JWTAuthenticationFilter;
import com.chubock.userservice.config.security.jwt.JWTAuthorizationFilter;
import com.chubock.userservice.config.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.chubock.userservice.config.security.oauth.OAuth2AuthenticationFailureHandler;
import com.chubock.userservice.config.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.chubock.userservice.config.security.provider.EmailPasswordAuthenticationProvider;
import com.chubock.userservice.config.security.provider.OAuthOtpAuthenticationProvider;
import com.chubock.userservice.config.security.provider.PhoneOtpAuthenticationProvider;
import com.chubock.userservice.service.OAuthUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JWTTokenUtil jwtTokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PhoneOtpAuthenticationProvider phoneOtpAuthenticationProvider;
    @Autowired

    private EmailPasswordAuthenticationProvider emailPasswordAuthenticationProvider;
    @Autowired

    private OAuthOtpAuthenticationProvider oAuthOtpAuthenticationProvider;

    @Autowired
    private OAuthUserService oAuthUserService;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .httpBasic()
                .disable()
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), new AntPathRequestMatcher("/**"))
                .and()
                .authorizeRequests()
                .antMatchers("/",
                        "/error",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/config",
                        "/verification",
                        "/register/**",
                        "/login",
                        "/auth/**",
                        "/oauth2/**",
                        "/refresh",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(oAuthUserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtTokenUtil, objectMapper))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtTokenUtil));

    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(phoneOtpAuthenticationProvider)
                .authenticationProvider(emailPasswordAuthenticationProvider)
                .authenticationProvider(oAuthOtpAuthenticationProvider);
    }

    /*
     By default, Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save
     the authorization request. But, since our service is stateless, we can't save it in
     the session. We'll save the request in a Base64 encoded cookie instead.
   */

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
