package com.chubock.userservice.model;

import com.chubock.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class UserModel {
    public UserModel(String id) {
        this.id = id;
    }

    private String id;

    private String username;
    private String fullName;
    private String imageUrl;

    public static UserModel of(User user) {
        return builder()
                .id(user.getId())
                .username(user.getNickname())
                .fullName(user.getFullName())
                .imageUrl(user.getImageUrl())
                .build();
    }

}
