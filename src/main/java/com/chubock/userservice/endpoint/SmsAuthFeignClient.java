package com.chubock.userservice.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sms-auth", url = "${app.sms.authHost}")
public interface SmsAuthFeignClient {

    @PostMapping("/oauth/token")
    TokenResponse getToken(@RequestBody TokenRequest request);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class TokenRequest {
        @JsonProperty("client_id")
        private String clientId;
        @JsonProperty("secret")
        private String clientSecret;
    }

    @Data
    class TokenResponse {
        private String jwt;
        @JsonProperty("token_type")
        private String tokenType;
        private Integer expires;
        private String type;
    }

}
