package com.chubock.userservice.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "local_users")
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class LocalUser extends User {

    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String phone;

    private String emailVerificationCode;
    private boolean emailVerified;

    @Override
    public String getPassword() {
        return password;
    }

}
