package com.chubock.userservice.service;

import com.chubock.userservice.component.JsonConverter;
import com.chubock.userservice.config.security.oauth.OAuth2AuthenticationProcessingException;
import com.chubock.userservice.entity.OAuthProvider;
import com.chubock.userservice.entity.OAuthUser;
import com.chubock.userservice.model.OAuthUserModel;
import com.chubock.userservice.repository.OAuthUserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class OAuthUserService extends DefaultOAuth2UserService {

    private static final int OTP_TOKEN_LENGTH = 16;

    private final JsonConverter jsonConverter;

    private final OAuthUserRepository userRepository;

    public OAuthUserService(JsonConverter jsonConverter, OAuthUserRepository userRepository) {
        this.jsonConverter = jsonConverter;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }

    }

    private OAuthUserModel processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {

        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthProvider provider = getOAuthProvider(registrationId);

        OAuthUserModel oAuthUserModel = getOAuthUserModel(provider, attributes);

        OAuthUser user = userRepository.findByProviderIdAndProvider(oAuthUserModel.getProviderId(), provider)
                .map(oAuthUser -> {

                    oAuthUser.setLogin(oAuthUserModel.getLogin());
                    oAuthUser.setEmail(oAuthUserModel.getEmail());
                    oAuthUser.setImageUrl(oAuthUserModel.getImageUrl());
                    return oAuthUser;

                }).orElseGet(() -> OAuthUser.builder()
                        .login(oAuthUserModel.getLogin())
                        .email(oAuthUserModel.getEmail())
                        .provider(provider)
                        .providerId(oAuthUserModel.getProviderId())
                        .fullName(oAuthUserModel.getFullName())
                        .imageUrl(oAuthUserModel.getImageUrl())
                        .build());

        user.setOtpToken(RandomStringUtils.randomAlphanumeric(OTP_TOKEN_LENGTH));
        user.setAttempt(0);
        user.setAttributes(jsonConverter.toJson(attributes));

        userRepository.save(user);

        OAuthUserModel model = OAuthUserModel.of(user);

        model.setUsername(user.getId());

        return model;
    }

    private OAuthUserModel getOAuthUserModel(OAuthProvider provider, Map<String, Object> attributes) {

        switch (provider) {
            case GITHUB:
                return github(attributes);
            case GOOGLE:
                return google(attributes);
            case FACEBOOK:
                return facebook(attributes);
            default:
                throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + provider + " is not supported yet.");
        }

    }

    private OAuthUserModel google(Map<String, Object> attributes) {

        return OAuthUserModel.builder()
                .login((String) attributes.get("email"))
                .email((String) attributes.get("email"))
                .providerId(String.valueOf(attributes.get("sub")))
                .fullName((String) attributes.get("name"))
                .imageUrl((String) attributes.get("picture"))
                .attributes(attributes)
                .build();


    }

    @SuppressWarnings("unchecked")
    private OAuthUserModel facebook(Map<String, Object> attributes) {

        String imageUrl = null;

        if(attributes.containsKey("picture")) {
            Map<String, Object> pictureObj = (Map<String, Object>) attributes.get("picture");
            if(pictureObj.containsKey("data")) {
                Map<String, Object>  dataObj = (Map<String, Object>) pictureObj.get("data");
                if(dataObj.containsKey("url")) {
                    imageUrl = (String) dataObj.get("url");
                }
            }
        }

        return OAuthUserModel.builder()
                .login((String) attributes.get("email"))
                .email((String) attributes.get("email"))
                .providerId(String.valueOf(attributes.get("id")))
                .fullName((String) attributes.get("name"))
                .imageUrl(imageUrl)
                .attributes(attributes)
                .build();


    }

    private OAuthUserModel github(Map<String, Object> attributes) {

        return OAuthUserModel.builder()
                .login((String) attributes.get("login"))
                .providerId(String.valueOf(attributes.get("id")))
                .fullName((String) attributes.get("name"))
                .imageUrl((String) attributes.get("avatar_url"))
                .attributes(attributes)
                .build();

    }

    private OAuthProvider getOAuthProvider(String registrationId) {
        return OAuthProvider.valueOf(registrationId.toUpperCase());
    }


}
