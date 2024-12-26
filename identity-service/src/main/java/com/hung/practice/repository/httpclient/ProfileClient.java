package com.hung.practice.repository.httpclient;

import com.hung.practice.configuration.AuthenticationRequestInterceptor;
import com.hung.practice.dto.request.ProfileCreationRequest;
import com.hung.practice.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "profile-service",
            configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {
    @PostMapping(value = "/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
    UserProfileResponse createProfile(
            @RequestBody ProfileCreationRequest request);
}
