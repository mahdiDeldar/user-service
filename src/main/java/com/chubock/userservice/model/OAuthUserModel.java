package com.chubock.userservice.model;


import com.chubock.userservice.entity.OAuthProvider;
import com.chubock.userservice.entity.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class OAuthUserModel extends UserModel implements OAuth2User, UserDetails {

    @NotEmpty
    private String login;
    private String email;
    private String providerId;
    private OAuthProvider provider;
    private Map<String, Object> attributes;

    private String otpToken;

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword() {
        return otpToken;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean isEnabled() {
        return true;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getName() {
        return getId();
    }

    public static OAuthUserModel of (OAuthUser user) {
        return builder()
                .id(user.getId())
                .login(user.getLogin())
                .email(user.getEmail())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .username(user.getNickname())
                .fullName(user.getFullName())
                .imageUrl(user.getImageUrl())
                .otpToken(user.getOtpToken())
                .build();
    }
}
