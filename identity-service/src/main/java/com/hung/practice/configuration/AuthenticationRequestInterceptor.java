package com.hung.practice.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class AuthenticationRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String authorization = requestAttributes.getRequest().getHeader("Authorization");
        log.info("Header {}: " + authorization);

        if(StringUtils.hasText(authorization)) {
            requestTemplate.header("Authorization", authorization);
        }
    }
}
