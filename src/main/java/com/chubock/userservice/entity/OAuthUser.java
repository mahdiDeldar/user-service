package com.chubock.userservice.entity;

import com.chubock.userservice.model.LocalUserModel;
import com.chubock.userservice.model.OAuthUserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "oauth_users")
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class OAuthUser extends User {

    @NotNull
    @Enumerated(EnumType.STRING)
    private OAuthProvider provider;

    @NotEmpty
    private String login;
    private String email;
    private String providerId;
    private String attributes;

    private String otpToken;
    private int attempt;

    public void increaseAttempt() {
        attempt++;
    }


}
