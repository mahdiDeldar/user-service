package com.chubock.userservice.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SmsEndpoint {

    private static final String AUTH_TOKEN_TYPE = "Bearer";
    private static final int ACCESS_TOKEN_ERROR_BACK_OFF_TIME = 30000;

    private final String clientId;
    private final String clientSecret;

    private final SmsApiFeignClient apiFeignClient;

    private String accessToken = null;


    public SmsEndpoint(@Value("${app.sms.clientId}") String clientId,
                       @Value("${app.sms.clientSecret}") String clientSecret,
                       SmsAuthFeignClient authFeignClient,
                       SmsApiFeignClient apiFeignClient) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.apiFeignClient = apiFeignClient;

        CompletableFuture.runAsync(() -> getAccessToken(authFeignClient));
    }

    private void getAccessToken(SmsAuthFeignClient authFeignClient) {
        while (true) {
            try {

                log.info("Trying to retrieve access token with clientId {} and clientSecret {} from sms.to", clientId, clientSecret);
                SmsAuthFeignClient.TokenRequest tokenRequest = new SmsAuthFeignClient.TokenRequest(clientId, clientSecret);
                SmsAuthFeignClient.TokenResponse tokenResponse = authFeignClient.getToken(tokenRequest);
                this.accessToken = AUTH_TOKEN_TYPE + " " + tokenResponse.getJwt();
                log.info("Retrieved access token from sms.to: {}", tokenResponse.getJwt());
                break;

            } catch (Exception e) {
                log.error("Error in getting access token from sms.to, retrying later...", e);
                try {
                    Thread.sleep(ACCESS_TOKEN_ERROR_BACK_OFF_TIME);
                } catch (InterruptedException ex) {
                    log.error("error in sleeping", ex);
                }
            }
        }
    }

    public void sendMessage(String receiver, String body) {

        log.info("sending message {} to receiver {}", receiver, body);

        if (accessToken == null) {
            log.error("error sending message {} to receiver {}, no access_token", receiver, body);
            return;
        }

        SmsApiFeignClient.SendSmsRequest sendSmsRequest = new SmsApiFeignClient.SendSmsRequest(receiver, body);
        String sendSmsResponse = apiFeignClient.sendMessage(accessToken, sendSmsRequest);

        log.info("message {} sent to receiver {} with response {}", body, receiver, sendSmsResponse);

    }


}
