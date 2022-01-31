package com.chubock.userservice.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    private String tokenSecret;
    private long accessTokenExpirationMilliseconds;
    private long refreshTokenExpirationMilliseconds;
    private List<String> authorizedRedirectUris = new ArrayList<>();

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public long getAccessTokenExpirationMilliseconds() {
        return accessTokenExpirationMilliseconds;
    }

    public void setAccessTokenExpirationMilliseconds(long accessTokenExpirationMilliseconds) {
        this.accessTokenExpirationMilliseconds = accessTokenExpirationMilliseconds;
    }

    public long getRefreshTokenExpirationMilliseconds() {
        return refreshTokenExpirationMilliseconds;
    }

    public void setRefreshTokenExpirationMilliseconds(long refreshTokenExpirationMilliseconds) {
        this.refreshTokenExpirationMilliseconds = refreshTokenExpirationMilliseconds;
    }

    public List<String> getAuthorizedRedirectUris() {
        return authorizedRedirectUris;
    }

    public void setAuthorizedRedirectUris(List<String> authorizedRedirectUris) {
        this.authorizedRedirectUris = authorizedRedirectUris;
    }
}
