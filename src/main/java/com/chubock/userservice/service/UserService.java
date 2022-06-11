package com.chubock.userservice.service;

import com.chubock.userservice.entity.LocalUser;
import com.chubock.userservice.entity.OAuthUser;
import com.chubock.userservice.entity.User;
import com.chubock.userservice.model.LocalUserModel;
import com.chubock.userservice.model.OAuthUserModel;
import com.chubock.userservice.model.UserModel;
import com.chubock.userservice.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found for username " + username));
    }


    @Transactional(readOnly = true)
    public UserModel getProfile(String id) {

        User user = userRepository.findById(id)
                .orElseThrow();

        if (user instanceof OAuthUser)
            return OAuthUserModel.of((OAuthUser)user);
        else if (user instanceof LocalUser)
            return LocalUserModel.of((LocalUser)user);
        else
            return UserModel.of(user);

    }

    @Transactional(readOnly = true)
    public UserModel getUser(String id) {

        User user = userRepository.findById(id)
                .orElseThrow();

        if (user instanceof OAuthUser)
            return of((OAuthUser)user);
        else if (user instanceof LocalUser)
            return of((LocalUser)user);
        else
            return UserModel.of(user);

    }

    @Transactional
    public UserModel update(UserModel model) {

        User user = userRepository.findById(model.getId())
                .orElseThrow(() -> new IllegalArgumentException("error.user.notFound"));

        user.setNickname(model.getUsername());
        user.setFullName(model.getFullName());
        user.setImageUrl(model.getImageUrl());

        return getProfile(user.getId());

    }

    public static LocalUserModel of (LocalUser user) {
        return LocalUserModel.builder()
                .id(user.getId())
                .username(user.getNickname() == null ? user.getEmail() : user.getNickname())
                .fullName(user.getFullName())
                .imageUrl(user.getImageUrl())
                .build();
    }

    public static OAuthUserModel of (OAuthUser user) {
        return OAuthUserModel.builder()
                .id(user.getId())
                .username(user.getNickname() == null ? user.getLogin() : user.getNickname())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .imageUrl(user.getImageUrl())
                .build();
    }

    public UserModel blockUser(Authentication authentication, String blockedUserId) {
        User user = userRepository.findById(authentication.getName()).orElseThrow();
        if(user.getBlockedUsersId() == null)
            user.setBlockedUsersId(new ArrayList<>());

        user.getBlockedUsersId().add(blockedUserId);
        user = userRepository.save(user);
        return UserModel.of(user);
    }
}
