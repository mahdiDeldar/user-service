package com.chubock.userservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@SuperBuilder(toBuilder = true)
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String nickname;
    private String fullName;

    @Builder.Default
    private boolean enabled = true;

    private String imageUrl;

    @Override
    @Transient
    public String getUsername() {
        return getId();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

}
