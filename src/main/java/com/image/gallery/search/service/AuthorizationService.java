package com.image.gallery.search.service;

import com.image.gallery.search.domain.dto.AuthTokenDto;
import com.image.gallery.search.exception.InvalidAuthTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
class AuthorizationService {
    @Value("${interview.agile.engine.apiKey}")
    private String apiKey;

    @Value("${interview.agile.engine.url}")
    private String baseUrl;

    private final static String AUTH_PATH = "/auth";

    private RestTemplate restTemplate = new RestTemplate();

    String renewToken() throws InvalidAuthTokenException {
        String url = baseUrl + AUTH_PATH;

        Map<String, String> body = new HashMap<>();
        body.put("apiKey", apiKey);

        AuthTokenDto tokenDto = this.restTemplate.postForEntity(url, body, AuthTokenDto.class).getBody();
        if (tokenDto != null && tokenDto.isAuth()) return tokenDto.getToken();
        else throw new InvalidAuthTokenException("Auth token is empty. Use valid api key");
    }
}
