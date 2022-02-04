package com.chubock.userservice.endpoint;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "property-service")
public interface PropertyApiFeignClient {
    @GetMapping("/api/v1/users/me/terminateUser/{id}")
    @Headers("Content-Type: application/json")
    ResponseEntity<Boolean> deleteProperty(@PathVariable(value = "id") String id);
}
