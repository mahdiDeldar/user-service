package com.chubock.userservice.endpoint;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "chat-service")
public interface ChatApiFeignClient {
    @GetMapping("/api/v1/profile/terminateUser/{id}")
    @Headers("Content-Type: application/json")
    ResponseEntity<Boolean> deleteChat(@PathVariable(value = "id") String id);
}
