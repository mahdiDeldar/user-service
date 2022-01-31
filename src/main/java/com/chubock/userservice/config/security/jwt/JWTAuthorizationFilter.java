package com.chubock.userservice.config.security.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.chubock.userservice.component.JWTTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.chubock.userservice.component.JWTTokenUtil.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JWTTokenUtil jwtTokenUtil;

    public JWTAuthorizationFilter(AuthenticationManager authManager, JWTTokenUtil jwtTokenUtil) {
        super(authManager);
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.replace(TOKEN_PREFIX, "");

        UsernamePasswordAuthenticationToken authentication = getAuthentication(token);

        if (authentication != null)
            SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, res);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {

        DecodedJWT decodedJWT = jwtTokenUtil.decode(token);

        if (!Arrays.asList(decodedJWT.getClaim(SCOPES_CLAIM_NAME).asArray(String.class)).contains(AUTH_TOKEN_SCOPE))
            return null;

        String user = decodedJWT
                .getSubject();

        List<GrantedAuthority> authorities = new ArrayList<>();

        //noinspection unchecked
        decodedJWT.getClaim(AUTHORITIES_CLAIM_NAME)
                .as(List.class)
                .forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority.toString())));

        if (user != null)
            return new UsernamePasswordAuthenticationToken(user, null, authorities);

        return null;
    }

}
