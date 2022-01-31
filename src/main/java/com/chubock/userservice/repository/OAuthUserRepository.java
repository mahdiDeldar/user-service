package com.chubock.userservice.repository;

import com.chubock.userservice.entity.OAuthProvider;
import com.chubock.userservice.entity.OAuthUser;

import java.util.Optional;

public interface OAuthUserRepository extends AbstractUserRepository<OAuthUser> {

    Optional<OAuthUser> findByProviderIdAndProvider(String login, OAuthProvider provider);

}
