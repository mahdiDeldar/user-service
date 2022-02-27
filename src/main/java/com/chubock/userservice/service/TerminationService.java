package com.chubock.userservice.service;

import com.chubock.userservice.endpoint.ChatApiFeignClient;
import com.chubock.userservice.endpoint.PropertyApiFeignClient;
import com.chubock.userservice.entity.LocalUser;
import com.chubock.userservice.entity.OAuthUser;
import com.chubock.userservice.entity.User;
import com.chubock.userservice.model.UserModel;
import com.chubock.userservice.repository.LocalUserRepository;
import com.chubock.userservice.repository.OAuthUserRepository;
import com.chubock.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TerminationService {
    private UserRepository userRepository;
    private LocalUserRepository localUserRepository;
    private OAuthUserRepository oAuthUserRepository;
    private PropertyApiFeignClient propertyApiFeignClient;
    private ChatApiFeignClient chatApiFeignClient;

    @Transactional
    public void userTermination(String userId) {
        User user = userRepository.findById(userId).get();
        propertyApiFeignClient.deleteProperty(userId);
        chatApiFeignClient.deleteChat(userId);
        if (user instanceof LocalUser) {
            localUserRepository.delete(localUserRepository.findById(userId).get());
        }
        if (user instanceof OAuthUser) {
            oAuthUserRepository.delete(oAuthUserRepository.findById(userId).get());
        }
        userRepository.delete(user);
    }
}
