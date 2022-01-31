package com.chubock.userservice.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "sms-api", url = "${app.sms.apiHost}")
public interface SmsApiFeignClient {

    @PostMapping("/sms/send")
    String sendMessage(@RequestHeader(name = "Authorization") String authorizationHeader, @RequestBody SendSmsRequest request);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SendSmsRequest {
        private String to;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SendSmsResponse {
        private String message;
        private String success;
        @JsonProperty("message_id")
        private String messageId;
    }

}
