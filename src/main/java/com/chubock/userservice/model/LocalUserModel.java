package com.chubock.userservice.model;

import com.chubock.userservice.entity.LocalUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class LocalUserModel extends UserModel {

    private String email;
    private String phone;
    private boolean emailVerified;

    public static LocalUserModel of (LocalUser user) {
        return builder()
                .id(user.getId())
                .username(user.getNickname())
                .fullName(user.getFullName())
                .imageUrl(user.getImageUrl())
                .email(user.getEmail())
                .phone(user.getPhone())
                .emailVerified(user.isEmailVerified())
                .blockedUsersId(user.getBlockedUsersId())
                .build();

    }
}
